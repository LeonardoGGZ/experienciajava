package CrearyAlmacenar;

import modulos_estaticos.MetodosComprobar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlmacenarEnBD extends MetodosComprobar {

    public void AlmacenarDatos(String nombre, byte horas, byte minutos, byte segundos, String Inicio, String Final) throws SQLException {

        try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/bd_horarios", "root", "")) {
            //cn.setAutoCommit(false);

            PreparedStatement consulta = cn.prepareStatement("INSERT INTO datostiempo(NOMBRE,HORAS,MINUTOS,SEGUNDOS,Inicio,Final) VALUES(?,?,?,?,?,?)");
            consulta.setString(1, nombre);
            consulta.setByte(2, horas);
            consulta.setByte(3, minutos);
            consulta.setByte(4, segundos);
            consulta.setString(5, Inicio);
            consulta.setString(6, Final);

            consulta.executeUpdate();

        }
    }
}
