package ClasesHilo;

        
import IGUs.Marco;

import java.awt.Toolkit;


public class EjecutarSonido implements Runnable {
    
    private Marco mrc;
    
    public EjecutarSonido(Marco m){
        this.mrc = m;
    }
    
    
    @Override
     public void run(){
                  
         /*
         La comprobacion interrupted() o isInterrupted() no ayuda en nada puesto que al entrar al no
         ser llamado el metodo interrupt() desde este metodo, el registro "se limpia" generando un
         retorno falso. La unica forma de aprovecharlo, en este caso, es utilizar la palabra reservada break
         */        

       while(mrc.isActive() && !Thread.currentThread().interrupted()){
          
         try{
              
             Thread.sleep(10000);
             Toolkit.getDefaultToolkit().beep();

             }catch(InterruptedException e){//Averiguar por Logger
               //System.out.println("Excepcion al dormir el hilo: sonido");
                break;
             }
        }
     }
}
