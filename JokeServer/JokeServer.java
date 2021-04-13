import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JokeServer{


    public static void main(String[] args) throws IOException{
        int port;
        int admin_port;
        
        int max_q_size = 6;
        
        if(args.length == 0){
            port = 4545; // default port will be 4545
            admin_port = 5050; //default admin port is 50000
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
class JokeServerAdmin extends Thread{
    //store the mode as a static string so it can be accessed outside of the class
    //keep static so there is 1 global state
    public static String mode = "J";
    private int port;

    JokeServerAdmin(){
        this.port = 5050; //default port for the admin server
    }
    JokeServerAdmin(int p){
        this.port = p;
    }

    public void run() {
        BufferedReader in;
        String command;
        
        int max_q_size = 6;
        
        
        Socket skt;
        ServerSocket server_skt;
        System.out.println("Starting Akshay Patel's JokeServer admin server at port: " + port);
        try{
            server_skt = new ServerSocket(port,max_q_size);
            System.out.println("Current mode: " + mode);
            //main loop 
            while(true){
                //blocking call
                skt = server_skt.accept();
    
                //since the socket will block until it recieves data, we can add our logic in the loop itself.
                in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                command = in.readLine();
                read_command(command);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }

    }


    private void read_command(String command){
        if(command.toLowerCase().equals("j") || command.toLowerCase().equals("joke")){
            if(mode.equals("P")){
                System.out.println("Current mode: J");
            }
            mode = "J";
        }
        else if(command.toLowerCase().equals("p") || command.toLowerCase().equals("proverb")){
            if(mode.equals("J")){
                System.out.println("Current mode: P");
            }
            mode = "P";
        }
        else if(command.isEmpty() || command == null){
            if(mode.equals("J")){
                System.out.println("Current mode: P");
                mode = "P";
            }
            else{
                System.out.println("Current mode: J");
                mode = "J";
            }
        }
    }

}


class Worker extends Thread{
    public static class Responses{
        public static String[] state_mapping = {"A","B","C","D"};
        
        public static Map<String,String> jokes = new HashMap<String,String>() {{
            put("A", "What's the difference between a bird and a fly? A bird can fly, but a fly can't bird!");
            put("B", "What time does a duck wake up? The quack of dawn!");
            put("C", "I couldn't figure out why the baseball kept getting bigger. Then it hit me.");
            put("D", "What do you get when you pour root beer into a square cup? Beer.");
        }};
        
        public static Map<String,String> proverbs = new HashMap<String,String>(){{
            put("A", "People who live in glass houses should not throw stones.");
            put("B", "Hope for the best, but prepare for the worst.");
            put("C", "There's no such thing as a free lunch.");
            put("D", "Beggars can't be choosers.");
        }};
        
        public static String get_response(int num, String mode){
            if(mode.equals("J")){
                String joke_letter = state_mapping[num%4];
                String output = jokes.get(joke_letter);
                return output;
            }
            else{
                String proverb_letter = state_mapping[num%4];
                String output = proverbs.get(proverb_letter);
                return output;
            }
        }
    }


    Socket skt;
    String status;
    String cookie;
    String user;
    
    //constructor to save the socket applied to the worker thread
    Worker(Socket s){
        skt = s;
    }
    Worker(Socket s, String stat){
        skt = s;
        status = stat;
    }
    Worker(Socket s, String stat, String c, String u){
        skt = s;
        status = stat;
        cookie = c;
        user = u;
    }

    
    public void run(){
        PrintStream out = null; //output stream to client
        BufferedReader in = null;//input stream from client socket
        try {
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            out = new PrintStream(skt.getOutputStream());

            
            try {
                String command;
                command = in.readLine (); //read the string from the socket
                output_function(command, out); //pass the input to the output function
            }
            catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace ();
            }
            
            skt.close(); //close the connection so it doesn't persist in memory
        }
        catch (IOException ioe){
            System.out.println(ioe);
        }

    }

    void output_function(String message, PrintStream out) {
        try{
            int num;
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
            output = status + Responses.state_mapping[num%4] + " " + username + ": " + output;
            out.println(output);
        }
        catch(NumberFormatException e){
            System.out.println(e);
        }

        //just a reference to the INET code to print onto the client terminal
        //out.println("Host name : " + machine.getHostName ());
        //out.println("Host IP : " + toText (machine.getAddress ()));
    }
       
    // reusing from INET code
    static String toText (byte ip[]) { /* Make portable for 128 bit format */
        StringBuffer result = new StringBuffer ();
        for (int i = 0; i < ip.length; ++ i) {
            if (i > 0) result.append (".");
            result.append (0xff & ip[i]);
        }
        return result.toString ();
    }

}
