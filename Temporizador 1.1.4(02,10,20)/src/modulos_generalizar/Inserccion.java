package modulos_generalizar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Inserccion {

    private String nom_tabla;

    public Inserccion(String nivel_g) {

        try {
            Generalizar gen = new Generalizar(nivel_g);
            nom_tabla = gen.getNombreTabla();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertar(String accion_g, String accion_e) {

        try {
            try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_horarios", "root", "")) {

                PreparedStatement c_insertar = cn.prepareStatement("INSERT INTO " + nom_tabla + "(accion_general,accion_especifica) VALUES(?,?)");
                c_insertar.setString(1, accion_g);
                c_insertar.setString(2, accion_e);
                c_insertar.executeUpdate();
            }

        } catch (SQLException ex) {
            System.out.println("Un problema ha ocurrido al establecer conexion o manejar la base de datos");
            ex.printStackTrace();
        }

    }

}
