
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class PrincipalServidorBloquear {

    private static Map<String, Cliente> users = new HashMap<>();
    private static Map<String,PrintWriter> sesiones = new HashMap<>();
    public static void main(String[] args) throws IOException {
   /* Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                guardarMap(users);
            }
        });*/
        users=cargarDatos();
        System.out.println(users.isEmpty());
        System.out.println("the chat server is running");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            System.out.println(listener.getInetAddress());
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }

        }

    }

    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
       
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
               
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null || name.equalsIgnoreCase("quit") || name.isEmpty()){
                        System.out.println("entro wewewewewew");
                        continue;
                    }
                    synchronized (users) {
                        System.out.println(sesiones.containsValue(out));
                        System.out.println(sesiones.containsKey(name));
                        for (Cliente object : users.values()) {
                            System.out.println(object.nombre);
                        }
                        
                        if (!users.containsKey(name)&& !sesiones.containsValue(out)) {
                            Cliente c = new Cliente(name);
                            users.put(name, c);
                            sesiones.put(name, out);
                            out.println("NAMEACCEPTED " + name);
                            guardarMap(users);
                            for (PrintWriter pw : sesiones.values()) {
                                pw.println("MESSAGE " + name + " joined");
                            }
                            /*for (Cliente user : users.values()) {
                                user.out.println("MESSAGE " + name + " joined");
                            }*/
                            break;
                        }else{
                            if (users.containsKey(name)) {
                                sesiones.put(name, out);
                                out.println("NAMEACCEPTED " + name);
                               for (PrintWriter pw : sesiones.values()) {
                                pw.println("MESSAGE " + name + " joined");
                            }
                                break;
                            }
                        }
                    }
                }
                while (true) {
                    String input = in.nextLine();
                    if (input.startsWith("/") && !input.startsWith("/quit") && !input.startsWith("/bloquear")&&!input.startsWith("/desbloquear")) {
                        System.out.println("enntro1");
                        if (input.indexOf(" ") >= 0) {
                            System.out.println("enntro2");
                            int inp = input.indexOf(' ');
                            String Receiver = input.substring(1, inp);
                            String message = input.substring(inp, input.length());
                            System.out.println(message);
                            if (users.containsKey(Receiver)) {
                                if (!name.equals(Receiver)) {
                                    if (!users.get(Receiver).bloqueados.contains(users.get(name).nombre)) {
                                        sesiones.get(users.get(Receiver).nombre).println("MESSAGE (PM-FROM-" + name + "): " + message);
                                        sesiones.get(users.get(name).nombre).println("MESSAGE (PM-FROM-" + Receiver + "): " + message);
                                    }
                                }

                            }
                        }
                    } else {
                        if (input.toLowerCase().startsWith("/quit")) {
                            return;
                        } else {
                            if (input.toLowerCase().startsWith("/bloquear")) {
                                if (input.indexOf(" ") >= 0) {
                                    System.out.println("entro aki bro");
                                    int inp = input.indexOf(' ') + 1;
                                    String bloqueado = input.substring(inp);
                                    System.out.println(users.containsKey(bloqueado));
                                    if (users.containsKey(bloqueado)) {
                                        System.out.println("entro aki bro 2");
                                        if (!name.equals(bloqueado)) {
                                            if (!users.get(name).bloqueados.contains(bloqueado)) {
                                                users.get(name).bloqueados.add(bloqueado);
                                                sesiones.get(users.get(name).nombre).println("MESSAGE bloqueaste a: " + bloqueado);
                                                
                                            }
                                            /*users.get(Receiver).out.println("MESSAGE (PM-FROM-" + name + "): " + message);
                                        users.get(name).out.println("MESSAGE (PM-TO-" + Receiver + "): " + message);*/
                                           
                                        }

                                    }
                                }

                            } else {
                                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
                                if (input.toLowerCase().startsWith("/desbloquear")) {
                                  System.out.println("entro al desbloquea");
                                if (input.indexOf(" ") >= 0) {
                                    int inp = input.indexOf(' ') + 1;
                                    String desbloqueado = input.substring(inp);
                                    System.out.println(users.containsKey(desbloqueado));
                                    if (users.containsKey(desbloqueado)) {
                                        System.out.println("entro al desbloquea");
                                        if (!name.equals(desbloqueado)) {
                                            if (users.get(name).bloqueados.contains(desbloqueado)) {
                                                users.get(name).bloqueados.remove(desbloqueado);
                                                sesiones.get(users.get(name).nombre).println("MESSAGE desbloqueaste a: " + desbloqueado);
                                            }
                                            /*users.get(Receiver).out.println("MESSAGE (PM-FROM-" + name + "): " + message);
                                        users.get(name).out.println("MESSAGE (PM-TO-" + Receiver + "): " + message);*/
                                           
                                        }

                                    }
                                }

                            }else{
                                      System.out.println("aaaaaaaaaaaaaaaaaaaaaaasdsdsdsdsdsa");
                                     for (Cliente user : users.values()) {
                                    System.out.println(user.bloqueados);
                                    System.out.println(users.get(name).nombre);
                                    if (!user.bloqueados.contains(users.get(name).nombre)) {
                                        System.out.println("entro a repartir");
                                        System.out.println(sesiones.get(user));
                                        sesiones.get(user.nombre).println("MESSAGE " + name + ": " + input);
                                       
                                    }

                                }
                                    
                                }
                               
                            }
                        }

                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null || name != null) {
                    System.out.println(name + " is leaving");
                    users.remove(name);
                    for (Cliente user : users.values()) {
                        sesiones.get(user.nombre).println("MESSAGE " + name + " has left");
                        
                    }

                }
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }

        }
           public static void guardarMap(Map<String, Cliente> users){
        try
           {
                  FileOutputStream fos =
                  new FileOutputStream("hashmap.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(users);
                  oos.close();
                  fos.close();
                  System.out.printf("Serialized HashMap data is saved in hashmap.ser");
           }catch(IOException ioe)
            {
                  ioe.printStackTrace();
            }
    }
        
    }
       public static  HashMap<String, Cliente>cargarDatos(){
      HashMap<String, Cliente> users = null;
        try
      {
         FileInputStream fis = new FileInputStream("hashmap.ser");
         ObjectInputStream ois = new ObjectInputStream(fis);
         users = (HashMap) ois.readObject();
         ois.close();
         fis.close();
         System.out.println(users);
      }catch(IOException ioe)
      {
         ioe.printStackTrace();
         return users;
      }catch(ClassNotFoundException c)
      {
         System.out.println("Class not found");
         c.printStackTrace();
         return users;
      }
        return users;
  }
    

}
