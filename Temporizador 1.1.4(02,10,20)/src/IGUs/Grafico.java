package IGUs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Grafico extends JFrame {

    private JMenuBar barra_opt;
    private JMenu dispos;
    private JMenuItem porcent, portiempo;
    //private String noms_accion[];
    private boolean esPorcentaje;

    public Grafico(HashMap<String, Float> mapData) {

        frameInit();

        setLayout(new BorderLayout());

        esPorcentaje = false;

        Lamina L_info = new Lamina(mapData);
        add(L_info, BorderLayout.CENTER);

        barra_opt = new JMenuBar();

        dispos = new JMenu("Visualizar");

        ActionListener Oyente1 = new Oyente_Seleccion();

        porcent = new JMenuItem("Porcentaje");
        porcent.addActionListener(Oyente1);
        dispos.add(porcent);

        portiempo = new JMenuItem("Tiempo");
        portiempo.addActionListener(Oyente1);
        dispos.add(portiempo);

        barra_opt.add(dispos);
        setJMenuBar(barra_opt);
    }

    class Oyente_Seleccion implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == porcent) {
                esPorcentaje = true;
            } else {
                esPorcentaje = false;
            }
            repaint();
        }

    }

    class Lamina extends JPanel {

        public Set<String> nombres;
        public Float[] horas;
        public Integer[] minutos;
        public List<Color> colores;
        public List<Integer> angulos;

        public Lamina(HashMap<String, Float> map) {

            setLayout(null);

            nombres = map.keySet();
            horas = new Float[map.values().size()];
            minutos = new Integer[map.values().size()];
            angulos = new LinkedList<>();

            int i = 0;
            Iterator<Float> it = map.values().iterator();

            while (it.hasNext()) {
                horas[i] = it.next();
                i++;
            }

            if (nombres.size() != horas.length || nombres.size() > 5) {
                return;
            }

            addLabels(nombres);
            setColors();
            procesarDatos(horas);//Guarda en un objeto List los minutos

        }

        @Override
        public void paintComponent(Graphics g) {
            //Cambiar referencias de variables para mayor entendimiento --> p.j horas.length

            if (nombres.size() != horas.length || nombres.size() > 5) {
                return;
            }

            int yR = 30, yH = 40;
            int suma = 0;

            int m_total = 0;

            for (int n : minutos) {
                m_total += n;
            }

            for (int i = 0; i < nombres.size(); i++) {

                g.setColor(Color.BLACK);

                if (esPorcentaje == false) {

                    if (minutos[i] < 60) {
                        g.drawString(": " + minutos[i] + "m", 79, yH);
                    } else {
                        g.drawString(": " + horas[i] + "h", 79, yH);
                        //minutos
                    }

                } else {
                    g.drawString(": " + minutos[i] * 100 / m_total + "%", 79, yH);
                }
                g.setColor(colores.get(i));
                g.fillRect(20, yR, 10, 10);
                g.fillArc(156, 45, 100, 100, suma, Math.round(angulos.get(i)));

                suma += angulos.get(i);

                yR += 25;
                yH += 25;
            }

        }

        private void procesarDatos(Float[] horas) {

            int total = 0;

            //Conversion a minutos
            int i = 0;
            for (float h : horas) {

                minutos[i] = (int) (h * 60);
                i++;
            }

            //Suma de todos los minutos
            for (int m : minutos) {
                total += m;
            }

            //Adicion de angulos
            for (int m : minutos) {
                angulos.add(m * 360 / total);
            }

        }

        private void addLabels(Set<String> noms) {

            int x = 30, y = 30;

            Iterator<String> it = noms.iterator();

            while (it.hasNext()) {

                JLabel NLabel = new JLabel(it.next());
                NLabel.setBounds(x, y, 100, 15);
                add(NLabel);

                y += 25;
            }

        }

        private void setColors() {

            colores = new LinkedList<Color>();
            colores.add(Color.GREEN);
            colores.add(Color.RED);
            colores.add(Color.MAGENTA);
            colores.add(Color.BLUE);
            colores.add(Color.CYAN);
            colores.add(Color.ORANGE);
            Collections.shuffle(colores);

        }

    }

}
