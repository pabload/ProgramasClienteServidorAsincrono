import java.io.Serializable;
import java.util.ArrayList;

public class Cliente implements Serializable{

    String nombre;
    String contra;
    ArrayList<String> bloqueados = new ArrayList<>();

    public Cliente(String nombre, String contra) {
        this.nombre = nombre;
        this.contra = contra;
    }

    
}
