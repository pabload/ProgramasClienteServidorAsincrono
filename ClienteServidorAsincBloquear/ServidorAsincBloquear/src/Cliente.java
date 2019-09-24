
import java.io.PrintWriter;
import java.util.ArrayList;

public class Cliente {

    String nombre;
    PrintWriter out;
    ArrayList<String> bloqueados;

    
    public Cliente(String nombre, PrintWriter out) {
        this.nombre = nombre;
        this.out = out;
    }

}
