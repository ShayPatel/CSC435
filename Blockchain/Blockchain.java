import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.*;


class Blockchain{

    public static void main(String[] args) {
        ArrayList<block> new_blocks =  utils.read_input_file("BlockInput0.txt", "A-0");
        
        Node a0 = new Node(4820,4930,"A-0");
        Node b1 = new Node(4821,4931,"B-1");

        a0.add_unverified_block_host("localhost", 4821);
        a0.add_verified_block_host("localhost", 4931);
        b1.add_unverified_block_host("localhost", 4820);
        b1.add_verified_block_host("localhost", 4930);

        try {
            Thread.sleep(3000);
            
            Gson gson = new Gson();
            for(block b: new_blocks){
                String json = gson.toJson(b);
                send_command(json,"localhost",4821); 
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


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
    //wrapper function to read a json string and convert to a block
    public static block decode_json(String json){
        Gson gson = new Gson();

        return gson.fromJson(json, block.class);
    }

    public static ArrayList<block> read_input_file(String filename, String name){
        ArrayList<block> new_blocks = new ArrayList<block>();
        try {

            //from the class code
            Date date = new Date();

            //from the class code
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line;
            String[] split;
            while((line = br.readLine()) != null){
                block b = new block();
                split = line.split("\\s+");
                
                //set the block id
                b.set_block_id(UUID.randomUUID().toString());

                //set the creator name
                b.set_creator_name(name);
                //set the timestamp
                //from the class code
                b.set_timestamp(String.format("%1$s %2$tF.%2$tT", "", date));

                b.set_first_name(split[0]);
                b.set_last_name(split[1]);
                b.set_birth_day(split[2]);
                b.set_ssn(split[3]);
                b.set_condition(split[4]);
                b.set_treatment(split[5]);
                b.set_medicine(split[6]);

                new_blocks.add(b);
            }

            br.close();
        } catch (IOException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
        return new_blocks;
        
    }
}


class block implements Serializable{
    //block identifier
    String block_id;
    //hash of the previous block to chain to
    String previous_hash;
    //winning hash that verifies this block
    String winning_hash;
    //the winning guess
    String random_seed;
    //the node name of the creator
    String creator_name;
    //timestamp the block was created
    String timestamp;

    String first_name;
    String last_name;
    String birth_day;
    String ssn;
    String condition;
    String treatment;
    String medicine;
    


    //special get as this creates the string representation of the block used during the work
    public String get_block_string(){
        /*
        This function gets the string representation of this block to senf to the work function to be hashed
        */

        String data = "";

        //concatenate the block fields
        data += block_id;
        data += creator_name;
        data += timestamp;

        data += first_name;
        data += last_name;
        data += birth_day;
        data += ssn;
        data += condition;
        data += treatment;
        data += medicine;

        return data;
    }
    
    
    public String get_block_id(){
        /*
        returns the identifier of the block
        */
        return block_id;
    }
    public String get_previous_hash(){
        return previous_hash;
    }
    public String get_creator_name(){
        return creator_name;
    }
    public String get_first_name(){
        return first_name;
    }
    public String get_last_name(){
        return last_name;
    }
    public String get_timestamp(){
        return timestamp;
    }
    public String get_birth_day(){
        return birth_day;
    }
    public String get_ssn(){
        return ssn;
    }
    public String get_condition(){
        return condition;
    }
    public String get_treatment(){
        return treatment;
    }
    public String get_medicine(){
        return medicine;
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
        assumes the previous hash exists
        */
        
        try {
            random_seed = seed;
            String concat = previous_hash + get_block_string() + seed;
            winning_hash = utils.hash_string(concat);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void set_creator_name(String s){
        creator_name = s;
    }
    public void set_first_name(String s){
        first_name = s;
    }
    public void set_last_name(String s){
        last_name = s;
    }
    public void set_timestamp(String s){
        timestamp = s;
    }
    public void set_birth_day(String s){
        birth_day = s;
    }
    public void set_ssn(String s){
        ssn = s;
    }
    public void set_condition(String s){
        condition = s;
    }
    public void set_treatment(String s){
        treatment = s;
    }
    public void set_medicine(String s){
        medicine = s;
    }

}


class Node{
    
    //the first n characters to take from the hash and convert to int
    //this determines the size of the problem space to 16^n possible values
    //increase to make the problem harder
    //decrease to make the problem easier
    public static final int hash_substring_length = 4;
    //the difficulty of the problem
    //increase the value to make easier. decrease to make harder
    //range from 0 to 65535 if hash_substring is 4
    //for n chars, easiest difficulty value is (16^n)-1
    public static final int difficulty = 10000;
    //speed translates to how many attempts to make before sleeping.
    //increase speed to solve the work faster
    //decrease to solve it slower
    public static final int speed = 1;
    //how many milliseconds to sleep after each set of attempts during work
    //increase to solve slower
    //decrease to solve faster
    //set to 0 or 1 for effectively no sleep
    public static final int sleep = 2000;


    public String name;

    //from the class code
    //blocking queue to store unverified blocks to be processed
    BlockingQueue<block> processing_queue;
    //store the identifier of a block. Populate set with verified block ids
    
    //can be used to check if a block has been verified. Useful to stop work on already verified blocks.
    HashSet<String> verified_blocks;
    HashSet<String> unverified_blocks;


    //hashmap maps to store host information
    //maps a host to a port
    //these are the hosts to send unverified blocks to
    //HashMap<String,Integer> unverified_block_server_hosts;
    //these are the hosts to send verified blocks to
    //HashMap<String,Integer> verified_block_server_hosts;
    //use these hashmaps as you cannot store the same key with multiple values in the hashmap
    HashSet<String> unverified_block_server_hosts;
    HashSet<String> verified_block_server_hosts;

    //store the latest verified block hash
    String previous_hash;
    //have a linked list of all the blocks that have been varified
    LinkedList<block> ledger;



    Node(int ub_port, int vb_port, String n){
        /*
        constructor to specify the ports of the unverified and verified block servers.
        Also initialize the queue, set, and hashmaps
        */

        //unverified_block_server_hosts = new HashMap<>();
        unverified_block_server_hosts = new HashSet<String>();
        //verified_block_server_hosts = new HashMap<>();
        verified_block_server_hosts = new HashSet<String>();

        //unverified_block_server_hosts.put("localhost", ub_port);
        unverified_block_server_hosts.add("localhost:"+ub_port);
        //verified_block_server_hosts.put("localhost", vb_port);
        verified_block_server_hosts.add("localhost:"+vb_port);

        //give a name to the node
        name = n;
        
        
        //keep the blocking queue as an unbounded queue to allow for unlimited blocks
        processing_queue = new LinkedBlockingQueue<block>();
        verified_blocks = new HashSet<String>();
        unverified_blocks = new HashSet<String>();

        //set initial value for previous hash
        previous_hash = "";
        //initialize the linked list
        ledger = new LinkedList<block>();


        //TODO:: start the servers and the workers
        new Thread(new unverified_block_server(ub_port)).start();;
        new Thread(new worker()).start();
        new Thread(new verified_block_server(vb_port)).start();
    }

    public void add_unverified_block_host(String host, int port){
        //unverified_block_server_hosts.put(host, port);
        unverified_block_server_hosts.add(host + ":" + port);
    }

    public void add_verified_block_host(String host, int port){
        //verified_block_server_hosts.put(host,port);
        verified_block_server_hosts.add(host + ":" + port);
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

                    System.out.println(String.format("%s-UB-server: Recieved unverified block to be processed",name));
                    //get the sender information
                    SocketAddress remote = skt.getRemoteSocketAddress();
                    System.out.println(String.format("%s-UB-server: block sent from: %s", name, remote));


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


                if(unverified_blocks.contains(b.get_block_id())){
                    System.out.println(String.format("%s-UB-worker: Recieved unverified block that has already been acknowledged", name));
                }
                else{
                    System.out.println(String.format("%s-UB-worker: Acknowledging unverified block: %s", name, b.get_block_id()));
                    unverified_blocks.add(b.get_block_id());

                    //add the block to the processing queue
                    processing_queue.add(b);
                    System.out.println(String.format("%s-UB-worker: added the block to the processing queue",name));
                    
                    skt.close();

                    String host;
                    int port;
                    //create loop to send to all child nodes' unverified block servers
                    for(String h:unverified_block_server_hosts){
                        //port = unverified_block_server_hosts.get(host);
                        host = h.split(":")[0];
                        port = Integer.parseInt(h.split(":")[1]);
                        System.out.println(String.format("%s-UB-worker: sending unverified block | host: %s:%d | block: %s", name, host, port, b.get_block_id()));
                        send_block(b, host, port);
                    }
                }
            }
            catch(IOException e){
            //catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    
        public void send_block(block b, String host, int port){
            /*
            Sends the block to the given host on the given port.
            This function does not assume the block has been verified
            Converts the block to a json string before sending
            */

            try {
                //convert the block to a json string
                Gson gson = new Gson();
                String json = gson.toJson(b);
                //System.out.println(json);
                

                Socket skt = new Socket(host,port);
                PrintStream to_server = new PrintStream(skt.getOutputStream());

                //sending the command
                to_server.println(json);
                to_server.flush();

                skt.close();
                
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class verified_block_server implements Runnable{
        /*
        Server to handle incoming verified blocks.
        Starts a server socket and expects blocks serialized as json.
        Once recieved, starts the verifified block worker to add the block to the ledger.
        The worker will also multicast the verified block out to the child nodes
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

                    System.out.println(String.format("%s-BC-server: Received block to be verified",name));

                    //get the sender information
                    SocketAddress remote = skt.getRemoteSocketAddress();
                    System.out.println(String.format("%s-BC-server: block sent from: %s", name, remote));
                    

                    new verified_block_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            
        }
    }

    class verified_block_worker extends Thread{
        /*
        Expects a json string input from the socket
        converts the string to a block
        reverifies to confirm work
        multicast the block out to other nodes
        */
        Socket skt;

        verified_block_worker(Socket s){
            skt = s;
        }

        public void run(){
            try {
                
                //read the socket and convert to block
                block b;
                BufferedReader reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                String json = reader.readLine();
                b = utils.decode_json(json);

                
                if(verified_blocks.contains(b.get_block_id())){
                    System.out.println(String.format("%s-BC-worker: Recieved block that has already been added to ledger", name));
                }
                //check the previous hash is still valid
                else if(b.get_previous_hash().equals(previous_hash)){
                    //reverify the block
                    String data = b.get_block_string();
                    String concat = previous_hash + data + b.random_seed;
                    String hash = utils.hash_string(concat);
                    int answer = Integer.parseInt(hash.substring(0,4),16);

                    if(answer < difficulty){
                        System.out.println(String.format("%s-BC-worker: Successfully verified block | id: %s", name, b.get_block_id()));
                        System.out.println(String.format("%s-BC-worker: Updating previous hash | old: %s | new: %s", name, previous_hash, hash));
                        previous_hash = hash;

                        //process the newly verified block
                        verified_blocks.add(b.get_block_id());
                        
                        
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        
                        // Convert the Java object to a JSON String:
                        String output = gson.toJson(b);
                        System.out.println(String.format("%s-BC-worker: block added to the ledger | id: %s", name, b.get_block_id()));
                        System.out.println(output);
                        ledger.add(b);

                        System.out.println(String.format("%s-BC-worker: Sending verified block to other nodes", name));
                        
                        String host;
                        int port;
                        //forward the verified block to other blockchain servers
                        //create loop to send to all child nodes' verified block servers
                        for(String h:verified_block_server_hosts){
                            //port = verified_block_server_hosts.get(host);
                            host = h.split(":")[0];
                            port = Integer.parseInt(h.split(":")[1]);
                            System.out.println(String.format("%s-BC-worker: sending to: %s:%d", name, host, port));
                            send_verified_block(b, host, port);
                        }
                        
                    }
                    else{
                        System.out.println(String.format("%s-BC-worker: unable to verify block with id: %s", name, b.get_block_id()));
                    }
                }
                else{
                    System.out.println(String.format("%s: block previous hash does not match the current previous hash | block: %s | node: %s", name, b.get_block_id(), previous_hash));
                }
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }

            
        }
    
        public void send_verified_block(block b, String host, int port){
            /*
            Sends the block to the given host on the given port.
            Assumes the block has already been verified.
            Converts the block to a json string before sending
            */

            try {
                //convert the block to a json string
                Gson gson = new Gson();
                String json = gson.toJson(b);
                //System.out.println(json);
                

                Socket skt = new Socket(host,port);
                PrintStream to_server = new PrintStream(skt.getOutputStream());

                //sending the command
                to_server.println(json);
                to_server.flush();

                skt.close();
                
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
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
                    //start a sleep before working on the next block
                    //sleep for a while to ensure the previous hash has been updated
                    Thread.sleep(2000);

                    //take from the blocking queue if an item exists
                    b = processing_queue.take();
                    System.out.println(String.format("%s-worker: Working on block", name));
                    System.out.println(String.format("%s-worker: took block from queue with id: %s", name, b.get_block_id()));

                    //need the block id to check in the verified blocks hashmap
                    String block_id = b.get_block_id();

                    System.out.println(String.format("%s-worker: starting work function on block: %s", name, b.get_block_id()));
                    //perform the work and get the random seed back
                    String random_seed = work(b.get_block_string(), block_id);
                    //set previous hash here to prevent previous hash from being changed on thread switch
                    b.set_previous_hash(previous_hash);

                    if(random_seed == null){
                        System.out.println(String.format("%s-worker: unable to process the block. Seed is null | block: %s", name, b.get_block_id()));
                        continue;
                    }
                    else if(verified_blocks.contains(block_id)){
                        //last check to see if the block has been verified before sending
                        System.out.println(String.format("%s-worker: block already verified by another process | block: %s", name, b.get_block_id()));
                        continue;
                    }
                    else{
                        //send the verified block to the other nodes
                        System.out.println(String.format("%s-worker: Sending verified block to other nodes | block: %s", name, b.get_block_id()));

                        //set the random seed and the winning hash
                        b.set_random_seed(random_seed);
                        

                        String host;
                        int port;
                        //create loop to send to all child nodes' verified block servers
                        for(String h:verified_block_server_hosts){
                            //port = verified_block_server_hosts.get(host);
                            host = h.split(":")[0];
                            port = Integer.parseInt(h.split(":")[1]);
                            System.out.println(String.format("%s-worker: sending to: %s:%d", name, host, port));                            
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
                    for(int i = 0; i < speed; i++){
                        //generate a random string and concatenate with the data
                        rand = utils.randomAlphaNumeric(8);
                        concat = previous_hash + data + rand;
        
                        //perform the hash of the new string
                        hash = utils.hash_string(concat);
                        
                        //take the first n characters and parse to hex
                        answer = Integer.parseInt(hash.substring(0,hash_substring_length),16);

                        //verification step
                        if(answer < difficulty){
                            //solved if condition met.
                            //return the random seed generated

                            System.out.println(String.format("%s-worker: SUCCESS! solved block | seed: %s | hash: %s", name, rand, hash));
                            return rand;
                        }
                    }

                    //reach here if the n attempts did not result in an answer
                    //System.out.println("not found in n attempts. trying again");
                    //sleep to fake the work
                    Thread.sleep(sleep);
                    
                    //check if the blockchain has been updated
                    //if the chain is updated, then break from the loop
                    //use the verified_blocks set
                    if(verified_blocks.contains(block_id)){
                        System.out.println(String.format("%s-worker: block verified during work. Exiting work function | block: %s", name, block_id));
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
            Converts the block to a json string before sending
            */

            try {
                //convert the block to a json string
                Gson gson = new Gson();
                String json = gson.toJson(b);
                //System.out.println(json);
                

                Socket skt = new Socket(host,port);
                PrintStream to_server = new PrintStream(skt.getOutputStream());

                //sending the command
                to_server.println(json);
                to_server.flush();

                skt.close();
                
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }
    

    }



}