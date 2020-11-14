package ClasesHilo;


import IGUs.Marco;

import java.util.concurrent.Callable;

import javax.swing.JFrame;


public class CrearMarco implements Callable<String> {
   
 
    @Override
    public String call(){
       
      Marco m = new Marco();
      m.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      m.setLocationRelativeTo(null);
      m.setResizable(false);
      m.setSize(300,210);
      m.setVisible(true);

      return "Satisfactorio";
    }
      
}