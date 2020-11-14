package oyentes;

import CrearyAlmacenar.CrearInforme;
import modulos_estaticos.MetodosComprobar;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

public class OyenteVentana extends WindowAdapter {

    private String nomHilo;
    private final Runtime r;
    private Process p;
    private ResultSet rS;
    private PreparedStatement cSelect;
    private Statement cReset;
    public static File dia;

    public OyenteVentana(String nHilo) {

        nomHilo = nHilo;

        r = Runtime.getRuntime();

    }

    @Override
    public void windowOpened(WindowEvent e) {

        try {

            if (nomHilo.equals("main")) {

                p = r.exec("C:\\xampp\\xampp-control.exe");

                File doc_txt = new File("C:/Users/pc/Documents/Temporizador/dia.txt");
                dia = doc_txt;

                if (!doc_txt.exists()) {

                    FileWriter escritor = new FileWriter(doc_txt.getAbsolutePath());

                    String nom_dia = CrearInforme.retornaFecha();

                    for (int i = 0; i < nom_dia.length(); i++) {
                        escritor.write(nom_dia.charAt(i));
                    }

                    escritor.close();
                }

            }

            String nom_BD = "bd_horarios";

            while (!MetodosComprobar.estaAbierta(nom_BD)) {
                MetodosComprobar.avisarBDInactiva();
            }

            try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/" + nom_BD, "root", "")) {

                cSelect = cn.prepareStatement("SELECT LISTO FROM autoincrementod WHERE NOMBRE = ?");
                cSelect.setString(1, "domingo");
                rS = cSelect.executeQuery();
                rS.next();

                if (rS.getBoolean(1) == true) {

                    cReset = cn.createStatement();
                    cReset.executeUpdate("UPDATE autoincrementod SET LISTO = FALSE");

                    System.out.println("Datos restaurados, todos false");

                }
            }

        } catch (IOException | SQLException ex) {
            System.out.println("Excepcion en OyenteVentana->establecer listo = false");
            ex.printStackTrace();
        }

    }

    @Override
    public void windowClosing(WindowEvent e) {

        if (nomHilo.equals("main")) {
            p.destroy();
        }

    }

}
