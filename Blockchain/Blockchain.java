import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.security.*;


class Blockchain{

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("test")){
            Node a0 = new Node(4710,4820,4930,"A-0");
            Node b1 = new Node(4711,4821,4931,"B-1");
            Node c2 = new Node(4712,4822,4932,"C-2");
            
            a0.add_unverified_block_host("localhost", 4821);
            a0.add_verified_block_host("localhost", 4931);
            a0.add_unverified_block_host("localhost", 4822);
            a0.add_verified_block_host("localhost", 4932);
            
            b1.add_unverified_block_host("localhost", 4820);
            b1.add_verified_block_host("localhost", 4930);
            b1.add_unverified_block_host("localhost", 4822);
            b1.add_verified_block_host("localhost", 4932);
            
            c2.add_unverified_block_host("localhost", 4820);
            c2.add_verified_block_host("localhost", 4930);
            c2.add_unverified_block_host("localhost", 4821);
            c2.add_verified_block_host("localhost", 4931);
            
            try {
                Thread.sleep(3000);
                start_console("localhost",4710,"A-0");
                
                Gson gson = new Gson();
                ArrayList<block> new_blocks =  utils.read_input_file("BlockInput0.txt", "A-0");
                for(block b: new_blocks){
                    String json = gson.toJson(b);
                    send_command(json,"localhost",4820); 
                }
                Thread.sleep(5000);
                new_blocks =  utils.read_input_file("BlockInput1.txt", "B-1");
                for(block b: new_blocks){
                    String json = gson.toJson(b);
                    send_command(json,"localhost",4821); 
                }
                Thread.sleep(5000);
                new_blocks =  utils.read_input_file("BlockInput2.txt", "C-2");
                for(block b: new_blocks){
                    String json = gson.toJson(b);
                    send_command(json,"localhost",4822); 
                }
    
            } catch (InterruptedException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        else if(args.length > 0 && args[0].equals("console")){
            String host = args[1];
            int port = Integer.parseInt(args[2]);
            String name = args[3];
            try {
                command_console(host, port, name);
            } catch (IOException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if(args.length == 1){
            int process;
            int[] servers = {4710,4820,4930};
            String[] names = {"first", "second", "third"};
            process = Integer.parseInt(args[0]);

            String name = names[process];

            Node n = new Node(servers[0]+process,servers[1]+process,servers[2]+process,name);
            
            switch(process){
                case 0:
                    n.add_unverified_block_host("localhost", 4821);
                    n.add_verified_block_host("localhost", 4931);
                    n.add_unverified_block_host("localhost", 4822);
                    n.add_verified_block_host("localhost", 4932);
                    break;
                case 1:
                    n.add_unverified_block_host("localhost", 4820);
                    n.add_verified_block_host("localhost", 4930);
                    n.add_unverified_block_host("localhost", 4822);
                    n.add_verified_block_host("localhost", 4932);
                    break;
                case 2:
                    n.add_unverified_block_host("localhost", 4820);
                    n.add_verified_block_host("localhost", 4930);
                    n.add_unverified_block_host("localhost", 4821);
                    n.add_verified_block_host("localhost", 4931);
                    try {
                        Thread.sleep(3000);
                        start_console("localhost",servers[0]+process,name);
                        
                        Gson gson = new Gson();
                        ArrayList<block> new_blocks =  utils.read_input_file("BlockInput0.txt", "first");
                        for(block b: new_blocks){
                            String json = gson.toJson(b);
                            send_command(json,"localhost",4820); 
                        }
                        Thread.sleep(5000);
                        new_blocks =  utils.read_input_file("BlockInput1.txt", "second");
                        for(block b: new_blocks){
                            String json = gson.toJson(b);
                            send_command(json,"localhost",4821); 
                        }
                        Thread.sleep(5000);
                        new_blocks =  utils.read_input_file("BlockInput2.txt", "third");
                        for(block b: new_blocks){
                            String json = gson.toJson(b);
                            send_command(json,"localhost",4822); 
                        }
            
                    } catch (InterruptedException e) {
                        //Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println();
                    break;
            }
        }
        else{
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter a name for your node");
                System.out.flush();
                String name = reader.readLine();
                System.out.println("Enter a port to run the command server");
                System.out.flush();
                int command_port = Integer.parseInt(reader.readLine());
                System.out.println("Enter a port to run the unverified block server");
                System.out.flush();
                int ub_port = Integer.parseInt(reader.readLine());
                System.out.println("Enter a port to run the verified block server");
                System.out.flush();
                int vb_port = Integer.parseInt(reader.readLine());

                Node n = new Node(command_port, ub_port, vb_port, name);
                start_console("localhost", command_port, name);
            }
            catch(IOException e){
                e.printStackTrace();
            }
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
            e.printStackTrace();
        }
    }

    
    public static void command_console(String host, int port, String name) throws IOException{
        System.out.println("Starting the command console");
        System.out.println(String.format("host: %s | port: %d | name: %s\n", host, port, name));
        
        System.out.println("Available commands:");
        System.out.println("print | ledger | show | print ledger | show ledger - prints the ledger on the running node's console");
        System.out.println("save - to save the ledger to a json file in the node");
        System.out.println("add | host | add host | new host- connect a node to this node");
        System.out.println("file | from file | read | read file - give a txt file that contains the new block data you want to add to the blockchain");
        System.out.println("block | new block | new - allows you to enter a line to create a single block to add to the chain");
        System.out.println("");

        String command;
        String json;
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.println("Enter a command: ");
            System.out.flush();
            command = reader.readLine();

            if(command.toLowerCase().equals("add") | command.toLowerCase().equals("host") | command.toLowerCase().equals("add host") | command.toLowerCase().equals("new host")){
                System.out.println("Enter the new host details");
                System.out.println("Enter the host name or IP: ");
                System.out.flush();
                String h = reader.readLine();
                System.out.println("Enter the port to send unverified blocks");
                int ub_port = Integer.parseInt(reader.readLine());
                System.out.println("Enter the port to send verified blocks");
                int vb_port = Integer.parseInt(reader.readLine());
                
                HashMap<String,String> output_json = new HashMap<String,String>();
                output_json.put("host",h);
                output_json.put("ub_port",""+ub_port);
                output_json.put("vb_port",""+vb_port);
                output_json.put("type","add host");

                json = gson.toJson(output_json);
                //System.out.println(json);
                send_command(json, host, port);
            }
            else if (command.toLowerCase().equals("block") | command.toLowerCase().equals("new") | command.toLowerCase().equals("new block")){
                System.out.println("Enter the block details in 1 line:");
                System.out.flush();

                String line;
                String[] split;
                Date date = new Date();

                line = reader.readLine();
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
                
                json = gson.toJson(b);
                send_command(json,host,port);
            }
            else if(command.toLowerCase().equals("file") | command.toLowerCase().equals("from file") | command.toLowerCase().equals("read") | command.toLowerCase().equals("read file")){
                System.out.println("Enter a file name to read the block from");
                System.out.flush();

                String filename = reader.readLine();

                ArrayList<block> new_blocks = utils.read_input_file(filename, name);
                for(block b: new_blocks){
                    json = gson.toJson(b);
                    System.out.println(String.format("Sending block: %s",b.get_block_id()));
                    send_command(json,host,port); 
                }
            }
            else if(command.toLowerCase().equals("print") | command.toLowerCase().equals("ledger") | command.toLowerCase().equals("print ledger") | command.toLowerCase().equals("show") | command.toLowerCase().equals("show ledger")){
                send_command("{\"type\":\"print\"}", host, port);
            }
            else if(command.toLowerCase().equals("save")){
                send_command("{\"type\":\"save\"}", host, port);
            }
            else{
                System.out.println("Invalid command");
            }
        }
    }

    public static void start_console(String host, int port, String name){
        //https://www.baeldung.com/java-detect-os
        //https://stackoverflow.com/questions/3819571/bash-open-a-terminal-with-a-command-to-run-passed-as-an-argument
        //https://askubuntu.com/questions/630698/how-can-i-keep-the-gnome-terminal-open-after-a-program-closes#:~:text=First%20option%3A%20edit%20gnome%2Dterminal,choose%20%22Keep%20terminal%20open%22.

        String os = System.getProperty("os.name");
        if(os.equals("Linux")){
            
            try {
                Process pb = new ProcessBuilder("x-terminal-emulator", "-e", String.format("java -cp \".:gson-2.8.2.jar\" Blockchain console %s %d %s;read line",host,port,name)).start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("Unable to open the console. You will have to do it manually in a different terminal.");
            }
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
    //the node name of the solver
    String solver;
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
        data += solver;

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
    public String get_solver(){
        return solver;
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
    public void set_solver(String s){
        solver = s;
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
    public static final int difficulty = 1000;
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
    //BlockingQueue<block> processing_queue;
    PriorityBlockingQueue<block> processing_queue;
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

    int unverified_block_server_port;


    Node(int command_port, int ub_port, int vb_port, String n){
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
        //processing_queue = new LinkedBlockingQueue<block>();
        //from the class code
        processing_queue = new PriorityBlockingQueue<>(100, BlockTSComparator);
        verified_blocks = new HashSet<String>();
        unverified_blocks = new HashSet<String>();

        //set initial value for previous hash
        previous_hash = "";
        //initialize the linked list
        ledger = new LinkedList<block>();


        //TODO:: start the servers and the workers
        Thread command_server_thread = new Thread(new command_server(command_port));
        command_server_thread.start();
        Thread unverified_block_server_thread = new Thread(new unverified_block_server(ub_port));
        unverified_block_server_thread.start();
        Thread worker_thread = new Thread(new worker());
        worker_thread.start();
        Thread verified_block_server_thread = new Thread(new verified_block_server(vb_port));
        verified_block_server_thread.start();
        
    }

    public void add_unverified_block_host(String host, int port){
        //unverified_block_server_hosts.put(host, port);
        System.out.println(String.format("%s-command: added new unverified block host | host: %s | port %d", name, host, port));
        unverified_block_server_hosts.add(host + ":" + port);
    }

    public void add_verified_block_host(String host, int port){
        //verified_block_server_hosts.put(host,port);
        System.out.println(String.format("%s-command: added new verified block host | host: %s | port %d", name, host, port));
        verified_block_server_hosts.add(host + ":" + port);
    }

    //from the class code
    public static Comparator<block> BlockTSComparator = new Comparator<block>()
    {
        @Override
        public int compare(block b1, block b2)
        {
            String s1 = b1.get_timestamp();
            String s2 = b2.get_timestamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };

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
            unverified_block_server_port = p;
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
                    System.out.println(String.format("%s: block previous hash does not match the current previous hash | block hash: %s | node hash: %s", name, b.get_block_id(), previous_hash));
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
                    //set the solver before work as the solver will be included in the data
                    b.set_solver(name);
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

    class command_server implements Runnable{
        /*
        Starts a server that accepts incomming commands
        commands can be to add a child node, read a file, print the ledger
        */
        
        //server variables
        int port;
        Socket skt;
        int max_q_size = 12;

        command_server(int p){
            port = p;
        }

        public void run(){

            try{
                ServerSocket server = new ServerSocket(port,max_q_size);
                while(true){
                    skt = server.accept();

                    System.out.println(String.format("%s-command-server: Received command",name));

                    //get the sender information
                    SocketAddress remote = skt.getRemoteSocketAddress();
                    System.out.println(String.format("%s-command-server: command sent from: %s", name, remote));
                    

                    new command_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            
        }
    }

    class command_worker extends Thread{
        Socket skt;

        command_worker(Socket s){
            skt = s;
        }

        public void run(){
            try {
                //read the socket and convert to json string
                BufferedReader reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                String json;
                json = reader.readLine();
                Gson gson = new Gson();
                HashMap command = gson.fromJson(json, HashMap.class);

                String type = (String) command.get("type");

                switch(type){
                    case "add host":
                        String host = (String) command.get("host");
                        int ub_port = Integer.parseInt((String) command.get("ub_port"));
                        int vb_port = Integer.parseInt((String) command.get("vb_port"));
                        add_unverified_block_host(host, ub_port);
                        add_verified_block_host(host, vb_port);
                        break;
                    case "print":
                        print_ledger();
                        break;
                    case "new":
                        String b = (String) command.get("block");
                        send_block(b);
                        break;
                    case "save":
                        save_ledger();
                        break;
                    default:
                        System.out.println("invalid command recieved");
                        break;
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void print_ledger(){
            System.out.println(String.format("%s-command-worker: PRINTING LEDGER", name));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String output = gson.toJson(ledger);
            System.out.println(output);
        }

        public void save_ledger(){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try( FileWriter writer = new FileWriter("BlockchainLedger.json")){
                gson.toJson(ledger, writer);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public void send_block(String command){
            /*
            sends a json string to the unverified block server
            the json string is the block
            sends 1 block
            */
            
            Socket skt;
            PrintStream to_server;
    
    
            try{
                //open the connection to the admin server
                skt = new Socket("localhost", unverified_block_server_port);
                
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

}