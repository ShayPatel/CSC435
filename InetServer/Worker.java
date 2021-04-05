import java.io.*;
import java.net.*;

class Worker extends Thread{
    Socket skt;

    //constructor to save the socket applied to the worker thread
    Worker(Socket s){
        skt = s;
    }

    public void run(){
        PrintStream out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            out = new PrintStream(skt.getOutputStream());

            
            try {
                String name;
                name = in.readLine ();
                System.out.println("Looking up " + name);
                printRemoteAddress(name, out);
            }
            catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace ();
            }
            skt.close(); //close the connection so it doesn't persist in memory
        }
        catch (IOException ioe){
            System.out.println(ioe);
        }

    }

    static void printRemoteAddress (String name, PrintStream out) {
        try {
            out.println("Looking up " + name + "...");
            InetAddress machine = InetAddress.getByName (name);
            out.println("Host name : " + machine.getHostName ()); // To client...
            out.println("Host IP : " + toText (machine.getAddress ()));
        }
        catch(UnknownHostException ex) {
            out.println ("Failed in atempt to look up " + name);
        }
    }
       
    // Not interesting to us:
    static String toText (byte ip[]) { /* Make portable for 128 bit format */
        StringBuffer result = new StringBuffer ();
        for (int i = 0; i < ip.length; ++ i) {
            if (i > 0) result.append (".");
            result.append (0xff & ip[i]);
        }
        return result.toString ();
    }
}
