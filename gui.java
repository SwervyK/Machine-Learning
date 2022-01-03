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
    private static int windowWidth = 400;
    private static int windowHeight = 400;
    private static BufferedImage buffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
    private static boolean wireframe = false;
    static class oldCube {
        public static int x = 0;
        public static int y = 0;
        public static int height = 1;
        public static int width = 1;
    };
    static class oldLine {
        public static int x1 = 0;
        public static int y1 = 0;
        public static int x2 = 0;
        public static int y2 = 0;
    };
    static class oldPolygon {
        public static int[] polygonX = {1};
        public static int[] polygonY = {1};
    };
    
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
        //g2.drawRect(0, 0, getWidth(), getHeight());
        g2.drawImage(buffer, null, 0, 0);
        //Graphics bufferReset = buffer.getGraphics();
        //bufferReset.drawRect(0, 0, buffer.getWidth(), buffer.getHeight());
        repaint();
    }
    
    public static void MakeGraph(int X, int Y, int length, ArrayList<Double> values, Color c, int range) {
        int hash = 2;
        resetLines(X - hash, Y-length, length + hash, length + 2 * hash);
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
        g.setColor(Color.WHITE);
        if (wireframe) {
            g.drawRect(oldCube.x, oldCube.y, oldCube.width, oldCube.height);
            g.setColor(c);
            g.drawRect(x, y, width, height);
        }
        else {
            g.fillRect(oldCube.x, oldCube.y, oldCube.width, oldCube.height);
            g.setColor(c);
            g.fillRect(x, y, width, height);
        }
        oldCube.x = x;
        oldCube.y = y;
        oldCube.height = height;
        oldCube.width = width;
        g.dispose();
    }
    
    public static void drawLine(int x1, int y1, int x2, int y2, Color c) {
        //System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2);
        //System.out.println("x1: " + oldLine.x1 + " y1: " + oldLine.y1 + " x2: " + oldLine.x2 + " y2: " + oldLine.y2 + " ///////////////");
        Graphics g = buffer.getGraphics();
        g.setColor(c);
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
        //oldLine.x1 = x1;
        //oldLine.y1 = y1;
        //oldLine.x2 = x2;
        //oldLine.y2 = y2;
    }

    public static void resetLines(int x, int y, int w, int h) {
        Graphics g = buffer.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.dispose();
    }
    
    public static void drawPolygon(int[] polygonX, int[] polygonY, Color c) {
        Graphics g = buffer.getGraphics();
        g.setColor(Color.WHITE);
        if (wireframe) {
            g.drawPolygon(oldPolygon.polygonX, oldPolygon.polygonY, oldPolygon.polygonX.length);
            g.setColor(c);
            g.drawPolygon(polygonX, polygonY, polygonX.length);
        }
        else {
            g.fillPolygon(oldPolygon.polygonX, oldPolygon.polygonY, oldPolygon.polygonX.length);
            g.setColor(c);
            g.fillPolygon(polygonX, polygonY, polygonX.length);
        }
        g.dispose();
        oldPolygon.polygonX = polygonX;
        oldPolygon.polygonY = polygonY; 
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
        Graphics g = buffer.getGraphics();
        g.fillRect(0, 0, windowWidth, windowHeight);
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
