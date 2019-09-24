
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

public class PrincipalServidorBloquear {

    private static Map<String, Cliente> users = new HashMap<>();

    public static void main(String[] args) throws IOException {
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
                    if (name == null || name.equalsIgnoreCase("quit") || name.isEmpty()) {
                        continue;
                    }
                    synchronized (users) {
                        if (!users.containsKey(name)) {
                            Cliente c = new Cliente(name, out);
                            users.put(name, c);
                            out.println("NAMEACCEPTED " + name);

                            for (Cliente user : users.values()) {
                                user.out.println("MESSAGE " + name + " joined");
                            }
                            break;
                        }
                    }
                }
                while (true) {
                    String input = in.nextLine();
                    if (input.startsWith("/") && !input.startsWith("/quit") && !input.startsWith("/bloquear")) {
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
                                        users.get(Receiver).out.println("MESSAGE (PM-FROM-" + name + "): " + message);
                                        users.get(name).out.println("MESSAGE (PM-TO-" + Receiver + "): " + message);
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

                                            /*users.get(Receiver).out.println("MESSAGE (PM-FROM-" + name + "): " + message);
                                        users.get(name).out.println("MESSAGE (PM-TO-" + Receiver + "): " + message);*/
                                            users.get(name).bloqueados.add(bloqueado);
                                            users.get(name).out.println("MESSAGE bloqueaste a: " + bloqueado);
                                        }

                                    }
                                }

                            } else {

                                for (Cliente user : users.values()) {
                                    System.out.println(user.bloqueados);
                                    System.out.println(users.get(name));
                                    if (!user.bloqueados.contains(users.get(name).nombre)) {
                                        user.out.println("MESSAGE " + name + ": " + input);
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
                        user.out.println("MESSAGE " + name + " has left");
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
