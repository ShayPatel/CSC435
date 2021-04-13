import java.io.*;
import java.net.*;

class JokeClientAdmin{


    public static void main(String[] args) {
        String serverName;
        int port;

        
        
        if(args.length == 0){
            //testing on the default port I assigned to the admin server
            port = 50000;
            serverName = "localhost";
        }
        else if(args.length == 1){
            port = 50000;
            serverName = args[0];
        }
        else{
            serverName = args[0];
            port = Integer.parseInt(args[1]);
        }


        System.out.println("Akshay Patel's JokeServer Admin Client");
        System.out.println("Using server: " + serverName + ", Port: " + String.valueOf(port));

        System.out.println("Enter \"j\" or \"joke\" (case insensitive) for joke mode");
        System.out.println("Enter \"p\" or \"proverb\" (case insensitive) for proverb mode");
        System.out.println("Enter \"quit\" (case insensitive) to exit client");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        
        String command;
        try {
            while(true) {
                System.out.print("Enter a command: ");
                System.out.flush ();
                command = in.readLine ();

                if(command.toLowerCase().equals("quit")){
                    System.out.println("Exiting client");
                    return;
                }

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
