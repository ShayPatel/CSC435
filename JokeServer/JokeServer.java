import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class JokeServer{


    public static void main(String[] args) throws IOException{
        int port;
        
        int max_q_size = 6;
        
        if(args.length == 0){
            port = 50000; // default port will be 50000
        }
        else{
            port = 50000;
        }
        
        
        System.out.println("Starting Akshay Patel's JokeServer at port: " + port);
        
        //create new thread that hosts the admin server
        //admin server and main server have to communicate the mode.
        //mode will have to be a saved state in the main server
        //TODO:: admin server here
        new JokeServerAdmin().run();
        


        Socket skt;
        ServerSocket server_skt = new ServerSocket(port,max_q_size);
        
        //main server loop
        while(true){
            //this is the method that will check the server socket queue to see if any items have been recieved
            skt = server_skt.accept();
            //new Worker(skt).start();
        }

    }

}


class JokeServerAdmin implements Runnable{
    //store the mode as a static string so it can be accessed outside of the class
    public static String mode;
    private int port;

    JokeServerAdmin(){
        this.port = 50001;
    }
    JokeServerAdmin(int p){
        this.port = p;
    }

    public void run() {
        BufferedReader in;
        String command;
        
        int max_q_size = 6;
        
        
        Socket skt;
        ServerSocket server_skt;
        try{
            server_skt = new ServerSocket(port,max_q_size);

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
        catch(IOException e){
            System.out.println(e);
        }

    }


    private void read_command(String command){
        System.out.println(command);
    }

}
