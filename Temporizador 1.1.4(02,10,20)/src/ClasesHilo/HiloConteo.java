package ClasesHilo;


import IGUs.Marco;


public class HiloConteo extends Thread {//Hilo encargado de contar
      
    private Marco mrc;
    
    public HiloConteo(Marco m){
    
       mrc = m;
    }
    
    
     @Override    
     public void run(){
        
       while(!isInterrupted()){/*Funciona, al igual que introducir true porque lo que tiene verdadera
                                   influencia no es el metodo isInterrupted() sino mas bien la palabra reservada 
                                   break en el catch*/
         try{
             Thread.sleep(1000);
             }catch(InterruptedException e){
              // System.out.println("Se interrumpio el hilo de conteo");
                break;
             }      
             
         mrc.segundosCont++;
          
         if(mrc.segundosCont == 60){
          mrc.segundosCont = 0;
          mrc.minutosCont++;
         }
          
         if(mrc.minutosCont == 60){
          mrc.minutosCont = 0;
          mrc.horasCont++;
         }
           
         mrc.jtextResult.setText(mrc.horasCont + " h: " + mrc.minutosCont + " m: " + mrc.segundosCont + " s");
          
       }
           
     }   
     
}
