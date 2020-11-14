package ClasesHilo;

import IGUs.Marco;
import calculart.CalcularT;
import modulos_estaticos.MetodosRestaurar;

import java.io.File;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HiloEliminar extends MetodosRestaurar implements Runnable {

    private final Marco mrc;
    private MetodosRestaurar m;//Averiguar por las instanciaciones

    public HiloEliminar(Marco m) {
        mrc = m;
    }

    @Override
    public void run() {

        CalcularT cT = new CalcularT();

        cT.Calcular(mrc.fecha.toString().substring(11, 22), "23:48");

        esperar(cT.getHoras(), cT.getMinutos(), cT.getSegundos());
        avisarR();

        Runtime runtime = Runtime.getRuntime();
        //System.out.println("Procesadores disponibles: " + runtime.availableProcessors());

        ExecutorService executor = Executors.newFixedThreadPool(runtime.availableProcessors());

        CrearGrafico hiloG = new CrearGrafico();

        Future<String> future = executor.submit(hiloG);

        long tiempo1 = System.currentTimeMillis();

        while (!future.isDone()){
        
        }

        long tiempo2 = System.currentTimeMillis();
        long tiempoT = tiempo2 - tiempo1;
        System.out.println("Tiempo de procesamiento del hilo: " + tiempoT);

        esperar(0, 10, 0);
        
        //Resetea los displays de todos los marcos y guarda los datos del dia
        if (reestDatos()) { //Elimina los datos de la tabla datostiempo
            File file_txt = new File("C:/Users/pc/Documents/Temporizador/dia.txt");
            if (file_txt.exists()) {
                file_txt.delete();
            }
        }
    }

}
