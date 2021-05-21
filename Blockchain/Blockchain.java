import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;


import java.security.*;


class Blockchain{

    public static void main(String[] args) {
        block b = new block();
        b.data = "output data";
        try {
            b.hash = utils.hash_string(b.data);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
        b.seed = utils.randomAlphaNumeric(8);

        utils.write_json(b, "output.json");
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
    //wrapper to translate the hash byte array to a string
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

}


class block{
    //TODO:: enter fields given by the assignment
    //serializable block class to contain all the data
    public String data;
    public String hash;
    public String seed;
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