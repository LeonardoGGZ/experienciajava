package ClasesHilo;

import IGUs.Grafico;
import modulos_generalizar.Generalizar;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

public class CrearGrafico implements Callable<String> {

    @Override
    public String call() {

        try {
            Generalizar gen = new Generalizar("Alto"/*,Ntabla*/);

            HashMap<String, Float> mapa = gen.getDatosGrafico();
            
            Grafico grafico = new Grafico(mapa);
            grafico.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            grafico.setSize(300, 250);
            grafico.setLocationRelativeTo(null);
            grafico.setResizable(false);
            grafico.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "Satisfactorio";
    }

}
