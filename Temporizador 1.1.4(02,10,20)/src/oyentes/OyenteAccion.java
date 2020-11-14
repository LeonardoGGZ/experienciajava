package oyentes;

import ClasesHilo.CrearMarco;
import IGUs.Marco;
import ClasesHilo.EjecutarSonido;
import ClasesHilo.HiloConteo;
import CrearyAlmacenar.*;
import modulos_estaticos.MetodosComprobar;
import modulos_estaticos.MetodosRestaurar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OyenteAccion extends MetodosComprobar implements ActionListener {

    Runtime runtime = Runtime.getRuntime();

    ExecutorService executor = Executors.newFixedThreadPool(runtime.availableProcessors());

    private final Marco mrc;//Esto es necesario, debido al uso de las mismas variables en otros oyentes(es una clase que comparten en comun)

    public OyenteAccion(Marco m, Thread th) {

        this.mrc = m;

        mrc.hS = new Thread(new EjecutarSonido(mrc));

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object origen = e.getSource();

        if (origen.equals(mrc.limpYalm)) {/*El horario de fin de una accion puede no coincidir con el dato horario(h,m,s) pues se puede presionar en un tiempo muy o poco cercano
                                       generando haci, una diferencia*/

            MetodosRestaurar.resetDisplay(mrc, true);

        } else if (origen.equals(mrc.continuar)) {

            if (mrc.hS.isAlive()) {
                mrc.hS.interrupt();
            }

            //El boton detener previamente fue presionado    
            if (mrc.thConteo.getState().toString().equals("TERMINATED")) {//Tiene como fin evitar la creacion de multiples hilos

                /* System.out.println("Detener fue presionado antes, actual: continuar, estado : " 
        + mrc.thConteo.getState());
                 */
                mrc.thConteo = new HiloConteo(mrc);
                mrc.thConteo.start();

            } else {
                /* Se cumple cada vez que se presiona el boton continuar luego de este ser presionado previamente y en el caso de no haberse presionado
                  el boton detener */

                ///Se imprime como estado, TIMED_WAITING puesto que el hilo esta en uso y no puede ser utilizado hasta que deje de cumplir un proceso
                //System.out.println("El hilo esta activo(no esta en el estado TERMINATED),TIMED_WAITING: " + mrc.thConteo.getState());

                /*Solamente al estar en el estado NEW, es decir, que es la primera 
            vez que se presiona Continuar(podria quedar inutilizable al iniciar el hilo por tipeo)*/
                if (!mrc.thConteo.isAlive()) {
                    mrc.thConteo.start();
                }

            }

            if (mrc.horasCont == 0 && mrc.minutosCont == 0 && mrc.segundosCont == 0) {
                CrearInforme.setHInicio(mrc);
            }

        } else if (origen.equals(mrc.cInf)) {

            try {

                mrc.crearInf = new CrearInforme();

                if (MetodosComprobar.EstaActivo("NitroPDF.exe")) {
                    MetodosComprobar.esperaPDF();
                }

                System.out.println("Creacion del pdf");
                CrearInforme.Crear(false);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(null, "Problema al crear el informe");

                ex.printStackTrace();

                if (ex.getCause().toString().contains("ConnectException")) {

                    System.out.println("La clase SQLException: " + ex.getClass());
                    JOptionPane.showMessageDialog(null, "Servicios no activados", "Base de datos inactiva", JOptionPane.ERROR_MESSAGE);

                }
            }

        } else if (origen.equals(mrc.nuevo)) {

            runtime = Runtime.getRuntime();

            Future<String> tarea = executor.submit(new CrearMarco());  //Tambien puede no manejarse como un hilo, y unicamente crear un objeto marco

            while (!tarea.isDone()) {
            }

        } else {

            if (!mrc.hS.isAlive()) {

                mrc.hS = new Thread(new EjecutarSonido(mrc));
                mrc.hS.start();

            }

            mrc.thConteo.interrupt();//No tiene efecto en el caso de no estar ejecutando un proceso(estado NEW) 

        }
    }

}
