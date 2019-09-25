
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class Cliente {

    String nombre;
    PrintWriter out;
    ArrayList<String> bloqueados = new ArrayList<>();

    
    public Cliente(String nombre, PrintWriter out) {
        this.nombre = nombre;
        this.out = out;
        
    }

}
