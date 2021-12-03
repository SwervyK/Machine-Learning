import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class runner extends JPanel {
    
    // polygon
    static List<Point> points = new ArrayList<>();
    static int[] polygonX = new int[0];
    static int[] polygonY = new int[0];
    
    // graph
    public static int axesLength = 180;
    
    // states
    static boolean reset = false;
    static boolean start = false;
    static boolean awake = false;
    static double clockSpeed = 100;
    static double clock = 0;
    int clock2 = 0;
    static boolean multiNetwork = false;
    
    // classes
    static FileManager files = new FileManager();
    static NeuralNetwork nn = new NeuralNetwork(5, 7, 5, (int)Math.random() * 100);
    static Object object = new Object();
    static int numNetoworks = 50;
    static NeuralNetwork[] networks = new NeuralNetwork[numNetoworks];
    static Object[] objects = new Object[numNetoworks];
    
    public void paintComponent(Graphics g) {
        if (clock % clockSpeed == 0 && awake) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            UpdatePolygon(reset);
            g2.setColor(Color.BLACK);
            g2.fillPolygon(polygonX, polygonY, polygonX.length);
            if (start) {
                if (!multiNetwork) {
                    MoveObject(object, nn, g2, false);
                }
                else {
                    for (int i = 0; i < numNetoworks; i++) {
                        MoveObject(objects[i], networks[i], g2, false);
                    }
                }
            }
            else {
                MoveObject(object, nn, g2, true);
            }
            reset = false;
            
            
            int hash = 2;
            Point start = new Point(150, 200);
            int yMulti = 200;
            g2.setColor(Color.black);
            g2.drawLine(start.x, start.y, start.x + axesLength, start.y);
            g2.drawLine(start.x, start.y, start.x, start.y - axesLength);
            // create hatch marks for y axis. 
            for (int i = 0; i < 10; i++) {
                int x0 = start.x + hash;
                int x1 = start.x - hash;
                int y0 = start.y - ((i + 1) * (axesLength/10));
                int y1 = y0;
                g2.drawLine(x0, y0, x1, y1);
            }
            // and for x axis
            for (int i = 0; i < nn.errorGraph.size() - 1; i++) {
                int x0 = (int)Math.round(start.x + ((i + 1) * (axesLength/Double.valueOf(nn.errorGraph.size()))));
                int x1 = x0;
                int y0 = start.y + hash;
                int y1 = start.y - hash;
                if (x0 > (start.x + axesLength)) {
                    break;
                }
                g2.drawLine(x0, y0, x1, y1);
            }
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(Color.green);
            for (int i = 0; i < nn.errorGraph.size() - 1; i++) {
                int x1 = (int)Math.round(start.x + ((i) * (axesLength/Double.valueOf(nn.errorGraph.size()))));
                int y1 = (int)Math.round(start.y - (nn.errorGraph.get(i) * yMulti));
                int x2 = (int)Math.round(start.x + ((i + 1) * (axesLength/Double.valueOf(nn.errorGraph.size()))));
                int y2 = (int)Math.round(start.y - (nn.errorGraph.get(i + 1) * yMulti));
                g2.drawLine(x1, y1, x2, y2);
            }            
        }
        clock++;
        repaint(0, 0, getWidth(), getHeight());
    }
    
    private void MoveObject(Object o, NeuralNetwork n, Graphics2D g, boolean isStatic) {
        if (isStatic) {
            Point movePos = o.Move(new Point(0, 0), polygonX, polygonY);
            g.fillRect(movePos.x, movePos.y, o.playerSize, o.playerSize);
            return;
        }
        double[][] direction = o.getDirection(polygonX, polygonY, n, g);
        int chosenDirection = n.aiUpdate(direction, o);
        Point velocity = (start) ? o.getRay(chosenDirection)[1] : new Point(0,0);
        //Point velocity = new Point(-1, -1);
        Point movePos = o.Move(velocity, polygonX, polygonY);
        g.setColor(Color.black);
        g.fillRect(movePos.x - (o.playerSize/2), movePos.y - (o.playerSize/2), o.playerSize, o.playerSize);
    }
    
    private void UpdatePolygon(boolean wantReset) {
        int i = 0;
        if (!wantReset) {
            if (points.size() > 0) {
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
    
    public runner() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                repaint(0, 0, getWidth(), getHeight());
            }
        });
    }
    
    public JPanel Buttons() {
        var startButton = new JButton("Start");
        var stopButton = new JButton("Stop");
        var saveButton = new JButton("Save");
        var loadButton = new JButton("Load");
        var saveBrainButton = new JButton("Save Brain");
        var loadBrainButton = new JButton("Load Brain");
        startButton.addActionListener(e -> Start());
        stopButton.addActionListener(e -> Stop());
        saveButton.addActionListener(e -> Save());
        loadButton.addActionListener(e -> Load());
        saveBrainButton.addActionListener(e -> SaveBrain());
        loadBrainButton.addActionListener(e -> LoadBrain());
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        //buttonPanel.add(saveBrainButton);
        buttonPanel.add(loadBrainButton);
        
        return buttonPanel;
    }
    
    public void Start() {
        if (!multiNetwork) {
            object.Reset();
            nn.seed = (int)(Math.random() * 100);
            nn.randomiseNetwork();
        }
        else {
            for (int i = 0; i < numNetoworks; i++) {
                objects[i].Reset();
                networks[i].seed = (int)(Math.random() * 100);
                networks[i].randomiseNetwork();
            }
        }
        
        start = true;
    }
    
    public void Stop() {
        System.out.println("Stoped");
        start = false;
        reset = true;
        if (!multiNetwork) {
            object.Reset();
            nn.Reset();
        }
        else {
            for (int i = 0; i < numNetoworks; i++) {
                objects[i].Reset();
                networks[i].Reset();
            }
        }
        repaint(0, 0, getWidth(), getHeight());
    }
    
    public void Save() {
        files.PolygonSave(points);
    }
    
    public void SaveBrain() {
        files.SaveingBrain(nn.w(), nn.b());
    }
    
    public void LoadBrain() {
        files.LoadBrain(3, 5, 3, runner.this);
    }
    
    public String[] FileLoad(String dialog) {
        return files.FileLoading(dialog, runner.this);
    }
    
    public void Load() {
        try {
            String[] polygonData = FileLoad("Select Polygon Data");
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
    
    public static void main(String[] args) {
        runner r = new runner(); 
        r.Setup();
        awake = true;
    }
    
    public void Setup() {
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("Simple Sketching Program");
            frame.getContentPane().add(new runner(), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(Buttons(), BorderLayout.SOUTH);
            frame.setSize(400, 300);
            //frame.pack();
            frame.setVisible(true);
        });
        files.fileSetup();
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new Object();
        }
        for (int i = 0; i < networks.length; i++) {
            networks[i] = new NeuralNetwork((int)(Math.random() * 100));
            networks[i].randomiseNetwork();
        }
    }
}