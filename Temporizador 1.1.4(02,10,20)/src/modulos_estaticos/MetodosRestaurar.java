package modulos_estaticos;

import CrearyAlmacenar.AlmacenarEnBD;
import CrearyAlmacenar.CrearInforme;
import static IGUs.Marco.NombreFuO;
import IGUs.Marco;
import static modulos_estaticos.MetodosComprobar.estaAbierta;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class MetodosRestaurar {

    //  *"No se puede acceder a un metodo no estatico en un contexto estatico"
    /*Para determinados metodos podria ser estrictamente necesario acceder a un dato recibido por 
    parametros en el constructor, es por eso que es conveniente no hacerlos estaticos  
     */
    //Buscar una posible mejor solucion
    public static List<Marco> marcos = new ArrayList<Marco>(); //Se añaden objetos en el Hilo CrearMarco para posteriormente utilizar en Marco

    public static void avisarR() {//avisar sobre la restauracion posterior de los datos y sobre la creacion del informe diario

        Toolkit.getDefaultToolkit().beep();

        esperar(0, 0, 3);

        Toolkit.getDefaultToolkit().beep();

        esperar(0, 0, 1);

        Toolkit.getDefaultToolkit().beep();

        JOptionPane.showMessageDialog(null, "QUEDAN 10 MINUTOS PARA LA RESTAURACION DE DATOS");

    }

    public static boolean reestDatos() {

        Iterator<Marco> it = MetodosRestaurar.marcos.iterator();

        while (it.hasNext()) {
            Marco n_marco = it.next();
            if (n_marco.isVisible()) {
                resetDisplay(n_marco, false);
            }
        }

        String nombre_dia = "";

        //Reducir instrucciones repetitivas --> Llamada en multiples metodos
        //////////////////////
        while (!MetodosComprobar.estaAbierta("bd_horarios")) {

            MetodosComprobar.avisarBDInactiva();
        }

        if (MetodosComprobar.EstaActivo("NitroPDF.exe")) {

            MetodosComprobar.esperaPDF();
        }
        ///////////////////////

        //! Crear unicamente el objeto de tipo file en OyenteVentana(si es necesario)
        try (FileReader lectorD = new FileReader(new File("C:/Users/Pc/Documents/Temporizador/dia.txt"))) {

            while (lectorD.ready()) {

                nombre_dia += (char) lectorD.read();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo txt. Datos no eliminados, informe no creado");
            ex.printStackTrace();
            return false;
        }

        if (CrearInforme.estaCreado(nombre_dia))//Condicion que genera que no se ejecuten consultas de eliminacion y de creacion, al no haber datos   
        {
            return false;
        }

        try (Connection miCn = DriverManager.getConnection("jdbc:mysql://localhost/bd_horarios", "root", "")) {

            esperar(0, 0, 10);

            CrearInforme.crearInfDiario();

            System.out.println("InformeDiario: creado");

            Statement cEliminar = miCn.createStatement();

            cEliminar.executeUpdate("TRUNCATE datostiempo;");

            cEliminar.close();

            JOptionPane.showMessageDialog(null, "¡Todo listo! restauracion satisfactoria");
            System.out.println("Eliminacion completada");

        } catch (Exception ex) {
            System.out.println("Excepcion al momento de restaurar datos --> Datos no eliminados");
            ex.printStackTrace();
        }

        return true;
    }

    public static void esperar(int h, int m, int s) {

        int segundos = s + h * 3600 + m * 60;

        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException ex) {
            System.out.println("Excepcion debido a la interrupcion de un hilo");
            ex.printStackTrace();
        }
    }

    public static void resetDisplay(Marco mrc, boolean confirmDiag) {

        mrc.thConteo.interrupt();

        if (mrc.hS.isAlive()) {
            mrc.hS.interrupt();
        }

        NombreFuO = mrc.jtextFnomb.getText();
        mrc.fecha = new Date();
        mrc.hFinal = mrc.fecha.toString().substring(11, 19);

        boolean resetAbs = false;

        if (confirmDiag) {

            byte alm = (byte) (JOptionPane.showConfirmDialog(null, "Almacenar en BBDD: ",
                    "Guardar datos", JOptionPane.YES_NO_CANCEL_OPTION));

            if (alm == -1 || alm == 2)//En el caso de salir del cuadro de dialogo o cancelar
            {
                return;
            }

            if (alm == 1) {

                mrc.horasCont = 0;
                mrc.minutosCont = 0;
                mrc.segundosCont = 0;

                mrc.jtextResult.setText(0 + " h: " + 0 + " m: " + 0 + " s");

            } else {//alm == 0 == "si"

                resetAbs = true;
            }

        }

        if (!confirmDiag || resetAbs) {

            if (mrc.horasCont == 0 && mrc.minutosCont == 0 && mrc.segundosCont == 0) {
                return;
            }

            AlmacenarEnBD almDatos = new AlmacenarEnBD();

            if (estaAbierta("bd_horarios")) {

                try {
                    almDatos.AlmacenarDatos(NombreFuO, mrc.horasCont, mrc.minutosCont, mrc.segundosCont, mrc.hInicio, mrc.hFinal);
                } catch (SQLException ex) {
                    System.out.println("Excepcion al almacenar datos:");
                    ex.printStackTrace();
                }

                if (confirmDiag) {
                    JOptionPane.showMessageDialog(null, "Datos guardados");
                }

                mrc.horasCont = 0;
                mrc.minutosCont = 0;
                mrc.segundosCont = 0;

                /*No es necesario dar nuevamente un valor nulo a las variables hInicio y hFinal, por la creacion de nuevas instancias
              al momento de reutilizarlas*/
                mrc.jtextResult.setText(0 + " h: " + 0 + " m: " + 0 + " s");

            } else {
                JOptionPane.showMessageDialog(null, "Recursos inactivos", "Base de datos sin abrir", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

}
