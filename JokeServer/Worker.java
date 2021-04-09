import java.io.*;
import java.net.*;
import java.util.*;


class Worker extends Thread{
    public static class Responses{
        public static String[] state_mapping = {"A","B","C","D"};
        
        public static Map<String,String> jokes = new HashMap<String,String>() {{
            put("A", "Joke A");
            put("B", "Joke B");
            put("C", "Joke C");
            put("D", "Joke D");
        }};
        
        public static Map<String,String> proverbs = new HashMap<String,String>(){{
            put("A", "Proverb A");
            put("B", "Proverb B");
            put("C", "Proverb C");
            put("D", "Proverb D");
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

    void output_function(String command, PrintStream out) {
        try{
            int num;
            String output;

            num = Integer.parseInt(command);
            output = Responses.get_response(num, status);

            output = status + Responses.state_mapping[num%4] +  ": " + output;
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
