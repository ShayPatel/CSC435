import java.io.*;
import java.net.*;

public class JokeClient {


    public static void main(String[] args) {
        String serverName;
        int port;

        //testing on the default port I assigned to the main server
        port = 50000;
        serverName = "localhost";


        System.out.println("Akshay Patel's JokesServer Client");
        System.out.println("Using server: " + serverName + ", Port: " + String.valueOf(port));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        
        String text;
        try {
            while(true) {
                System.out.print("Enter a line: ");
                System.out.flush ();
                text = in.readLine ();
                send_text(text,serverName,port);
            }
        }
        catch (IOException x){
            x.printStackTrace ();
        }
    }

    public static void send_text(String text, String serverName, int port){
        Socket skt;
        //BufferedReader fromServer;
        PrintStream toServer;


        try{
            //open the connection to the admin server
            skt = new Socket(serverName, port);
            
            //Create the output stream to send to the server
            toServer = new PrintStream(skt.getOutputStream());

            //sending the command
            toServer.println(text); toServer.flush();

            skt.close();
        }
        catch(IOException e){

        }
    }

}
