import java.io.IOException;
import java.net.*;

public class JokeServer{


    public static void main(String[] args) {
        int port;
        Socket skt;
        
        int max_q_size = 6;
     
        if(args.length == 0){
            port = 50000; // default port will be 50000
        }
        else{
            port = 50000;
        }


        ServerSocket server_skt = new ServerSocket(port,max_q_size);


        //create new thread that hosts the admin server
        //admin server and main server have to communicate the mode.
        //mode will have to be a saved state in the main server
        //TODO:: admin server here
        


        //main server loop
        while(true){
            //this is the method that will check the server socket queue to see if any items have been recieved
            skt = server_skt.accept();
            new Worker(skt).start();
        }

    }

}


class JokeServerAdmin implements Runnable{
    //store the mode as a static string so it can be accessed outside of the class
    public static String mode;
    private int port;

    public JokeClientAdmin(){
        port = 50001;
    }
    public JokeClientAdmin(int p){
        port = p;
    }

    public void run(){
        Socket skt;
        BufferedReader in;
        String command;
        
        int max_q_size = 6;

        
        try{
            ServerSocket server_skt = new ServerSocket(port,max_q_size);
        }
        catch(IOException e){
            System.out.println(e);
        }

        
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


    private void read_command(String command){
        System.out.println(command);
    }

}
