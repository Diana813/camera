package dianaszczepankowska;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main extends JFrame {
    public Main() {
        setTitle("Wirtualna kamera");
        setSize(1600, 1000);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Scene scene = new Scene(getWidth(), getHeight());
        add(scene);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}