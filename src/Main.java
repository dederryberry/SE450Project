import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           JFrame frame = new JFrame("SE450 Project - Swing Test");
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.setSize(300,200);

           JLabel label = new JLabel("Hello, World!", SwingConstants.CENTER);
           frame.add(label);

           frame.setLocationRelativeTo(null);
           frame.setVisible(true);
        });
    }

}
