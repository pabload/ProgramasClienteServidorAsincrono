
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

public class PrincipalServidorBloquear  {
    //private static Map<String, PrintWriter> users = new HashMap<>();
    private static ArrayList<Cliente> clientes= new ArrayList<>();
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
                    synchronized(clientes){
                        if(!clientes.contains(name)){
                            System.out.println("PrincipalServidorBloquear.Handler.run()");
                            Cliente c =new Cliente(name, out);
                            clientes.add(c);
                            out.println("NAMEACCEPTED " + name);
                            for (Cliente cliente : clientes) {
                                cliente.out.println("MESSAGE " + name + " joined");
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
                   System.out.println(Receiver);
                   System.out.println(message);
                    System.out.println(clientes.contains(Receiver));
                    if (clientes.contains(Receiver)) {
                       System.out.println("clientes tiene al conetendor");
                        if (!name.equals(Receiver)) {
                            for (Cliente cliente : clientes) {
                                System.out.println("enntroal ciclo");
                                if (cliente.nombre.equals(Receiver)) {
                                      System.out.println("enntroakixd");
                                    cliente.out.println("MESSAGE (PM-FROM-" + name + "): " + message);
                                }
                                if (cliente.nombre.equals(name)) {
                                    System.out.println("enntroakixd2");
                                    cliente.out.println("MESSAGE (PM-TO-" + Receiver + "): " + message);
                                }
                            }
                        }
                       
                    }
                       System.out.println("asdasdasdasdasdas");
                    }
                } else {
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (Cliente cliente : clientes) {
                      cliente.out.println("MESSAGE " + name + ": " + input);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            if(out != null || name != null){
                System.out.println(name+" is leaving");
                clientes.remove(name);
                //users.remove(name);
                for (Cliente cliente : clientes) {
                    cliente.out.println("MESSAGE "+name+" has left");
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
