import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JokeServer{


    public static void main(String[] args) throws IOException{
        int port;
        
        int max_q_size = 6;
        
        if(args.length == 0){
            port = 4545; // default port will be 4545
        }
        else{
            port = 4545;
        }
        
        
        
        //create new thread that hosts the admin server
        //admin server and main server have to communicate the mode.
        //mode will have to be a saved state in the main server
        new JokeServerAdmin().start();
        
        
        Socket skt;
        ServerSocket server_skt = new ServerSocket(port,max_q_size);
        System.out.println("Starting Akshay Patel's JokeServer at port: " + port);
        

        //main server loop
        while(true){
            //this is the method that will check the server socket queue to see if any items have been recieved
            skt = server_skt.accept();

            //have to add the mode from the admin server static variable and pass to thread
            new Worker(skt,JokeServerAdmin.mode).start();
        }

    }

}


//have to extend thread. If using runnable, then the server socket on main server is blocked
class JokeServerAdmin extends Thread{
    //store the mode as a static string so it can be accessed outside of the class
    //keep static so there is 1 global state
    public static String mode = "J";
    private int port;

    JokeServerAdmin(){
        this.port = 4546; //default port for the admin server
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
        System.out.println("Starting Akshay Patel's JokeServer admin server at port: " + port);
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
        if(command.toLowerCase().equals("j") || command.toLowerCase().equals("joke")){
            mode = "J";
        }
        else if(command.toLowerCase().equals("p") || command.toLowerCase().equals("proverb")){
            mode = "P";
        }
    }

}

