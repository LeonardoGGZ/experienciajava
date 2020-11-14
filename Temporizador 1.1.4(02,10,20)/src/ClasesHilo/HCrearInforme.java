package ClasesHilo;

import IGUs.Marco;
import CrearyAlmacenar.CrearInforme;
import modulos_estaticos.MetodosComprobar;


public class HCrearInforme extends Thread {
  
   private Marco mrc;
    
    public HCrearInforme(Marco mrc){
        this.mrc = mrc;
    }
    
    
   @Override
   public void run(){
   
     mrc.crearInf = new CrearInforme(); 
         
           
     if(MetodosComprobar.EstaActivo("NitroPDF.exe"))
        MetodosComprobar.esperaPDF();
        
     try{
         CrearInforme.Crear(false);
         }catch(Exception ex){
           System.out.println("Un problema ha ocurrido al crear el PDF");
           ex.printStackTrace();
         }
   }
   
}
