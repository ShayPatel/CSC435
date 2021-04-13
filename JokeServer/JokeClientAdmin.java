import java.io.*;
import java.net.*;

class JokeClientAdmin{


    public static void main(String[] args) {
        String serverName;
        int port;

        //testing on the default port I assigned to the admin server
        port = 4546;
        serverName = "localhost";


        System.out.println("Akshay Patel's JokeServer Admin Client");
        System.out.println("Using server: " + serverName + ", Port: " + String.valueOf(port));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        
        String command;
        try {
            while(true) {
                System.out.print("Enter a command: ");
                System.out.flush ();
                command = in.readLine ();
                send_command(command,serverName,port);
            }
        }
        catch (IOException x){
            x.printStackTrace ();
        }
    }

    public static void send_command(String command, String serverName, int port){
        Socket skt;
        //BufferedReader fromServer;
        PrintStream toServer;


        try{
            //open the connection to the admin server
            skt = new Socket(serverName, port);
            
            //Create the output stream to send to the server
            toServer = new PrintStream(skt.getOutputStream());

            //sending the command
            toServer.println(command); toServer.flush();

            skt.close();
        }
        catch(IOException e){

        }
    }

}
