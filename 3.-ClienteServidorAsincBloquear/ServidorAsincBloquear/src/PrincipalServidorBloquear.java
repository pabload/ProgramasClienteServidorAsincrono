//final
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

    private static Map<String, Cliente> usuarios = new HashMap<>();
    private static Map<String, PrintWriter> sesiones = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            System.out.println(listener.getInetAddress());
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }

        }

    }
    public static class Handler implements Runnable {

        private String Nombre;
        private Socket socket;
        private PrintWriter Escritor;
        private Scanner Entrada;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                Entrada = new Scanner(socket.getInputStream());
                Escritor = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    Escritor.println("NOMBREDEENVIO");
                    Nombre = Entrada.nextLine();
                    if (Nombre.equals("null")) {
                        return;
                    }
                    if (Nombre.equalsIgnoreCase("salir") || Nombre.isEmpty() || sesiones.containsKey(Nombre)) {
                        continue;
                    }
                    synchronized (usuarios) {
                        if (!usuarios.containsKey(Nombre) && !sesiones.containsValue(Escritor)) {
                            Cliente c = new Cliente(Nombre);
                            usuarios.put(Nombre, c);
                            sesiones.put(Nombre, Escritor);
                            Escritor.println("NOMBREACEPTADO " + Nombre);
                            for (PrintWriter pw : sesiones.values()) {
                                pw.println("MENSAJE " + Nombre + " se unio");
                            }
                            break;
                        } else {
                            if (usuarios.containsKey(Nombre)) {
                                sesiones.put(Nombre, Escritor);
                                Escritor.println("NOMBREACEPTADO " + Nombre);
                                for (PrintWriter pw : sesiones.values()) {
                                    pw.println("MENSAJE " + Nombre + " se unio de nuevo");

                                }
                                //System.out.println(usuarios.get(Nombre).bloqueados);
                                break;
                            }
                        }
                    }
                }
                while (true) {
                    String input = Entrada.nextLine();
                    if (input.startsWith("/") && !input.startsWith("/salir") && !input.startsWith("/bloquear") && !input.startsWith("/desbloquear")) {
                        MensajePrivado(input);
                    } else {
                        if (input.toLowerCase().startsWith("/salir")) {
                            return;
                        } else {
                            if (input.toLowerCase().startsWith("/bloquear")) {
                                bloquear(input);
                            } else {
                                if (input.toLowerCase().startsWith("/desbloquear")) {
                                    desbloquear(input);
                                } else {
                                    EnviarMensaje(input);

                                }

                            }
                        }

                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (Escritor != null || Nombre != null) {
                    //System.out.println(Nombre + " se va");
                    
                    for (Map.Entry<String, Cliente> user : usuarios.entrySet()) {
                        String nombre = user.getKey();
                        Cliente obejto = user.getValue();
                        if (!obejto.bloqueados.contains(usuarios.get(Nombre).nombre) && sesiones.containsKey(nombre)) {
                            sesiones.get(nombre).println("MENSAJE " + Nombre + ": " + "se fue");

                        }

                    }
                    sesiones.remove(Nombre);
                    usuarios.remove(Nombre);

                }
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }

        }

        //////Metodos////////////////////////////////////////////////////////////
        public void EnviarMensaje(String input) {
            if (!input.equals("")) {
                for (Map.Entry<String, Cliente> user : usuarios.entrySet()) {
                    String nombre = user.getKey();
                    Cliente obejto = user.getValue();
                    if (!obejto.bloqueados.contains(usuarios.get(Nombre).nombre) && sesiones.containsKey(nombre)) {
                        sesiones.get(nombre).println("MENSAJE " + Nombre + ": " + input);

                    }

                }
            }
        }

        public void MensajePrivado(String input) {
            if (input.indexOf(" ") >= 0) {
                int inp = input.indexOf(' ');
                String destinatario = input.substring(1, inp);
                String mensaje = input.substring(inp, input.length());
                //System.out.println(mensaje);
                if (usuarios.containsKey(destinatario)) {
                    if (!Nombre.equals(destinatario)) {
                        if (!usuarios.get(destinatario).bloqueados.contains(usuarios.get(Nombre).nombre) && sesiones.containsKey(destinatario)) {
                            sesiones.get(usuarios.get(destinatario).nombre).println("MENSAJE (MP-DE-" + Nombre + "): " + mensaje);
                            sesiones.get(usuarios.get(Nombre).nombre).println("MENSAJE (MP-PARA-" + destinatario + "): " + mensaje);
                        }
                    }

                }
            }
        }

        public void bloquear(String input) {
            if (input.indexOf(" ") >= 0) {
                int inp = input.indexOf(' ') + 1;
                String bloqueado = input.substring(inp);
                //System.out.println(usuarios.containsKey(bloqueado));
                if (usuarios.containsKey(bloqueado)) {
                    if (!Nombre.equals(bloqueado)) {
                        if (!usuarios.get(Nombre).bloqueados.contains(bloqueado)) {
                            usuarios.get(Nombre).bloqueados.add(bloqueado);
                            sesiones.get(usuarios.get(Nombre).nombre).println("MENSAJE bloqueaste a: " + bloqueado);
                        }

                    }

                }
            }
        }

        public void desbloquear(String input) {
            if (input.indexOf(" ") >= 0) {
                int inp = input.indexOf(' ') + 1;
                String desbloqueado = input.substring(inp);
                //System.out.println(usuarios.containsKey(desbloqueado));
                if (usuarios.containsKey(desbloqueado)) {
                    if (!Nombre.equals(desbloqueado)) {
                        if (usuarios.get(Nombre).bloqueados.contains(desbloqueado)) {
                            usuarios.get(Nombre).bloqueados.remove(desbloqueado);
                            sesiones.get(usuarios.get(Nombre).nombre).println("MENSAJE desbloqueaste a: " + desbloqueado);
                        }
                    }

                }
            }
        }

    }
}
