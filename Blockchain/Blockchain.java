import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
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

}

class block{

    public void work(String data){
        
    }

}