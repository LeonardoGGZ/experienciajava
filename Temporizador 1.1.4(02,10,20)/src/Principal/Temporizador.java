package Principal;

import IGUs.Marco;

import javax.swing.JFrame;

public class Temporizador {

    public static void main(String Args[]) {

        //System.out.println(System.getenv("windir"));
        Marco m = new Marco();
        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.setResizable(false);
        m.setSize(350, 210);
        m.setLocationRelativeTo(null);
        m.setVisible(true);

    }
}
