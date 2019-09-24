
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;


public class PrincipalServidor  {
    private static Map<String, PrintWriter> users = new HashMap<>();
    public static void main(String[] args) throws IOException {
        System.out.println("the chat server is running");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try(ServerSocket listener = new ServerSocket(59001)){
            System.out.println(listener.getInetAddress());
            while (true) {                
                pool.execute(new Handler(listener.accept()));
            }
 
        }
        
    }
    private static class Handler implements Runnable{
        private String name;
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
    
    
    public  Handler(Socket socket){
       this.socket=socket;
    }
    
    public void run (){
        try {
            in= new Scanner(socket.getInputStream());
            out= new PrintWriter(socket.getOutputStream(),true);
             while (true) {                    
                    out.println("SUBMITNAME");
                    name= in.nextLine();
                    if(name==null || name.equalsIgnoreCase("quit") || name.isEmpty()){
                        continue;
                    }
                    synchronized(users){
                        if(!users.containsKey(name)){
                            users.put(name, out);
                            out.println("NAMEACCEPTED " + name);
                
                            for(PrintWriter writer : users.values()){
                                writer.println("MESSAGE " + name + " joined");
                            }
                            break;
                        }
                    }
                }
            while (true) {
                String input = in.nextLine();
                if (input.startsWith("/") && !input.startsWith("/quit")) {
                    System.out.println("enntro1");
                    if (input.indexOf(" ")>=0) {
                     System.out.println("enntro2");
                    int inp = input.indexOf(' ');
                    String Receiver = input.substring(1, inp);
                    String message = input.substring(inp, input.length());
                    if (users.containsKey(Receiver)) {
                        if (!name.equals(Receiver)) {
                         users.get(Receiver).println("MESSAGE (PM-FROM-" + name + "): " + message);
                        users.get(name).println("MESSAGE (PM-TO-" + Receiver + "): " + message); 
                        }
                       
                    }  
                    }
                } else {
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : users.values()) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            if(out != null || name != null){
                System.out.println(name+" is leaving");
                users.remove(name);
                for (PrintWriter writer : users.values()) {
                    writer.println("MESSAGE "+name+" has left");
                }
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        
    }
    }
    
}
