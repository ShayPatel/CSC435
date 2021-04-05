import java.io.*;
import java.net.*;

public class InetClient {
    
    public static void main(String[] args) {
        String serverName;
        int port;

        if(args.length == 0){
            serverName = "localhost";
            port = 50000;
        }
        //check if the first arg is an integer
        else if(args.length == 1){
            try{
                //if int. then set port to that int
                port = Integer.parseInt(args[1]);
                serverName = "localhost";
            }
            catch(NumberFormatException e){
                //if not int, then set server name
                serverName = args[0]; //this server name is for network hosts
                port = 5000;
            }
        }
        //if 2 args are passed
        else{
            //server name first
            serverName = args[0];
            try{
                port = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e){
                port = 50000;
            }
        }

        System.out.println("Akshay Patel's Inet Client");
        System.out.println("Using server: " + serverName + ", Port: " + String.valueOf(port));

        //read the user input
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            String name;
            do {
                System.out.print("Enter a hostname or an IP address, (quit) to end: ");
                System.out.flush ();
                name = in.readLine ();
                if (name.indexOf("quit") < 0){
                    //adding port in the function call to reduce constants in code
                    getRemoteAddress(name, serverName, port);
                }
            }
            while (name.indexOf("quit") < 0);
            System.out.println ("Cancelled by user request.");
        }
        catch (IOException x){
            x.printStackTrace ();
        }
    }
    
    //added port arg
    static void getRemoteAddress (String name, String serverName, int port){
        Socket skt;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
       
        try{
            //unlike the server, we simply push the item through a socket. From here it actually looks like the
            //socket object is a connection that "streams" the data.
            skt = new Socket(serverName, port);
        
            //this is where the connection is being created to the server
            fromServer = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            toServer = new PrintStream(skt.getOutputStream());

            // sending the data to the server
            toServer.println(name); toServer.flush();
            //not explicitly recieving input object, so it's probably being streamed back to the BufferedReader object

            //a blocking call to get the data
            for (int i = 1; i <=3; i++){
                //either the readline function makes a call to the server for data, or it has its own internal
                //queue and acts like a server to wait for the incoming data to populate the buffer
                textFromServer = fromServer.readLine();
                if (textFromServer != null){
                    System.out.println(textFromServer);
                }
            }
            skt.close();
        }
        catch (IOException x) {
            System.out.println ("Socket error.");
            x.printStackTrace ();
        }
    }

    //is this function even necessary in this class? We don't even seem to use it
    static String toText (byte ip[]) {
        StringBuffer result = new StringBuffer ();
        for (int i = 0; i < ip.length; ++ i) {
            if (i > 0){
                result.append (".");
            }
            result.append (0xff & ip[i]);
        }
        return result.toString ();
    }
       
}

