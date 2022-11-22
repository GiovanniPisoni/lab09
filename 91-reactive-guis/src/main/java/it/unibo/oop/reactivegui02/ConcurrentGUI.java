package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;




/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final int SW_PROPORTION = 7;
    private static final int SH_PROPORTION = 14;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    final Agent agent = new Agent();

    public ConcurrentGUI() {
        super();
        final JPanel canvas = new JPanel();
        canvas.setLayout(new FlowLayout());
        display.setText("0");

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

        new Thread(agent).start();

        /*
         *Listener
        */
        stop.addActionListener((e) -> agent.stop());
        up.addActionListener((e) -> agent.up());
        down.addActionListener((e) -> agent.down());

    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
                while(!this.stop) {
                    try {
                        this.counter += this.down ? -1 : +1;
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(Integer.toString(counter)));
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
    
            public void stop() {
                this.stop = true;
            }
    
            public void up() {
                this.down = false;
            }
    
            public void down() {
                this.down = true;
            }


    }
}
