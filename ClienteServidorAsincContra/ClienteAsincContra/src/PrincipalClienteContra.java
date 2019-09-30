import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class PrincipalClienteContra {

    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new  JTextArea(16,50);
    
    public PrincipalClienteContra(String serverAddress){
        this.serverAddress=serverAddress;
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField,BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea));
        frame.pack();
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              out.println(textField.getText());
              textField.setText("");
            }
        });
    }
    private  String getNombre(){
        return JOptionPane.showInputDialog(
        frame,
        "Nombre",
        "ingresa nombre",
        JOptionPane.PLAIN_MESSAGE
        );
 
    }
     private  String getContra(){
        return JOptionPane.showInputDialog(
        frame,
        "Contraseña:",
        "ingresa contraseña",
        JOptionPane.PLAIN_MESSAGE
        );
 
    }
    
    
    private void run() throws IOException{
        try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("NOMBREDEENVIO")) {
                    out.println(getNombre());
                } else if (line.startsWith("CONTRA")) {
                    out.println(getContra());
                } else if (line.startsWith("NOMBREACEPTADO")) {
                    //System.out.println(line);
                    this.frame.setTitle("Chatter -" + line.substring(15));
                    textField.setEditable(true);
                } else if (line.startsWith("MENSAJE")) {
                    //System.out.println(line);
                    messageArea.append(line.substring(8) + "\n");
                }
            }
                    
                    
        }finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("es necesario poner la ip");
            return;
        }
        PrincipalClienteContra cliente = new PrincipalClienteContra(args[0]);
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setVisible(true);
        cliente.run();
    }
    
}
