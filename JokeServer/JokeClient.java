/*--------------------------------------------------------

1. Akshay Patel 4/13/21:

2. Java version used (java -version), if not the official version for the class:

build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08

3. Precise command-line compilation examples / instructions:

> javac JokeClient.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeClient

The server name and the port number can be passed as command line arguments

> java JokeClient [server name]
> java JokeClientAdmin [server name] [port]

5. List of files needed for running the program.
JokeServer.java
JokeClient.java
JokeClientAdmin.java

5. Notes:
The client stores its own state in the running process. The state is passed as a message to the server. Each client has independent state.
----------------------------------------------------------*/




import java.io.*;
import java.net.*;

public class JokeClient {
    
    //save the client state in the static variable to the process
    //each process should not be able modify others' state
    static int joke_state = 0;
    static int proverb_state = 0;

    public static void main(String[] args) {
        String server_name;
        int port;
        String username;

        //testing on the default port I assigned to the main server
        //port = 50000;
        //server_name = "localhost";

        if(args.length == 0){
            port = 4545; //default port from assignment
            server_name = "localhost";
        }
        //if 1 arg. then set it to the server name
        else if(args.length == 1){
            port = 4545;
            server_name = args[0];
        }
        //arg order: server name then port
        else{
            server_name = args[0];
            port = Integer.parseInt(args[1]);
        }


        System.out.println("Akshay Patel's JokesServer Client");
        System.out.println("Using server: " + server_name + ", Port: " + String.valueOf(port));

        //instructions
        System.out.println("Enter \"quit\" (case insensitive) to exit client");
        System.out.println("Any input other than quit will be ignored");

        
        
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text;
        try {
            //get the username
            System.out.print("\nEnter username: ");
            System.out.flush ();
            username = reader.readLine ();


            while(true) {
                //just prompting the user to press enter
                System.out.print("Press Enter for a response: ");
                System.out.flush();
                text = reader.readLine(); // not really doing anything with it. should I even save the variable?

                //checking for quit condition
                if(text.toLowerCase().equals("quit")){
                    System.out.println("Exiting client");
                    return;//exit main
                }


                //dont really want to refactor this code
                //just pass the username as the text instead of passing the input
                send_text(username,server_name,port);
            }
        }
        catch (IOException e){
            System.out.println(e);;
        }
    }

    public static void send_text(String text, String server_name, int port){
        Socket skt;
        BufferedReader fromServer;
        PrintStream toServer;


        try{
            //open the connection to the admin server
            skt = new Socket(server_name, port);
            
            //Create the output stream to send to the server
            toServer = new PrintStream(skt.getOutputStream());
            //create the input stream to recieve the data from the server
            fromServer = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            //create a message that consists of "{joke state}{proverb state} {username}"
            //the server will parse the message to get the client state directly from the message
            String message = String.format("%d%d %s", joke_state, proverb_state, text);


            //sending the command
            //to_server.println(text); to_server.flush();
            //sending the message to the server
            toServer.println(message);
            toServer.flush();

            String response = fromServer.readLine(); //output formatted by server. no change to this
            System.out.println(response);
            
            //update the client state based on the response from the server
            //if state > 4 then cycle
            if(response.charAt(0) == 'J'){
                joke_state = (joke_state + 1)%4;
                //check cycle
                if(joke_state == 0){
                    System.out.println("JOKE CYCLE COMPLETED");
                }
            }
            else if(response.charAt(0) == 'P'){
                proverb_state = (proverb_state + 1)%4;
                //check cycle
                if(proverb_state == 0){
                    System.out.println("PROVERB CYCLE COMPLETED");
                }
            }
            else{
                System.out.println("Unknown response");
            }
            

            skt.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

}
