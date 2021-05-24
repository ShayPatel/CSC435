import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.*;


class Blockchain{

    public static void main(String[] args) {
        Node n1 = new Node(4821,4931);

        block b = utils.read_json("block.json");
        Gson gson = new Gson();
        String json = gson.toJson(b);

        send_command(json,"localhost",4821);
    }

    public static void send_command(String command, String server_name, int port){
        Socket skt;
        //admin client does not recieve messages, so don't need buffered reader
        PrintStream to_server;


        try{
            //open the connection to the admin server
            skt = new Socket(server_name, port);
            
            //Create the output stream to send to the server
            to_server = new PrintStream(skt.getOutputStream());

            //sending the command
            to_server.println(command);
            to_server.flush();

            skt.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

}

class utils{

    //borrowed from the class code
    //credit to:
    //https://www.quickprogrammingtips.com/java/how-to-generate-sha256-hash-in-java.html  @author JJ
    //https://dzone.com/articles/generate-random-alpha-numeric  by Kunal Bhatia  ·  Aug. 09, 12 · Java Zone
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
    }
    public static String ByteArrayToString(byte[] ba){
		StringBuilder hex = new StringBuilder(ba.length * 2);
		for(int i=0; i < ba.length; i++){
			hex.append(String.format("%02X", ba[i]));
		}
		return hex.toString();
    }
    
    //wrapper function to hash the input string
    public static byte[] hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //create the digest on the sha 256 algorithm
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        //perform the hash. gets the output as a byte array
        //class code converts to utf encoding. Don't know why
        byte[] output_hash = digest.digest(s.getBytes("UTF-8"));

        //should probably convert to string here, but probably more modular if I don't
        return output_hash;
    }
    //wrapper to translate the input string to hash byte array to a string
    public static String hash_string(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        byte[] output_hash = hash(s);
        String output = ByteArrayToString(output_hash);
        return output;
    }

    //wrapper function to read the json into a block
    public static block read_json(String filename){
        //create json deserializer
        Gson gson = new Gson();

        //read the file and serialize it to the block class
        try (Reader reader = new FileReader(filename)){
            block b = gson.fromJson(reader, block.class);
            return b;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    //wrapper function to write a block to the given filename
    public static void write_json(block b, String filename){
        //from the class code to write the json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try( FileWriter writer = new FileWriter(filename)){
            gson.toJson(b, writer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //wrapper function to read a json string anc convert to a block
    public static block decode_json(String json){
        Gson gson = new Gson();

        return gson.fromJson(json, block.class);
    }
}


class block implements Serializable{
    //TODO:: enter fields given by the assignment

    //block identifier
    String block_id;
    //hash of the previous block to chain to
    String previous_hash;
    //winning hash that verifies this block
    String winning_hash;
    //the winning guess
    String random_seed;


    public String get_block_string(){
        /*
        This function gets the string representation of this block to senf to the work function to be hashed
        */

        //start with the previous hash
        String data = previous_hash;

        //concatenate the block fields
        data += block_id;
        //TODO:: add more fields

        return data;
    }

    public String get_block_id(){
        /*
        returns the identifier of the block
        */
        //TODO:: implement function

        return block_id;
    }

    public String get_previous_hash(){
        return previous_hash;
    }

    public void set_block_id(String id){
        block_id = id;
    }

    public void set_previous_hash(String hash){
        previous_hash = hash;
    }

    public void set_random_seed(String seed){
        /*
        Sets the  seed and the corresponding hash
        */
        
        try {
            random_seed = seed;
            String block_string = get_block_string();
            String concat = block_string + seed;
            winning_hash = utils.hash_string(concat);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
    }

}


class Node{

    //the difficulty of the problem
    //increase the value to make easier. decrease to make harder
    //range from 0 to 65535
    public static final int difficulty = 1000;

    //from the class code
    //blocking queue to store unverified blocks to be processed
    BlockingQueue<block> processing_queue;
    //store the identifier of a block. Populate set with verified block ids
    //TODO:: choose the identifier. UUID or previous hash
    //can be used to check if a block has been verified. Useful to stop work on already verified blocks.
    HashSet<String> verified_blocks;


    //hashmap maps to store host information
    //maps a host to a port
    //these are the hosts to send unverified blocks to
    HashMap<String,Integer> unverified_block_server_hosts;
    //these are the hosts to send verified blocks to
    HashMap<String,Integer> verified_block_server_hosts;



    Node(int ub_port, int vb_port){
        /*
        constructor to specify the ports of the unverified and verified block servers.
        Also initialize the queue, set, and hashmaps
        */
        unverified_block_server_hosts = new HashMap<>();
        verified_block_server_hosts = new HashMap<>();

        unverified_block_server_hosts.put("localhost", ub_port);
        verified_block_server_hosts.put("localhost", vb_port);
        
        
        //keep the blocking queue as an unbounded queue to allow for unlimited blocks
        processing_queue = new LinkedBlockingQueue<block>();
        verified_blocks = new HashSet<String>();

        //TODO:: start the servers and the workers
        new Thread(new unverified_block_server(ub_port)).start();;
        new Thread(new worker()).start();
    }

    public void add_unverified_block_host(String host, int port){
        unverified_block_server_hosts.put(host, port);
    }

    public void add_verified_block_host(String host, int port){
        verified_block_server_hosts.put(host,port);
    }


    class unverified_block_server implements Runnable{
        /*
        Server to handle incoming new unverified blocks.
        Starts a server socket and expects block type objects.
        Once recieved, starts the unverified block worker to add the block to the processing queue.
        */

        //server variables
        int port;
        Socket skt;
        int max_q_size = 12;

        unverified_block_server(int p){
            port = p;
        }

        public void run(){
            /*
            starts the server
            */

            try{
                ServerSocket server = new ServerSocket(port,max_q_size);
                while(true){
                    skt = server.accept();

                    //start the worker to add to the queue
                    new unverified_block_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class unverified_block_worker extends Thread{
        /*
        A worker thread to read an incoming socket request.
        The data should be a block object serialized as a json string.
        The process decodes the json string to a block.
        Once read, the worker adds the block to the processing queue.
        */

        Socket skt;

        unverified_block_worker(Socket s){
            skt = s;
        }

        public void run(){
            //block b;

            try{
                //this input expects the data in string format
                BufferedReader reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                //get the socket data as a json string
                String json = reader.readLine();
                //System.out.println(json);
                //decode the string into a block object
                block b = utils.decode_json(json);
                //System.out.println(b.block_id);

                
                //get the socket data as an object
                //ObjectInputStream input = new ObjectInputStream(skt.getInputStream());
                //cast the object to a block
                //b = (block)input.readObject();

                //add the block to the processing queue
                processing_queue.add(b);
                //System.out.println("added the block to the queue");
                skt.close();
            }
            catch(IOException e){
            //catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    class verified_block_server implements Runnable{
        /*
        Server to handle incoming verified blocks.
        Starts a server socket and expects block type objects.
        Once recieved, starts the verifified block worker to add the block to the ledger.
        The worker will also muticast the verified block out to the child nodes
        */

        //server variables
        int port;
        Socket skt;
        int max_q_size = 12;

        verified_block_server(int p){
            port = p;
        }

        public void run(){

            try{
                ServerSocket server = new ServerSocket(port,max_q_size);
                while(true){
                    skt = server.accept();

                    new verified_block_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            
        }
    }

    class verified_block_worker extends Thread{
        Socket skt;

        verified_block_worker(Socket s){
            skt = s;
        }

        public void run(){
            //TODO:: read the socket and convert to block
            
            //TODO:: add the new block to the ledger
        }
    }


    class worker implements Runnable{
        /*
        Class to perform the work on a block.
        Use as a process to take from the processing queue and verify the block
        Sends the verified block to other nodes
        */
        public void run(){
            try{
                block b;
                while(true){
                    //take from the blocking queue if an item exists
                    b = processing_queue.take();

                    //need the block id to check in the verified blocks hashmap
                    String block_id = b.get_block_id();

                    //perform the work and get the random seed back
                    String random_seed = work(b.get_block_string(), block_id);
                    
                    if(random_seed == null){
                        continue;
                    }
                    else if(verified_blocks.contains(block_id)){
                        //last check to see if the block has been verified before sending
                        continue;
                    }
                    else{
                        //send the verified block to the other nodes

                        //set the random seed and the winning hash
                        b.set_random_seed(random_seed);

                        int port;
                        //create loop to send to all child nodes' verified block servers
                        for(String host:verified_block_server_hosts.keySet()){
                            port = verified_block_server_hosts.get(host);
                            send_verified_block(b, host, port);
                        }
                    }
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        public String work(String data, String block_id){
            /*
            This function is used to perform the work to verify a block.
            Expects the block string as input.
            Performs the work in a loop and periodically checks if the current block has already been verified.
            If so, then stop. Continues work until the block is verified.
            Returns the random seed if found
            Returns null if exited early
            */

            //TODO: update to ensure that the work is consistent with the assignment
    
            //string to store the random seed for the answer
            String rand;
            //placeholder string for the concatenatation
            String concat;
            //placeholder string to store the hash
            String hash;
            //placeholder int to store the hash leading values
            int answer;
    
            try {
                //keep generating until an answer has been found
                while(true){
                    //run loop n times before checking the set for the verified block
                    //increase n to solve faster
                    //decrease n to solve slower
                    for(int i = 0; i < 3; i++){
                        //generate a random string and concatenate with the data
                        rand = utils.randomAlphaNumeric(8);
                        concat = data + rand;
        
                        //perform the hash of the new string
                        hash = utils.hash_string(concat);
                        
                        //take the first 4 characters and parse to hex
                        answer = Integer.parseInt(hash.substring(0,4),16);

                        //verification step
                        if(answer < difficulty){
                            //solved if condition met.
                            //return the random seed generated

                            System.out.println("block verified");
                            System.out.println(String.format("random seed is: %s", rand));
                            System.out.println(String.format("winning hash is: %s", hash));
                            System.out.println(String.format("answer is: %d", answer));
                            return rand;
                        }
                    }

                    //reach here if the n attempts did not result in an answer
                    //System.out.println("not found in n attempts. trying again");
                    //sleep to fake the work
                    Thread.sleep(2000);
                    
                    //check if the blockchain has been updated
                    //if the chain is updated, then break from the loop
                    //use the verified_blocks set
                    if(verified_blocks.contains(block_id)){
                        return null;
                    }
                }
    
            }
            catch (NoSuchAlgorithmException | UnsupportedEncodingException | InterruptedException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
            //final return block for any exceptions that occur
            return null;
        }

        public void send_verified_block(block b, String host, int port){
            /*
            Sends the block to the given host on the given port.
            Assumes the block has already been verified.
            */
            Socket skt;

            try {
                skt = new Socket(host,port);

                //send the data as an object
                ObjectOutputStream output = new ObjectOutputStream(skt.getOutputStream());

                //send the object
                output.writeObject(b);
                output.flush();
                skt.close();
                
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }
    

    }



}