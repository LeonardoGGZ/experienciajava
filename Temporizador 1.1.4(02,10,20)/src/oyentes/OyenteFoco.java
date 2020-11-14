package oyentes;

import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;


public class OyenteFoco extends FocusAdapter {
    

     @Override
     public void focusGained(FocusEvent e){
         
      OyenteTeclado.cSeleccionado = e.getSource();
     }

     
     @Override
     public void focusLost(FocusEvent e){
         
      OyenteTeclado.cSeleccionado = null;
     }

}
