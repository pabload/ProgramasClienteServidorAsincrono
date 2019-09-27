
import java.io.File;
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
    private static Map<String, PrintWriter> sesiones = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println(cargarDatos());
        if (cargarDatos()!=null) {
             users=cargarDatos();
        }
        System.out.println("the chat server is running");

        for (Map.Entry<String, Cliente> entry : users.entrySet()) {
            String key = entry.getKey();
            Cliente value = entry.getValue();
            System.out.println(key);
        }
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
                    if (name == null || name.equalsIgnoreCase("quit") || name.isEmpty() || sesiones.containsKey(name)) {
                        System.out.println("entro wewewewewew");
                        continue;
                    }
                    synchronized (users) {
                        if (!users.containsKey(name) && !sesiones.containsValue(out)) {
                            Cliente c = new Cliente(name);
                            users.put(name, c);
                            sesiones.put(name, out);
                            out.println("NAMEACCEPTED " + name);
                            guardarMap(users);
                            for (PrintWriter pw : sesiones.values()) {
                                pw.println("MESSAGE " + name + " joined");
                            }
                            break;
                        } else {
                            if (users.containsKey(name)) {
                                sesiones.put(name, out);
                                out.println("NAMEACCEPTED " + name);
                                for (PrintWriter pw : sesiones.values()) {
                                    pw.println("MESSAGE " + name + " joined again");

                                }
                                System.out.println(users.get(name).bloqueados);
                                break;
                            }
                        }
                    }
                }
                while (true) {
                    String input = in.nextLine();
                    if (input.startsWith("/") && !input.startsWith("/quit") && !input.startsWith("/bloquear") && !input.startsWith("/desbloquear")) {
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
                                                 guardarMap(users);

                                            }

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
                                                     guardarMap(users);
                                                }
                                            }

                                        }
                                    }

                                } else {
                                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaasdsdsdsdsdsa");

                                    for (Map.Entry<String, Cliente> user : users.entrySet()) {
                                        String nombre = user.getKey();
                                        Cliente obejto = user.getValue();
                                        System.out.println("llego aki");
                                        if (!obejto.bloqueados.contains(users.get(name).nombre) && sesiones.containsKey(nombre)) {
                                            System.out.println("llego aki 3");
                                            sesiones.get(nombre).println("MESSAGE " + name + ": " + input);

                                        }

                                    }
                                    /*for (Cliente user : users.values()) {
                                    System.out.println(user.bloqueados);
                                    System.out.println(users.get(name).nombre);
                                    if (!user.bloqueados.contains(users.get(name).nombre)) {
                                        System.out.println("entro a repartir");
                                        System.out.println(sesiones.size());
                                        sesiones.get(user).println("MESSAGE " + name + ": " + input);
                                       
                                    }

                                }*/

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
                    sesiones.remove(name);
                    /* for (Cliente user : users.values()) {
                        sesiones.get(user.nombre).println("MESSAGE " + name + " has left");
                        
                    }*/
                    for (Map.Entry<String, Cliente> user : users.entrySet()) {
                        String nombre = user.getKey();
                        Cliente obejto = user.getValue();
                        System.out.println("llego aki");
                        if (!obejto.bloqueados.contains(users.get(name).nombre) && sesiones.containsKey(nombre)) {
                            System.out.println("llego aki 3");
                            sesiones.get(nombre).println("MESSAGE " + name + ": " + "has left");

                        }

                    }

                }
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }

        }

        public static void guardarMap(Map<String, Cliente> users) {
            try {
                FileOutputStream fos
                        = new FileOutputStream("hashmap.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(users);
                oos.close();
                fos.close();
                System.out.printf("Serialized HashMap data is saved in hashmap.ser");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public static HashMap<String, Cliente> cargarDatos() {
        HashMap<String, Cliente> users = null;
        try {
            File archivo = new File("hashmap.ser");
            if (!archivo.exists()) {
                archivo.createNewFile();

            } else {
                if (archivo.length() == 0) {
                    return users;
                } else {
                    FileInputStream fis = new FileInputStream("hashmap.ser");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    users = (HashMap) ois.readObject();
                    ois.close();
                    fis.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return users;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return users;
        }
        return users;
    }

    public static void guardarMap(Map<String, Cliente> users) {
        try {
            FileOutputStream fos
                    = new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(users);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
