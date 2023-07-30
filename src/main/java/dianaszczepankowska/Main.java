package dianaszczepankowska;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main extends JFrame {

    public static final int SCREEN_WIDTH = 1600;
    public static final int SCREEN_HEIGHT = 1000;

    public Main() {
        setTitle("Wirtualna kamera");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Scene scene = new Scene();
        add(scene);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}