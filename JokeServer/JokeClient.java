import java.io.*;
import java.net.*;

import javax.swing.text.DefaultStyledDocument.ElementSpec;

public class JokeClient {
    
    //save the client state in the static variable to the process
    static int joke_state = 0;
    static int proverb_state = 0;

    public static void main(String[] args) {
        String serverName;
        int port;
        String username;

        //testing on the default port I assigned to the main server
        //port = 50000;
        //serverName = "localhost";

        if(args.length == 0){
            port = 4545;
            serverName = "localhost";
        }
        //if 1 arg. then set it to the server name
        else if(args.length == 1){
            port = 4545;
            serverName = args[0];
        }
        //arg order: server name then port
        else{
            serverName = args[0];
            port = Integer.parseInt(args[1]);
        }


        System.out.println("Akshay Patel's JokesServer Client");
        System.out.println("Using server: " + serverName + ", Port: " + String.valueOf(port));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


        
        String text;
        try {
            //get the username
            System.out.print("\nEnter username: ");
            System.out.flush ();
            username = in.readLine ();


            while(true) {
                //just prompting the user to press enter
                System.out.print("Press Enter for a response: ");
                System.out.flush ();
                text = in.readLine ();

                if(text.toLowerCase().equals("quit")){
                    System.out.println("Exiting client");
                    return;
                }


                //dont really want to refactor this code
                //just pass the username as the text instead of passing the input
                send_text(username,serverName,port);
            }
        }
        catch (IOException x){
            x.printStackTrace ();
        }
    }

    public static void send_text(String text, String serverName, int port){
        Socket skt;
        BufferedReader fromServer;
        PrintStream toServer;


        try{
            //open the connection to the admin server
            skt = new Socket(serverName, port);
            
            //Create the output stream to send to the server
            toServer = new PrintStream(skt.getOutputStream());
            //create the input stream to recieve the data from the server
            fromServer = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            //create a message that consists of "{joke state}{proverb state} {username}"
            //the server will parse the message to get the client state directly from the message
            String message = String.format("%d%d %s", joke_state, proverb_state, text);


            //sending the command
            //toServer.println(text); toServer.flush();
            //sending the message to the server
            toServer.println(message); toServer.flush();

            String response = fromServer.readLine();
            System.out.println(response);
            
            if(response.charAt(0) == 'J'){
                joke_state = (joke_state + 1)%4;
                if(joke_state == 0){
                    System.out.println("JOKE CYCLE COMPLETED");
                }
            }
            else if(response.charAt(0) == 'P'){
                proverb_state = (proverb_state + 1)%4;
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
