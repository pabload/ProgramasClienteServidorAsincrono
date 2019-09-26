
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class Cliente implements Serializable{

    String nombre;
    ArrayList<String> bloqueados = new ArrayList<>();

    
    public Cliente(String nombre) {
        this.nombre = nombre;
        
    }

}
