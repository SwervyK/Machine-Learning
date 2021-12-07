import javax.swing.*;
import java.awt.*;

public class gui extends JPanel {

    private static JFrame frame = new JFrame("Simple Sketching Program");
    public static Graphics g;
    public static Graphics2D g2;

    public gui() {
        repaint(0, 0, getWidth(), getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        gui.g = g;
        gui.g2 = g2;
    }

    public static void Setup() {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().add(new MachineLearning(), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(Buttons(), BorderLayout.SOUTH);
            frame.setSize(400, 300);
            frame.setVisible(true);
        });
    }

    public static JPanel Buttons() {
        var startButton = new JButton("Start");
        var stopButton = new JButton("Stop");
        var saveButton = new JButton("Save");
        var loadButton = new JButton("Load");
        var saveBrainButton = new JButton("Save Brain");
        var loadBrainButton = new JButton("Load Brain");
        startButton.addActionListener(e -> MachineLearning.Start());
        stopButton.addActionListener(e -> MachineLearning.Stop());
        saveButton.addActionListener(e -> MachineLearning.Save());
        loadButton.addActionListener(e -> MachineLearning.Load());
        saveBrainButton.addActionListener(e -> MachineLearning.SaveBrain());
        loadBrainButton.addActionListener(e -> MachineLearning.LoadBrain());
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        //buttonPanel.add(saveBrainButton);
        buttonPanel.add(loadBrainButton);
        
        return buttonPanel;
    }

    public static JFrame getFrame() {
        return frame;
    } 
}
