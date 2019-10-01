//final
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

public class PrincipalServidor {

    private static Map<String, PrintWriter> users = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("El servidor esta arriba");
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
                    out.println("NOMBREDEENVIO");
                    name = in.nextLine();
                    if (name.equals("null")) {
                        return;
                    }
                    if (name == null || name.equalsIgnoreCase("salir") || name.isEmpty()) {
                        continue;
                    }
                    synchronized (users) {
                        if (!users.containsKey(name)) {
                            users.put(name, out);
                            out.println("NOMBREACEPTADO " + name);

                            for (PrintWriter writer : users.values()) {
                                writer.println("MENSAJE " + name + " se unio");
                            }
                            break;
                        }
                    }
                }
                while (true) {
                    String input = in.nextLine();
                    if (input.startsWith("/") && !input.startsWith("/salir")) {
                        //System.out.println("enntro1");
                        if (input.indexOf(" ") >= 0) {
                            //System.out.println("enntro2");
                            int inp = input.indexOf(' ');
                            String Receiver = input.substring(1, inp);
                            String message = input.substring(inp, input.length());
                            if (users.containsKey(Receiver)) {
                                if (!name.equals(Receiver)) {
                                    users.get(Receiver).println("MENSAJE (MP-DE-" + name + "): " + message);
                                    users.get(name).println("MENSAJE (MP-PARA-" + Receiver + "): " + message);
                                }

                            }
                        }
                    } else {
                        if (input.toLowerCase().startsWith("/salir")) {
                            return;
                        }
                        for (PrintWriter writer : users.values()) {
                            writer.println("MENSAJE " + name + ": " + input);
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if ((out != null || name != null)&&(!name.equals("null"))) {
                    System.out.println(name + " se va");
                    users.remove(name);
                    for (PrintWriter writer : users.values()) {
                        writer.println("MENSAJE " + name + " se fue");
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
