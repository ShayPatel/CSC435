/*--------------------------------------------------------

1. Akshay Patel 4/13/21:

2. Java version used (java -version), if not the official version for the class:

build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08

3. Precise command-line compilation examples / instructions:

> javac JokeClientAdmin.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeClientAdmin

The server name and the port can be specified as command line args. The port of the host can also be specified with the server name

> java JokeClientAdmin [server name]
> java JokeClientAdmin [server name] [port]

5. List of files needed for running the program.
JokeServer.java
JokeClient.java
JokeClientAdmin.java

5. Notes:

The admin client is resilient to variants of the input commands. Can exit with "quit". The client can be started or ended at any time without impact on the server.
----------------------------------------------------------*/



import java.io.*;
import java.net.*;

class JokeClientAdmin{


    public static void main(String[] args) {
        String server_name;
        int port;

        
        
        if(args.length == 0){
            //testing on the default port I assigned to the admin server
            port = 5050;
            server_name = "localhost";
        }
        else if(args.length == 1){
            port = 5050;
            server_name = args[0];
        }
        else{
            server_name = args[0];
            port = Integer.parseInt(args[1]);
        }


        System.out.println("Akshay Patel's JokeServer Admin Client");
        System.out.println("Using server: " + server_name + ", Port: " + String.valueOf(port));


        //input instructions
        System.out.println("Enter \"j\" or \"joke\" (case insensitive) for joke mode");
        System.out.println("Enter \"p\" or \"proverb\" (case insensitive) for proverb mode");
        System.out.println("Press Enter to switch to alternate mode");
        System.out.println("Enter \"quit\" (case insensitive) to exit client");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        
        String command;
        try {
            while(true) {
                //enter the input for the admin server
                System.out.print("Enter a command: ");
                System.out.flush ();
                //read the command
                command = in.readLine ();

                //check for quit before sending message
                if(command.toLowerCase().equals("quit")){
                    System.out.println("Exiting client");
                    return;
                }

                send_command(command,server_name,port);
            }
        }
        catch (IOException e){
            System.out.println(e);;
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
