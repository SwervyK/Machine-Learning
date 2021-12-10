import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.image.*;

public class gui extends JPanel {
    
    private static JFrame frame = new JFrame("Simple Sketching Program");
    private static BufferedImage buffer = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
    private static boolean wireframe = false; 

    public gui() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                MachineLearning.setPoints(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                MachineLearning.setPoints(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.drawRect(0, 0, getWidth(), getHeight());
        g2.drawImage(buffer, null, 0, 0);
        buffer.flush();
        repaint();
    }
    
    public static void MakeGraph(int X, int Y, int length, ArrayList<Double> values, Color c, int range) {
        int hash = 2;
        Point start = new Point(X, Y);
        int yMulti = length/range;
        drawLine(start.x, start.y, start.x + length, start.y, Color.BLACK);
        drawLine(start.x, start.y, start.x, start.y - length, Color.BLACK);
        // create hatch marks for y axis. 
        for (int i = 0; i < 10; i++) {
            int x0 = start.x + hash;
            int x1 = start.x - hash;
            int y0 = start.y - ((i + 1) * (length/10));
            int y1 = y0;
            drawLine(x0, y0, x1, y1, Color.BLACK);
        }
        // and for x axis
        for (int i = 0; i < values.size() - 1; i++) {
            int x0 = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            int x1 = x0;
            int y0 = start.y + hash;
            int y1 = start.y - hash;
            if (x0 > (start.x + length)) {
                break;
            }
            drawLine(x0, y0, x1, y1, Color.BLACK);
        }
        for (int i = 0; i < values.size() - 1; i++) {
            int x1 = (int)Math.round(start.x + ((i ) * (length/Double.valueOf(values.size()))));
            int y1 = (int)Math.round(start.y - (values.get(i) * yMulti));
            int x2 = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            int y2 = (int)Math.round(start.y - (values.get(i + 1) * yMulti));
            drawLine(x1, y1, x2, y2, c);
        }
    }
    
    public static void drawCube(int x, int y, int width, int height, Color c) {
        Graphics g = buffer.getGraphics();
        g.setColor(c);
        if (wireframe) {
            g.drawRect(x, y, width, height);
        }
        else {
            g.fillRect(x, y, width, height);
        }
        g.dispose();
    }
    
    public static void drawLine(int x1, int y1, int x2, int y2, Color c) {
        Graphics g = buffer.getGraphics();
        g.setColor(c);
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    public static void drawPolygon(int[] polygonX, int[] polygonY, Color c) {
        Graphics g = buffer.getGraphics();
        g.setColor(c);
        if (wireframe) {
            g.drawPolygon(polygonX, polygonY, polygonX.length);
        }
        else {
            g.fillPolygon(polygonX, polygonY, polygonX.length);
        }
        g.dispose();
    }

    public static void setWireframe(boolean w) {
        wireframe = w;
    }

    
    public static void Setup() {
        frame.getContentPane().add(new gui(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(Buttons(), BorderLayout.SOUTH);
        frame.setSize(400, 300);
        frame.setVisible(true);
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
    
    public JFrame getFrame() {
        return frame;
    } 
}
