package oyentes;

import calculart.CalcularT;
import ClasesHilo.HiloConteo;
import CrearyAlmacenar.CrearInforme;
import IGUs.Marco;
import modulos_estaticos.MetodosComprobar;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class OyenteTeclado implements KeyListener {

    private Marco mrc;
    private Set<Integer> pressed = new HashSet<Integer>();//Pagina StackOverFlow
    private boolean iniciarC = true;
    public static Object cSeleccionado;

    public OyenteTeclado(Marco mrc) {

        this.mrc = mrc;

    }

    @Override
    public void keyReleased(KeyEvent e) {

        pressed.remove(e.getKeyCode());

    }

    @Override
    public void keyPressed(KeyEvent e) {

        pressed.add(e.getKeyCode());

        if (pressed.size() > 1) {

            if (e.isControlDown()/* -> || pressed.contains(17)*/) {

                if (pressed.contains(67) || pressed.contains(65)) {
                    iniciarC = false;
                }

            }

        } else {

            if (pressed.contains(8)) {
                iniciarC = false;
            }

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

        //En el caso de presionarse cualquier tecla excepto el enter  
        if (e.getKeyChar() != KeyEvent.VK_ENTER) {//Un char es un entero

            //Bloque encargado de la interrupcion del hilo EjecutarSonido
            if (mrc.hS.isAlive()) {
                mrc.hS.interrupt();
            }

            if (mrc.thConteo.isAlive()) {//El hilo se encuentra en el estado Runnable

            } else if (!iniciarC) {

                //Para evitar iniciar el conteo innecesariamente
            } else if (mrc.thConteo.getState().toString().equals("NEW")) {// = !mrc.thConteo.getState().toString().equals("TERMINATED")

                /* Se cumple solo una vez: cuando el evento se produce por primera vez, para que de esta forma se use la instancia asignada en el constructor
            de la clase Marco */
                mrc.thConteo.start();

            } else {//Detener fue presionado en un momento previo 

                mrc.thConteo = new HiloConteo(mrc);


                /*A tener en cuenta: En el caso de utilizarse un espacio de memoria propio para almacenar el hilo pasado por parametro, 
             al momento de crear una nueva instancia tambien se genera un objeto diferente y por tanto tambien, la perdida del control sobre thConteo
             desde el OyenteAccion*/
                mrc.thConteo.start();

            }

            if (iniciarC) {

                if (mrc.horasCont == 0 && mrc.minutosCont == 0 && mrc.segundosCont == 0) {
                    CrearInforme.setHInicio(mrc);
                }

            } else {

                //Se restablece el valor de iniciarC
                iniciarC = true;
            }

        } else {

            //El valor de esta variable cambia en OyenteFoco
            if (cSeleccionado != null) {

                JButton bSelect = (JButton) cSeleccionado;
                bSelect.doClick();

            } else {

                pedirDatos();
            }
        }

    }

    public void pedirDatos() {

        CalcularT cTiempo;

        try {

            Connection cn;
            PreparedStatement enviaC;
            String nombre;

            while (!MetodosComprobar.estaAbierta("bd_horarios")) {
                MetodosComprobar.avisarBDInactiva();
            }

            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_horarios", "root", "");

            enviaC = null;

            do {

                nombre = JOptionPane.showInputDialog(null, "Introduce el nombre: ");

                if (nombre == null) {
                    break;//Se realiza para evitar una excepcion al momento de comprobar una igualdad nula en el while
                }
            } while (nombre.equals(""));

            if (nombre != null) {

                String horasOmin;

                do {

                    horasOmin = JOptionPane.showInputDialog(null, "¿Que datos deseas introducir?");

                    if (horasOmin == null) {
                        break;
                    }

                } while (horasOmin.equals(""));

                if (horasOmin != null) {

                    String cadenaR;
                    String inicio = "";
                    String conclusion = "";
                    byte horas = 0;
                    byte minutos = 0;

                    if (horasOmin.equalsIgnoreCase("Todos")) {

                        do {

                            cadenaR = JOptionPane.showInputDialog(null, "Introduce el horario de inicio");

                            if (cadenaR == null) {
                                break;
                            }

                        } while (cadenaR.equals(""));

                        if (cadenaR != null) {

                            inicio = cadenaR;

                            do {

                                cadenaR = JOptionPane.showInputDialog(null, "Introduce el horario de conclusion");

                                if (cadenaR == null) {
                                    break;
                                }

                            } while (cadenaR.equals(""));

                            if (cadenaR != null) {

                                conclusion = cadenaR;

                                cTiempo = new CalcularT();
                                cTiempo.Calcular(inicio, conclusion);
                                horas = cTiempo.getHoras();
                                minutos = cTiempo.getMinutos();

                            }

                        }

                    } else if (horasOmin.equalsIgnoreCase("Horas") && horasOmin.equalsIgnoreCase("Minutos")) {//En el caso de ser hOm nulo, lanza un NullPointerException

                        do {

                            cadenaR = JOptionPane.showInputDialog(null, "Introduce las horas: ");

                            if (cadenaR == null) {
                                break;
                            }

                        } while (cadenaR.equals(""));

                        if (cadenaR != null) {

                            horas = (byte) Integer.parseInt(cadenaR);

                            do {

                                cadenaR = JOptionPane.showInputDialog(null, "Introduce los minutos: ");

                                if (cadenaR == null) {
                                    break;
                                }

                            } while (cadenaR.equals(""));

                            if (cadenaR != null) {
                                minutos = (byte) Integer.parseInt(cadenaR);
                            }

                        }

                    } else if (horasOmin.equalsIgnoreCase("Minutos")) {

                        do {

                            cadenaR = JOptionPane.showInputDialog(null, "Introduce los minutos: ");

                            if (cadenaR == null) {
                                break;
                            }

                        } while (cadenaR.equals(""));

                        if (cadenaR != null) {
                            minutos = (byte) Integer.parseInt(cadenaR);
                        }

                    } else if (horasOmin.equalsIgnoreCase("Horas")) {

                        do {

                            cadenaR = JOptionPane.showInputDialog(null, "Introduce las horas: ");

                            if (cadenaR == null) {
                                break;
                            }

                        } while (cadenaR.equals(""));

                        if (cadenaR != null) {
                            horas = (byte) Integer.parseInt(cadenaR);
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Opcion no existente", "ADVERTENCIA", JOptionPane.ERROR_MESSAGE);
                    }

                    if (!inicio.equals("") && !conclusion.equals("")) {

                        enviaC = cn.prepareStatement("INSERT INTO datostiempo(NOMBRE,HORAS,MINUTOS,SEGUNDOS,INICIO,FINAL) VALUES (?,?,?,0,?,?)");
                        enviaC.setString(1, nombre);
                        enviaC.setByte(2, horas);
                        enviaC.setByte(3, minutos);
                        enviaC.setString(4, inicio);
                        enviaC.setString(5, conclusion);

                    } else if (horas != 0 || minutos != 0) {

                        enviaC = cn.prepareStatement("INSERT INTO datostiempo(NOMBRE,HORAS,MINUTOS,SEGUNDOS) VALUES(?,?,?,0)");
                        enviaC.setString(1, nombre);
                        enviaC.setByte(2, horas);
                        enviaC.setByte(3, minutos);

                    }

                    if (horas != 0 || minutos != 0) {
                        enviaC.executeUpdate();
                    }

                }

            }

        } catch (HeadlessException | NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Sevicios no activados", "La base de datos esta inactiva", JOptionPane.ERROR_MESSAGE);
        }
    }

}
