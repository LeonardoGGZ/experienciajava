package IGUs;

import CrearyAlmacenar.RestaurarDatos;
import ClasesHilo.*;
import CrearyAlmacenar.CrearInforme;
import oyentes.*;
import modulos_estaticos.MetodosRestaurar;

import java.util.Date;

import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Marco extends JFrame {

    public JButton continuar;
    public JButton detener;
    public JButton limpYalm;
    public JButton cInf;
    public JButton notificar;
    public JButton nuevo;
    public JButton reset;

    public JTextField jtextResult;
    public JTextField jtextFnomb;//Los nombres de eventos

    public byte segundosCont;
    public byte minutosCont;//Utilizados en el conteo
    public byte horasCont;

    public Thread thConteo;
    public Thread hS;
    private static Thread hel;//hilo eliminar 

    //Las variables final deben ir en mayuscula, pero las de tipo static, no necesariamente
    public String hInicio, hFinal;

    public static String NombreFuO;//Nombre facultad u otro

    private ActionListener listener;
    private OyenteVentana oV;
    private OyenteFoco oF;
    private OyenteTeclado oT;

    public CrearInforme crearInf;

    public Date fecha;

    public Marco() {

        frameInit();

        setLayout(new BorderLayout());

        setTitle("Temporizador");

        MetodosRestaurar.marcos.add(this);

        Lamina lamina = new Lamina(this);
        add(lamina, BorderLayout.CENTER);

    }

    /*
    Mayor modularizacion del codigo debido a: 
     
    *La utilizacion comun de los campos de la clase Marco, por parte de los oyentes y del hiloConteo
    *La gran cantidad de codigo e implementaciones en el Frame del programa 
    *La referencia continua a un objeto aunque puede no ser del todo correcto
    
     */
    //class HiloConteo
    //class OyenteEventos implements ActionListener{
    //class OyenteTeclado extends KeyAdapter
    //class OyenteVentana 
    class Lamina extends JPanel {

        public Lamina(Marco marco) {

            setLayout(null);

            thConteo = new HiloConteo(marco);//El hilo contador

            fecha = new Date();

            listener = new OyenteAccion(marco, thConteo);

            oF = new OyenteFoco();

            oT = new OyenteTeclado(marco);

            boolean es_main;

            if (Thread.currentThread().getName().equals("main")) {

                hel = new Thread(new HiloEliminar(marco));
                hel.setPriority(Thread.MAX_PRIORITY);
                hel.start();

                es_main = true;

            } else {
                es_main = false;
            }

            oV = new OyenteVentana(Thread.currentThread().getName());
            addWindowListener(oV);

            segundosCont = 0;
            minutosCont = 0;
            horasCont = 0;

            hInicio = "";
            hFinal = "";

            continuar = new JButton("Continuar");
            continuar.setBounds(5, 35, 90, 100);
            continuar.addActionListener(listener);
            continuar.addFocusListener(oF);
            continuar.addKeyListener(oT);
            add(continuar);

            detener = new JButton("Detener");
            detener.setBounds(198, 35, 90, 100);
            detener.addActionListener(listener);
            detener.addFocusListener(oF);
            detener.addKeyListener(oT);
            add(detener);

            limpYalm = new JButton("Restaurar");
            limpYalm.setBounds(96, 140, 100, 30);
            limpYalm.addActionListener(listener);
            limpYalm.addFocusListener(oF);
            limpYalm.addKeyListener(oT);
            //limpYalm.setForeground(Color.WHITE);
            limpYalm.setBackground(Color.GREEN);
            add(limpYalm);

            notificar = new JButton("Notificar");
            notificar.addActionListener(listener);
            notificar.addFocusListener(oF);
            notificar.addKeyListener(oT);
            notificar.setBounds(3, 140, 90, 30);
            add(notificar);

            cInf = new JButton("Informe");
            cInf.setBounds(96, 40, 100, 80);
            cInf.addActionListener(listener);
            cInf.addFocusListener(oF);
            cInf.addKeyListener(oT);
            add(cInf);

            nuevo = new JButton("Nuevo");
            nuevo.setBounds(202, 140, 90, 30);
            nuevo.addActionListener(listener);
            nuevo.addFocusListener(oF);
            nuevo.addKeyListener(oT);
            add(nuevo);

            jtextResult = new JTextField();
            jtextResult.setEditable(false);
            jtextResult.setBounds(6, 0, 182, 25);
            add(jtextResult);

            jtextFnomb = new JTextField();
            jtextFnomb.setEditable(true);
            jtextFnomb.setBounds(190, 0, 100, 25);
            jtextFnomb.addKeyListener(oT);
            add(jtextFnomb);

            if (es_main) {
                reset = new JButton("R");
                reset.setBounds(290, 50, 45, 45);
                reset.setToolTipText("Presionar solo en el caso de no usar mas durante el dia de hoy");
                reset.addActionListener((e) -> {

                    int decision = JOptionPane.showConfirmDialog(null, "¿Seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);

                    if (decision == 0) {
                        RestaurarDatos rd = new RestaurarDatos(reset);
                        rd.restaurar();
                    }

                });
                add(reset);
            }
        }
    }

}
