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
import java.awt.image.*;

public class UserInterface extends JPanel {
    
    // UI
    private static JFrame frame = new JFrame("Machine Learning");
    private static int windowWidth = 800;
    private static int windowHeight = 600;
    private static BufferedImage buffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
    private static boolean wireframe = false;

    // Polygon
    static List<Point> points = new ArrayList<>();
    static int[] polygonX = new int[0];
    static int[] polygonY = new int[0];
    
    // Graph
    public static final int AXES_LENGTH = 180;
    
    // States
    static boolean reset = false;
    static boolean start = false;
    static double clockSpeed = 20;
    static boolean multiNetwork = false;
    static boolean ai = true;
    
    // Classes
    static Random random = new Random();
    static NeuralNetwork nn = new NeuralNetwork(5, 7, 5, random.nextInt() * 100);
    static Box object = new Box();
    static int numNetworks = 50;
    static NeuralNetwork[] networks = new NeuralNetwork[numNetworks];
    static Box[] objects = new Box[numNetworks];
    
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
    
    public UserInterface() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setPoints(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setPoints(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        //g2.drawRect(0, 0, getWidth(), getHeight());
        g2.drawImage(buffer, null, 0, 0);
        //Graphics bufferReset = buffer.getGraphics();
        //bufferReset.drawRect(0, 0, buffer.getWidth(), buffer.getHeight());
        repaint();
    }
    
    public static void makeGraph(int x, int y, int length, List<Double> values, Color c, int range) {
        int hash = 2;
        resetLines(x - hash, y-length, length + hash, length + 2 * hash);
        Point start = new Point(x, y);
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
    
    public static JPanel buttons() {
        var startButton = new JButton("Start");
        var stopButton = new JButton("Stop");
        var saveButton = new JButton("Save");
        var loadButton = new JButton("Load");
        var saveBrainButton = new JButton("Save Brain");
        var loadBrainButton = new JButton("Load Brain");
        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        saveButton.addActionListener(e -> save());
        loadButton.addActionListener(e -> load());
        saveBrainButton.addActionListener(e -> saveBrain());
        loadBrainButton.addActionListener(e -> loadBrain());
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        //buttonPanel.add(saveBrainButton);
        buttonPanel.add(loadBrainButton);
        
        return buttonPanel;
    }

    public static void main(String[] args) {
        frame.getContentPane().add(new UserInterface(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(buttons(), BorderLayout.SOUTH);
        frame.setSize(windowWidth, windowHeight);
        frame.setVisible(true);
        Graphics g = buffer.getGraphics();
        g.fillRect(0, 0, windowWidth, windowHeight);
        setup();
        update();
    }
    
    public JFrame getFrame() {
        return frame;
    } 


    
    public static void update() {
        updatePolygon(reset);
        UserInterface.makeGraph(150, 200, AXES_LENGTH, object.getDirectionGraph(), Color.RED, 7);
        UserInterface.makeGraph(150, 200, AXES_LENGTH, nn.errorGraph, Color.GREEN, 1);
        UserInterface.drawPolygon(polygonX, polygonY, Color.BLACK);
        if (start) {
            if (!multiNetwork) {
                moveObject(object, nn, false);
            }
            else {
                for (int i = 0; i < numNetworks; i++) {
                    moveObject(objects[i], networks[i], false);
                }
            }
        }
        else {
            moveObject(object, nn, true);
        }
        reset = false;

        try {
            Thread.sleep((long)clockSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        update();
    }
    
    private static void moveObject(Box o, NeuralNetwork n, boolean isStatic) {
        if (isStatic) {
            return;
        }
        double[][] direction = o.getDirections(polygonX, polygonY, n);
        int chosenDirection = n.aiUpdate(direction, ai, o);
        Point velocity = (start) ? o.getRay(chosenDirection)[1] : new Point(0,0);
        //Point velocity = new Point(-1, -1);
        Point movePos = o.move(velocity, polygonX, polygonY);
        UserInterface.drawCube(movePos.x - (o.getPlayerSize()/2), movePos.y - (o.getPlayerSize()/2), o.getPlayerSize(), o.getPlayerSize(), Color.BLACK);
    }
    
    private static void updatePolygon(boolean wantReset) {
        int i = 0;
        if (!wantReset) {
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
        else {
            polygonX = new int[0];
            polygonY = new int[0];
            points = new ArrayList<>();
        }
    }
    
    public static void start() {
        if (!multiNetwork) {
            object.reset();
            nn.setSeed(random.nextInt() * 100);
            nn.randomizeNetwork();
        }
        else {
            for (int i = 0; i < numNetworks; i++) {
                objects[i].reset();
                networks[i].setSeed(random.nextInt() * 100);
                networks[i].randomizeNetwork();
            }
        }
        
        start = true;
    }
    
    public static void stop() {
        System.out.println("Stopped");
        start = false;
        reset = true;
        if (!multiNetwork) {
            object.reset();
            nn.reset();
        }
        else {
            for (int i = 0; i < numNetworks; i++) {
                objects[i].reset();
                networks[i].reset();
            }
        }
    }
    
    public static void save() {
        FileManager.polygonSave(points);
    }
    
    public static void saveBrain() {
        FileManager.savingBrain(nn.getW(), nn.getB());
    }
    
    public static void loadBrain() {
        FileManager.loadBrain(3, 5, 3);
    }
    
    public static String[] fileLoad(String dialog) {
        return FileManager.fileLoading(dialog);
    }
    
    public static void load() {
        try {
            String[] polygonData = fileLoad("Select Polygon Data");
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
    
    public static void setPoints(Point point) {
        points.add(point);
    }

    public static void debugLines(int x, int y) {
        //gui.drawLine(x, y, x, y, Color.RED);
    }
    
    // public static void main(String[] args) {
    //     UserInterface.setup();
    //     setup();
    //     update();
    // }
    
    public static void setup() {
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new Box();
        }
        for (int i = 0; i < networks.length; i++) {
            networks[i] = new NeuralNetwork(random.nextInt() * 100);
            networks[i].randomizeNetwork();
        }
        FileManager.fileSetup();
    }

    public static int[] getCube() {
        return new int[] {object.getPlayerPos().x, object.getPlayerPos().y, object.getPlayerSize(), object.getPlayerSize()};
    }
}
