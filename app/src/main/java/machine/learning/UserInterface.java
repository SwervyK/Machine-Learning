package machine.learning;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserInterface extends JPanel {
    
    // Constants
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    public static final int AXES_LENGTH = 180;

    // UI
    private JFrame frame = new JFrame("Machine Learning");
    // private BufferedImage buffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    // Polygon
    private List<Point> points = new ArrayList<>();
    private int[] polygonX = new int[0];
    private int[] polygonY = new int[0];
    
    // States
    private boolean hasStarted = false;
    private double clockSpeed = 20;
    private boolean useAI = false;
    
    // Classes
    private Random random = new Random();
    private NeuralNetwork nn = new NeuralNetwork(5, 7, 5, random.nextInt() * 100);
    private Box box = new Box(100, 150);
    
    public UserInterface() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                 points.add(new Point(e.getX(), e.getY()));
            }
        });
    }

    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface();
        userInterface.createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame.add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(buttons(), BorderLayout.SOUTH);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setVisible(true);

        FileManager.incrementVersion();
        update();
    }

    private JPanel buttons() {
        var startButton = new JButton("Start");
        var stopButton = new JButton("Stop");
        var saveButton = new JButton("Save");
        var loadButton = new JButton("Load");
        var saveBrainButton = new JButton("Save Brain");
        var loadBrainButton = new JButton("Load Brain");
        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        saveButton.addActionListener(e -> FileManager.polygonSave(points));
        loadButton.addActionListener(e -> load());
        saveBrainButton.addActionListener(e -> FileManager.savingBrain(nn.getW(), nn.getB()));
        loadBrainButton.addActionListener(e -> FileManager.loadBrain(3, 5, 3));
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        //buttonPanel.add(saveBrainButton);
        buttonPanel.add(loadBrainButton);
        
        return buttonPanel;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw graphs
        makeGraph(150, 200, AXES_LENGTH, box.getDirectionGraph(), Color.RED, 7, g);
        makePlainGraph(150, 200, AXES_LENGTH, nn.errorGraph, Color.GREEN, 1, g);
        
        // Draw Polygon
        g.setColor(Color.BLACK);
        g.fillPolygon(polygonX, polygonY, polygonX.length);
        
        // Draw Cube
        moveObject(g);
    }
    
    private void update() {
        updatePolygon();
        
        try {
            Thread.sleep((long)clockSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
        update();
    }
    
    private void makeGraph(int x, int y, int length, List<Double> values, Color c, int range, Graphics g) {
        int hash = 2;
        Point start = new Point(x, y);
        int yMulti = length/range;
        g.setColor(Color.BLACK);
        g.drawLine(start.x, start.y, start.x + length, start.y);
        g.drawLine(start.x, start.y, start.x, start.y - length);
        // create hatch marks for y axis. 
        for (int i = 0; i < 10; i++) {
            int yPos = start.y - ((i + 1) * (length/10));
            g.drawLine(start.x + hash, yPos, start.x - hash, yPos);
        }
        // and for x axis
        for (int i = 0; i < values.size() - 1; i++) {
            int xPos = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            if (xPos > (start.x + length)) {
                break;
            }
            g.drawLine(xPos, start.y + hash, xPos, start.y - hash);
        }
        for (int i = 0; i < values.size() - 1; i++) {
            int x1 = (int)Math.round(start.x + ((i ) * (length/Double.valueOf(values.size()))));
            int y1 = (int)Math.round(start.y - (values.get(i) * yMulti));
            int x2 = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            int y2 = (int)Math.round(start.y - (values.get(i + 1) * yMulti));
            g.setColor(c);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void makePlainGraph(int x, int y, int length, List<Double> values, Color c, int range, Graphics g) {
        Point start = new Point(x, y);
        int yMulti = length/range;
        for (int i = 0; i < values.size() - 1; i++) {
            int x1 = (int)Math.round(start.x + ((i ) * (length/Double.valueOf(values.size()))));
            int y1 = (int)Math.round(start.y - (values.get(i) * yMulti));
            int x2 = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            int y2 = (int)Math.round(start.y - (values.get(i + 1) * yMulti));
            g.setColor(c);
            g.drawLine(x1, y1, x2, y2);
        }
    }
    
    private void moveObject(Graphics g) {
        double[][] direction = box.getDirections(polygonX, polygonY, nn, g);
        int chosenDirection = nn.aiUpdate(direction, useAI, box);
        Point velocity = (hasStarted) ? box.getRay(chosenDirection)[1] : new Point(0,0);
        Point movePos = box.move(velocity, polygonX, polygonY);
        g.setColor(Color.BLACK);
        g.fillRect(movePos.x - (box.getPlayerSize()/2), movePos.y - (box.getPlayerSize()/2), box.getPlayerSize(), box.getPlayerSize());
    }
    
    private void updatePolygon() {
        int i = 0;
        if (!points.isEmpty()) {
            polygonX = new int[points.size()];
            polygonY = new int[points.size()];
            for (Point point : points) {
                polygonX[i] = point.x;
                polygonY[i] = point.y;
                i++;
            }
        }
    }
    
    private void start() {
        box.reset();
        nn.setSeed(random.nextInt() * 100);
        nn.randomizeNetwork();
        
        hasStarted = true;
    }
    
    private void stop() {
        System.out.println("Stopped");
        hasStarted = false;
        box.reset();
        nn.reset();
    }
    
    private void load() {
        try {
            String[] polygonData = FileManager.fileLoading("Select Polygon Data");
            for (int i = 0; i < polygonData.length; i++) {
                int xStart = polygonData[i].indexOf("x") + 1;
                int xEnd = polygonData[i].indexOf(",");
                int yStart = polygonData[i].indexOf("y") + 1;
                int yEnd = polygonData[i].indexOf("]");
                int subX = Integer.parseInt(polygonData[i].substring(xStart + 1, xEnd));
                int subY = Integer.parseInt(polygonData[i].substring(yStart + 1, yEnd));
                Point point = new Point(subX, subY);
                if (points.size() >= polygonData.length) {
                    points.set(i, point);
                } 
                else {
                    points.add(i, point);
                }
            }
            if (points.size() > polygonData.length) {
                for (int i = polygonData.length; i < points.size(); i++) {
                    points.remove(i);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
