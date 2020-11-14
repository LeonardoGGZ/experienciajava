package CrearyAlmacenar;

import java.io.File;

import javax.swing.JButton;

import modulos_estaticos.MetodosRestaurar;

public class RestaurarDatos {

    private JButton reset;

    public RestaurarDatos(JButton reset) {
        this.reset = reset;
    }

    public void restaurar() {

        MetodosRestaurar.reestDatos();
        File f = new File("C:/Users/pc/Documents/Temporizador/dia.txt");

        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            System.out.println("Excepcion en el hilo " + Thread.currentThread());
            ex.printStackTrace();
        }

        f.delete();

        reset.setEnabled(false);
    }
}
