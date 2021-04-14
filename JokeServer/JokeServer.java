/*--------------------------------------------------------

1. Akshay Patel 4/13/21:

2. Java version used (java -version), if not the official version for the class:

build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java

4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer

You may pass additional arguments into the run command for the server to change the port

> java JokeServer [port]


5. List of files needed for running the program.
JokeServer.java
JokeClient.java
JokeClientAdmin.java

5. Notes:

The server clients can handle additional commands to change the state of the server. The state of the server is tied to the running instance of the server.


----------------------------------------------------------*/



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JokeServer{


    public static void main(String[] args) throws IOException{
        //admin server and the main server have to run on different ports
        int port;
        int admin_port;
        //ignore. took it from the inet code i submitted
        int max_q_size = 6;
        
        if(args.length == 0){
            port = 4545; // default port will be 4545
            admin_port = 5050; //default admin port is 5050
        }
        //if 1 arg
        //this will only change the server port. if not number, then uses secondary
        else if(args.length == 1){
            try{
                port = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException e){
                port = 4546;
            }
            admin_port = 5050;
        }
        //2 args or more
        //read only the first 2
        //1st will be main port. 2nd is admin port
        //if 2nd is not a number, then use 4546 secondary port
        else{
            try{
                port = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException e){
                port = 4546;
            }
            admin_port = Integer.parseInt(args[1]);
        }
        
        
        
        //create new thread that hosts the admin server
        //admin server and main server have to communicate the mode.
        //mode will have to be a saved state in the admin server
        new JokeServerAdmin(admin_port).start();
        
        
        Socket skt;
        ServerSocket server_skt = new ServerSocket(port,max_q_size);
        System.out.println("Starting Akshay Patel's JokeServer at port: " + port);
        

        //main server loop
        while(true){
            //this is the method that will check the server socket queue to see if any items have been recieved
            skt = server_skt.accept();

            //have to add the mode from the admin server static variable and pass to thread
            new Worker(skt,JokeServerAdmin.mode).start();
        }

    }

}


//have to extend thread. If using runnable, then the server socket on main server is blocked
//call with start(), not run()
class JokeServerAdmin extends Thread{
    //store the mode as a static string so it can be accessed outside of the class
    //keep static so there is 1 global state per admin server process
    public static String mode = "J";
    private int port;

    //for testing
    JokeServerAdmin(){
        this.port = 5050; //default port for the admin server
    }
    //this constructor will always get called
    JokeServerAdmin(int p){
        this.port = p;
    }

    public void run() {
        BufferedReader reader; //TODO:: check if there is another way to extract socket data in java 8
        String command;
        
        int max_q_size = 6;
        
        
        Socket skt;
        ServerSocket server_skt;
        System.out.println("Starting Akshay Patel's JokeServer admin server at port: " + port);
        try{
            server_skt = new ServerSocket(port,max_q_size); //start the server
            System.out.println("Current mode: " + mode);
            //main loop 
            while(true){
                //blocking call
                skt = server_skt.accept();
    
                //since the socket will block until it recieves data, we can add our logic in the loop itself.
                reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                command = reader.readLine();
                read_command(command);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }

    }


    private void read_command(String command){
        //checking if joke command was sent
        if(command.toLowerCase().equals("j") || command.toLowerCase().equals("joke")){
            if(mode.equals("P")){
                //print if previous mode was proverb
                System.out.println("Current mode: J");
            }
            mode = "J";
        }
        //checking if proverb command was sent
        else if(command.toLowerCase().equals("p") || command.toLowerCase().equals("proverb")){
            if(mode.equals("J")){
                //print if the previous mode was joke
                System.out.println("Current mode: P");
            }
            mode = "P";
        }
        //if nothing was entered in the admin command
        else if(command.isEmpty() || command == null){
            if(mode.equals("J")){
                //print if previous mode was joke
                System.out.println("Current mode: P");
                mode = "P";
            }
            else{
                //print if previous mode was proverb
                System.out.println("Current mode: J");
                mode = "J";
            }
        }
    }

}


class Worker extends Thread{
    //Create nested inner class to keep static
    //dont create constructor
    public static class Responses{
        //converts a number to response letter by index of the array
        //to convert the state of the client to a position in the response dictionary
        public static String[] state_mapping = {"A","B","C","D"};
        
        //jokes from https://bestlifeonline.com/funny-clean-jokes/
        public static Map<String,String> jokes = new HashMap<String,String>() {{
            //add the entries in place
            put("A", "What's the difference between a bird and a fly? A bird can fly, but a fly can't bird!");
            put("B", "What time does a duck wake up? The quack of dawn!");
            put("C", "I couldn't figure out why the baseball kept getting bigger. Then it hit me.");
            put("D", "What do you get when you pour root beer into a square cup? Beer.");
        }};
        
        //proverbs from https://www.phrasemix.com/collections/the-50-most-important-english-proverbs
        public static Map<String,String> proverbs = new HashMap<String,String>(){{
            //add the entries in place
            put("A", "People who live in glass houses should not throw stones.");
            put("B", "Hope for the best, but prepare for the worst.");
            put("C", "There's no such thing as a free lunch.");
            put("D", "Beggars can't be choosers.");
        }};
        
        //reads the index of state_mapping given by num. Use the value to key the hashmap
        public static String get_response(int num, String mode){
            //if joke mode
            if(mode.equals("J")){
                String joke_letter = state_mapping[num%4]; //get state_mapping from num
                String output = jokes.get(joke_letter); //query the key
                return output;
            }
            else{
                String proverb_letter = state_mapping[num%4]; //get state_mapping from num
                String output = proverbs.get(proverb_letter); //query the key
                return output;
            }
        }
    }


    Socket skt;
    String status;
    
    //constructor to save the socket applied to the worker thread
    //for testing
    Worker(Socket s){
        skt = s;
    }
    //only call this constructor
    Worker(Socket s, String stat){
        skt = s;
        status = stat; // only J or P
    }

    
    public void run(){
        PrintStream out = null; //output stream to client
        BufferedReader reader = null;//input stream from client socket
        try {
            reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            out = new PrintStream(skt.getOutputStream());

            
            try {
                String command;
                command = reader.readLine (); //read the string from the socket
                output_function(command, out); //pass the input to the output function
            }
            catch (IOException e) {
                System.out.println(e);
            }
            
            skt.close(); //close the connection so it doesn't persist in memory
        }
        catch (IOException ioe){
            System.out.println(ioe);
        }

    }

    void output_function(String message, PrintStream out) {
        try{
            int num; //the client state for the current mode
            String output;

            //test to get response output by number
            //num = Integer.parseInt(message);
            //output = Responses.get_response(num, status);

            //if joke mode, use index 0 of message for client joke state
            if(status.equals("J")){
                num = Character.getNumericValue(message.charAt(0));
            }
            //proverb mode, use index 1 of message for client proverb state
            else{
                num = Character.getNumericValue(message.charAt(1));
            }

            //extract the username from the message
            String username;
            username = message.substring(3);

            
            output = Responses.get_response(num, status);
            //concat the strings to get the proper output
            output = status + Responses.state_mapping[num%4] + " " + username + ": " + output;
            out.println(output);
        }
        catch(NumberFormatException e){
            System.out.println(e);
        }

    }

}
