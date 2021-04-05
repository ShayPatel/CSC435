import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class InetServer {
    public static void main(String[] args) throws IOException{
        //set a queue length. The queue is defined in the socket object
        int max_q_size = 6;

        int port;
        //adding ability to set the port on startup. This will be easier to test which ports work and which don't
        if(args.length == 0){
            //the default port will be 50000
            port = 50000;
        }
        else{
            try{
                //convert the arg to int
                port = Integer.parseInt(args[0]);
                //checking for the ephemeral ports
                if(port < 49152 || port > 65535){
                    port = 50000;
                }
            }
            catch(NumberFormatException e){
                port = 50000;
            }
        }

        //These are the objects that act as the recievers in our system. 
        Socket skt; //seems like the socket object is the child object that handles the incoming item. Rather, it is likely a container holding the data(byte strings) from each incoming item.
        ServerSocket server_skt = new ServerSocket(port,max_q_size); //different from the socket object. This one looks like it holds the incoming items to be stores into the socket objects. A glorified queue with networking properties.

        System.out.println("Akshay Patel's Inet Server");
        System.out.println("Created the Server socket object with properties:");
        System.out.println(server_skt.toString());

        while(true){
            //this is the method that will check the server socket queue to see if any items have been recieved
            skt = server_skt.accept();
            new Worker(skt).start();
        }

        //should probably do server_skt.close() somewhere at the end. Have to look up java exit functionality
    }
}
