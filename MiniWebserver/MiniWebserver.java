/*
1. Akshay Patel 4/28/21

2. Java version
build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08

3. compile the server with the command
> javac MiniWebserver.java

4. Run the server with the command
> java MiniWebserver

5. List of files needed to run the program
MiniWebserver.java

Notes:
The mini web server has a static function that reads the html file "WebAdd.html"
That is not needed for this program to run as the response generator function has the 
strings hard coded in.


Questions:
1. How mime-types work.
The mime type explains to the browser the format of the incoming data stream.
If text, then the browser converts the strings to a text format and saves as a text file
if html, then the browser renders the page through its rendering engine
Can also send other BLOB data such as images and audio to be interpreted. Really it is up to the browser how to handle the type.

2. How to return the html contents
The output stream to the browser takes text string or byte string content.
You have to specify the type of the content to the browser. You have to specify that in the http reponse.
The content should be properly formatted in HTML to be rendered correctly.

3. How to return the contents as text
Specify the content type as text/plain. Send that to the browser output stream.


*/


import java.io.*;
import java.net.*;
import java.util.*;


public class MiniWebserver {
    

    public static void main(String[] args) throws IOException{
        //default port for assignment will be 2540
        int port = 2540;
        int max_q_size = 6;
        

        Socket skt;
        ServerSocket server_skt = new ServerSocket(port,max_q_size);

        while (true) {
            skt = server_skt.accept();
            new ListenWorker(skt).start();
        }

    }

}

class ListenWorker extends Thread {
    Socket skt;

    ListenWorker(Socket s){
        skt = s;
    }

    public void run(){
        PrintStream out;
        BufferedReader in;

        try{
            out = new PrintStream(skt.getOutputStream());
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            
            String request = in.readLine();
            String[] params = Response.parse_request(request);
            
            
            //send the html output here
            
            //first send the http version and status code
            out.println("HTTP/1.1 200 OK");
            out.println("Connection: close");
            
            //then send the string buffer size for the output
            out.println("Content-Length: 800");
            
            //send the type of the output
            //the data is sent as a string to the browser. It needs context.
            //the browser needs to know what to do with the data in order to pipe it to the correct process.
            //the type is requested as text/html in order to render the page in the browser.
            out.println("Content-Type: text/html \r\n\r\n");
            //uncomment this line to send as text
            //out.println("Content-Type: text/plain \r\n\r\n");
            
            //write the html as a string to the output stream
            Response.generate_html(out,params);
            
            //for(int j=0; j<6; j++){
            //    System.out.println(in.readLine());
            //} 
            //String[] lines = in.lines().toArray(String[]::new);
            //String request = lines[0];
            
                
            
            skt.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
}

class Response{
    //pass the html file webadd to the response
    public static void get_response(PrintStream out){
        //file reading logic borrowed from https://www.techiedelight.com/how-to-read-contents-of-a-file-in-java-8/
        try{
            //read the html source file
            BufferedReader reader = new BufferedReader(new FileReader(new File("WebAdd.html")));
            
            //read the file line by line
            //apply the println to each line of the file
            //should take care of the html output
            reader.lines().forEachOrdered(out::println); //use :: not .
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    //the final html generator function
    public static void generate_html(PrintStream out, String[] params){
        if(params == null){
            get_response(out);
        }
        else{
            //no longer referencing the file as get_response.
            //manually send the dynamic html output
            
            //send the constant part of the html file
            String html = "<HTML><BODY><H1>WebAdd</H1><FORM method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi\">Enter your name and two numbers. My program will return the sum:<p>";
            out.println(html);

            //dynamic part of the name field. Gets the string formatted with the person value from the http query
            html = String.format("<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"%s\"><P>", params[0]);
            out.println(html);

            //dynamic part of the num1 field
            //format the string with the num1 var
            html = String.format("<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"%s\"> <br>", params[1]);
            out.println(html);

            //dynamic part of the num2 field
            //format the string with the num2 var
            html = String.format("<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"%s\"> <br>", params[2]);
            out.println(html);

            //send the button;
            html = "<INPUT TYPE=\"submit\" VALUE=\"Submit Numbers\"></FORM>";
            out.println(html);

            //dynamically send the results back as a line of text
            int result = Integer.parseInt(params[1]) + Integer.parseInt(params[2]);
            html = String.format("Hello %s. Your sum is %d", params[0],result);
            out.println(html);
            
            //the closing html tags
            html = "</BODY></HTML>";
            out.println(html);
        }
    }

    public static String[] parse_request(String request) throws MalformedURLException{
        //request parsing function borrowed from https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
        request = request.split(" ")[1];
        //add the protocol and host to prevent the malformed url login error
        URL url = new URL(new URL("http://localhost:2540"),request);
        //easy way to extract the query parameters passed into the url
        request = url.getQuery();
        //System.out.println(request);
        //if the query is empty, then return null
        if(request == null){
            return null;
        }


        //the structure of the request is known so we don't need to loop or store in a map
        String[] pairs = request.split("&");
        
        //error when splitting on +
        //solved with https://stackoverflow.com/questions/40246231/java-util-regex-patternsyntaxexception-dangling-meta-character-near-index-0
        String person = pairs[0].split("=")[1].replaceAll("\\+", " "); //have to escape the + with \\
        String num1 = pairs[1].split("=")[1];
        String num2 = pairs[2].split("=")[1];
        
        //return the array of the query parameters.
        String[] output = new String[] {person, num1, num2};
        return output;
    }
}