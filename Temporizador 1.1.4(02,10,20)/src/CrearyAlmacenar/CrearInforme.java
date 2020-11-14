package CrearyAlmacenar;

import IGUs.Marco;
import modulos_generalizar.Generalizar;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Font;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CrearInforme {

    static Document document = new Document();

    //Cambiar referencias
    public static void Crear(boolean infd) throws Exception {//Al estar abierto el documento pdf, se imprime un mensaje 

        final PdfPTable tabla = new PdfPTable(6);
        final Image imagen = Image.getInstance("C:/Users/Pc/Documents/Temporizador/Cibernetica.jpg");
        final Paragraph parrafo = new Paragraph();
        final Paragraph pResumenHrio = new Paragraph();

        try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/BD_horarios", "root", "")) {

            imagen.scaleToFit(200, 300);
            imagen.setAlignment(Chunk.ALIGN_CENTER);

            parrafo.setFont(FontFactory.getFont("Fontana", 15, Font.ITALIC, BaseColor.GRAY));
            parrafo.setAlignment(Paragraph.ALIGN_CENTER);
            parrafo.add("\n\n TABLA DE HORARIOS \n \n \n");

            if (!infd) {//En el caso contrario el objeto se inicializa en CrearInfDiario 

                document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("C://Users//Pc//Desktop//informeHorarios.pdf"));

            }

            tabla.addCell("Facultad u otro");
            tabla.addCell("Hora");
            tabla.addCell("Minuto");
            tabla.addCell("Segundo");
            tabla.addCell("Ti");//("Tiempo de inicio")
            tabla.addCell("Tc");//("Tiempo de conclusion")

            PreparedStatement cTiempos = cn.prepareStatement("SELECT SUM(HORAS),SUM(MINUTOS),SUM(SEGUNDOS) FROM datostiempo WHERE Nombre LIKE ? '%'");

            //****************************************************************
            Generalizar gen = new Generalizar("Alto");

            HashMap<String, Float> map = gen.getDatosGrafico();
            HashSet<String> setG = gen.getAcciones("Accion_general", null);

            Iterator<String> it = setG.iterator();

            while (it.hasNext()) {

                String accionG = it.next();

                pResumenHrio.add(accionG + " = " + map.get(accionG) + " h \n\n");
            }

            //******************************************************************  
            pResumenHrio.setAlignment(Paragraph.ALIGN_LEFT);

            //*****************************************************************
            ResultSet rs;

            rs = cTiempos.executeQuery("SELECT * FROM datostiempo");

            while (rs.next()) {

                tabla.addCell(rs.getString(2));
                tabla.addCell(rs.getString(3));
                tabla.addCell(rs.getString(4));
                tabla.addCell(rs.getString(5));
                tabla.addCell(rs.getString(6));
                tabla.addCell(rs.getString(7));

            }

            //*****************************************************************
            document.open();

            document.add(imagen);
            document.add(pResumenHrio);
            document.add(parrafo);
            document.add(tabla);

            document.close();

            //System.gc();
            Runtime.getRuntime().gc();

        }
    }

    public static void crearInfDiario() throws Exception {

        PreparedStatement cEstablecerListo;
        int numDia;
        document = new Document();
        String nombre_dia = "";

        try {
            FileReader lector = new FileReader(new File("C:/Users/Pc/Documents/Temporizador/dia.txt"));

            while (lector.ready()) {

                nombre_dia += (char) lector.read();
            }

            System.out.println("Nombre del dia: " + nombre_dia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/BD_Horarios", "root", "")) {

            Statement crearTabla;
            Statement llenarTabla;
            PreparedStatement pedirNumDia;//("Pedir numero de dia: el valor autoincrementable")
            PreparedStatement enviarNDInc;//("Enviar el nuevo dato,el numero de dia, incrementado en 1")
            ResultSet rsAutoInc;//("El espacio de memoria en el que se guardará el valor autoincrementable")

            pedirNumDia = cn.prepareStatement("SELECT NUMERO FROM autoincrementod WHERE NOMBRE = ?");
            pedirNumDia.setString(1, nombre_dia);
            rsAutoInc = pedirNumDia.executeQuery();//Se pide el valor autoincrementable
            rsAutoInc.next();//De esta manera se posiciona en el dato
            numDia = rsAutoInc.getInt(1);

            PdfWriter.getInstance(document, new FileOutputStream("C:/Users/Pc/Desktop/Informes Semanales/informeHorarios" + nombre_dia + numDia + ".pdf"));

            crearTabla = cn.createStatement();
            llenarTabla = cn.createStatement();
            crearTabla.executeUpdate("CREATE TABLE informe" + nombre_dia + numDia + " LIKE datostiempo");

            /*com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure 
               generada en el caso de haber distorciones en el tiempo: cambiar la hora o tardar demasiado en 
               ejecutar
             */
 /*Si se realiza esta impresion en este momento, el procedimiento algoritmico de este bloque se ve interrumpido, lo que genera que los datos de autoincrementod
                 no se actualizen y por tanto, que otros hilos de ejecucion puedan ejecutar la eliminacion, dando como resultado dos excepciones:
               
               *NullPointerException: puesto que se verifica mediante el metodo CrearInforme.estaCreado(), algo incorrecto por lo que el condicional if
               no supone una barrera para el flujo del programa
               
               *SQLException: Debido a que se intenta crear nuevamente una tabla que ya existe
               
               en->HiloEliminar->EliminarDatosSi-> en el bookmark del metodo eliminarDatos
               
             */
            //JOptionPane.showMessageDialog(null,"Tabla creada correctamente,Insercción");
            llenarTabla.executeUpdate("INSERT INTO informe" + nombre_dia + numDia + " SELECT * FROM datostiempo");

            enviarNDInc = cn.prepareStatement("UPDATE autoincrementod SET NUMERO = ? WHERE NOMBRE = ?");
            enviarNDInc.setInt(1, numDia + 1);
            enviarNDInc.setString(2, nombre_dia);
            enviarNDInc.executeUpdate();

            cEstablecerListo = cn.prepareStatement("UPDATE autoincrementod SET LISTO = TRUE WHERE NOMBRE = ?");
            cEstablecerListo.setString(1, nombre_dia);
            cEstablecerListo.executeUpdate();

            Crear(true);
        }
    }

    public static String retornaFecha() {

        Date fecha = new Date();
        String Nombre = "";

        if (fecha.toString().contains("Mon")) {
            Nombre = "lunes";
        } else if (fecha.toString().contains("Tue")) {
            Nombre = "martes";
        } else if (fecha.toString().contains("Wed")) {
            Nombre = "miercoles";
        } else if (fecha.toString().contains("Thu")) {
            Nombre = "jueves";
        } else if (fecha.toString().contains("Fri")) {
            Nombre = "viernes";
        } else if (fecha.toString().contains("Sat")) {
            Nombre = "sabado";
        } else if (fecha.toString().contains("Sun")) {
            Nombre = "domingo";
        }

        return Nombre;
    }

    public static boolean estaCreado(String dia) {//La creacion del informe diario y la eliminacion de datos, ya se han hecho 

        try {
            try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_horarios", "root", "")) {

                PreparedStatement cListo = cn.prepareStatement("SELECT LISTO FROM autoincrementod WHERE NOMBRE = ?");

                cListo.setString(1, dia);

                ResultSet rS = cListo.executeQuery();

                rS.next();

                return rS.getBoolean(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static void setHInicio(Marco mrc) {

        mrc.fecha = new Date();
        mrc.hInicio = mrc.fecha.toString().substring(11, 19);

    }

}
