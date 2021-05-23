import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.Field;
import java.security.*;


class Blockchain{

    public static void main(String[] args) {
        
    }

}

class utils{

    //borrowed from the class code
    //credit to:
    //https://www.quickprogrammingtips.com/java/how-to-generate-sha256-hash-in-java.html  @author JJ
    //https://dzone.com/articles/generate-random-alpha-numeric  by Kunal Bhatia  ·  Aug. 09, 12 · Java Zone
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
    }
    public static String ByteArrayToString(byte[] ba){
		StringBuilder hex = new StringBuilder(ba.length * 2);
		for(int i=0; i < ba.length; i++){
			hex.append(String.format("%02X", ba[i]));
		}
		return hex.toString();
    }
    
    //wrapper function to hash the input string
    public static byte[] hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //create the digest on the sha 256 algorithm
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        //perform the hash. gets the output as a byte array
        //class code converts to utf encoding. Don't know why
        byte[] output_hash = digest.digest(s.getBytes("UTF-8"));

        //should probably convert to string here, but probably more modular if I don't
        return output_hash;
    }
    //wrapper to translate the input string to hash byte array to a string
    public static String hash_string(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        byte[] output_hash = hash(s);
        String output = ByteArrayToString(output_hash);
        return output;
    }

    //wrapper function to read the json into a block
    public static block read_json(String filename){
        //create json deserializer
        Gson gson = new Gson();

        //read the file and serialize it to the block class
        try (Reader reader = new FileReader(filename)){
            block b = gson.fromJson(reader, block.class);
            return b;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    //wrapper function to write a block to the given filename
    public static void write_json(block b, String filename){
        //from the class code to write the json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try( FileWriter writer = new FileWriter(filename)){
            gson.toJson(b, writer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    //iterate over all the fields in a block
    public static String iterate_block(block b){
        //from https://stackoverflow.com/questions/17095628/loop-over-all-fields-in-a-java-class

        Field[] fields = b.getClass().getDeclaredFields();
        
        String block_data = "";
        try {
            for(Field f: fields){
                Class t = f.getType();
                Object v = f.get(b);
                
                //check if the field is a string
                if(t == String.class && v != null){
                    //concatenate to the data string
                    block_data += v;
                }
                else if (t == int.class){
                    block_data += String.valueOf(v);
                }
            }    
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
             e.printStackTrace();
        }
        return block_data;
    }

}


class block implements Serializable{
    //TODO:: enter fields given by the assignment

}


class blockchain{

    public void work(String data){
        //TODO: update to ensure that the work is consistent with the assignment

        //string to store the random seed for the answer
        String rand;
        //placeholder string for the concatenatation
        String concat;
        String hash;
        int answer;

        try {
            //keep generating until an answer has been found
            do{
                //generate a random string and concatenate with the data
                rand = utils.randomAlphaNumeric(8);
                concat = data + rand;

                //perform the hash of the new string
                hash = utils.hash_string(concat);
                
                //take the first 4 characters and parse to hex
                answer = Integer.parseInt(hash.substring(0,4),16);

                //TODO: sleep here
                
                //TODO: check if the blockchain has been updated
                //if the chain is updated, then break from the loop

            }while(answer > 20000);


        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
    }
}



class Node{



    class unverified_block_server implements Runnable{
        /*
        Server to handle incoming new unverified blocks.
        Starts a server socket and expects block type objects.
        Once recieved, starts the unverified block worker to add the block to the processing queue.
        */

        //server variables
        int port;
        Socket skt;
        int max_q_size = 12;

        unverified_block_server(int p){
            port = p;
        }

        public void run(){
            /*
            starts the server
            */

            try{
                ServerSocket server = new ServerSocket(port,max_q_size);
                while(true){
                    skt = server.accept();

                    //start the worker to add to the queue
                    new unverified_block_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class unverified_block_worker extends Thread{
        /*
        A worker thread to read an incoming socket request.
        The data should be a block object. Once read, the worker adds the block to the processing queue.
        */

        Socket skt;

        unverified_block_worker(Socket s){
            skt = s;
        }

        public void run(){
            //TODO:: read the socket and convert to block
            
            //TODO:: add the new block to the processing queue
        }
    }

    class verified_block_server implements Runnable{
        /*
        Server to handle incoming verified blocks.
        Starts a server socket and expects block type objects.
        Once recieved, starts the verifified block worker to add the block to the ledger.
        The worker will also muticast the verified block out to the child nodes
        */

        //server variables
        int port;
        Socket skt;
        int max_q_size = 12;

        verified_block_server(int p){
            port = p;
        }

        public void run(){

            try{
                ServerSocket server = new ServerSocket(port,max_q_size);
                while(true){
                    skt = server.accept();

                    new verified_block_worker(skt).start();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class verified_block_worker extends Thread{
        Socket skt;

        verified_block_worker(Socket s){
            skt = s;
        }

        public void run(){
            //TODO:: read the socket and convert to block
            
            //TODO:: add the new block to the ledger
        }
    }

}