package modulos_estaticos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public abstract class MetodosComprobar {

    //Para generar diversidad de tipos de metodos, se establece opcionalmente al metodo EstaActivo, como estatico 
    public static boolean EstaActivo(String programa) {//Principalmente usado para comprobar la actividad de archivos PDF

        String Nprograma = programa;

        try {

            String str_proceso;
            String admin = System.getenv("windir") + "\\system32\\" + "tasklist.exe";

            Process proceso = Runtime.getRuntime().exec(admin);

            BufferedReader input = new BufferedReader(new InputStreamReader(proceso.getInputStream()));

            while ((str_proceso = input.readLine()) != null) {

                if (str_proceso.contains(Nprograma)) {
                    return true;
                }

            }

            proceso.destroy();
            input.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static boolean estaAbierta(String nomBD) {

        try {

            DriverManager.getConnection("jdbc:mysql://localhost/" + nomBD, "root", "").close();
            return true;

        } catch (SQLException e) {
            System.out.println("Conexion no establecida");
            return false;
        }
    }

    public static void avisarBDInactiva() {

        try {
            JOptionPane.showMessageDialog(null, "Debes abrir la base de datos");
            Thread.sleep(10000);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void esperaPDF() {

        while (MetodosComprobar.EstaActivo("NitroPDF.exe")) {
            MetodosRestaurar.esperar(0, 0, 10);
            JOptionPane.showMessageDialog(null, "Debes cerrar el documento PDF");
        }

    }

}
