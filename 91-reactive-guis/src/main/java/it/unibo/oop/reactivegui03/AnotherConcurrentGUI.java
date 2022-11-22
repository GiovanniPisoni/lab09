package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final int SW_PROPORTION = 7;
    private static final int SH_PROPORTION = 14;

    private volatile boolean stop;

    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public AnotherConcurrentGUI() {
        super();
        final JPanel canvas = new JPanel();
        canvas.setLayout(new FlowLayout());
        display.setText("0");
        final JButton stop = new JButton("stop");

        this.getContentPane().add(canvas);
        canvas.add(display);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);

        /*
         *Setting frame dimention
        */ 
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screen.getWidth() / SW_PROPORTION), (int) (screen.getHeight() / SH_PROPORTION));
        this.setLocationByPlatform(true);

        /* 
         * Setting closable and visible the frame
        */
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        final Agent agent = new Agent();
        final SecondAgent stopAll = new SecondAgent();
        new Thread(agent).start();
        new Thread(stopAll).start();

        /*
         *Listener
        */
        stop.addActionListener((e) -> agent.stop());
        up.addActionListener((e) -> agent.up());
        down.addActionListener((e) -> agent.down());

    }

    private class Agent implements Runnable {

        private int counter;
        private volatile boolean down;

        @Override
        public void run() {
            while (!AnotherConcurrentGUI.this.stop) {
                try {
                    this.counter += this.down ? -1 : +1;
                    AnotherConcurrentGUI.this.display.setText(Integer.toString(counter));
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    e.getStackTrace();
                }
            
            }
        }

        public void stop() {
            AnotherConcurrentGUI.this.stop = true;
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
        }

        public void up() {
            this.down = false;
        }

        public void down() {
            this.down = true;
        }


    }

    private class SecondAgent implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
                SwingUtilities.invokeAndWait(() -> {
                    AnotherConcurrentGUI.this.stop = true;
                    AnotherConcurrentGUI.this.up.setEnabled(false);
                    AnotherConcurrentGUI.this.down.setEnabled(false);
                });
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        
    } 
    
}

