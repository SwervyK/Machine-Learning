import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning extends JPanel {
    
    // polygon
    static List<Point> points = new ArrayList<>();
    static int[] polygonX = new int[0];
    static int[] polygonY = new int[0];
    
    // graph
    public static int axesLength = 180;
    
    // states
    static boolean reset = false;
    static boolean start = false;
    static double clockSpeed = 100;
    static double clock = 0;
    static boolean multiNetwork = false;
    
    // classes
    static NeuralNetwork nn = new NeuralNetwork(5, 7, 5, (int)Math.random() * 100);
    static Object object = new Object();
    static int numNetoworks = 50;
    static NeuralNetwork[] networks = new NeuralNetwork[numNetoworks];
    static Object[] objects = new Object[numNetoworks];
    
    public void paintComponent(Graphics g) {
        if (clock % clockSpeed == 0) {
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
            MakeGraph(g2, 150, 200, axesLength, object.getDirectionGraph(), Color.RED, 7);
            MakeGraph(g2, 150, 200, axesLength, nn.errorGraph, Color.GREEN, 1);        
        }
        clock++;
        repaint(0, 0, getWidth(), getHeight());
    }

    public static void Update() {
        if (clock % clockSpeed == 0) {
            UpdatePolygon(reset);
            gui.g2.setColor(Color.BLACK);
            gui.g2.fillPolygon(polygonX, polygonY, polygonX.length);
            if (start) {
                if (!multiNetwork) {
                    MoveObject(object, nn, gui.g2, false);
                }
                else {
                    for (int i = 0; i < numNetoworks; i++) {
                        MoveObject(objects[i], networks[i], gui.g2, false);
                    }
                }
            }
            else {
                MoveObject(object, nn, gui.g2, true);
            }
            reset = false;
            MakeGraph(gui.g2, 150, 200, axesLength, object.getDirectionGraph(), Color.RED, 7);
            MakeGraph(gui.g2, 150, 200, axesLength, nn.errorGraph, Color.GREEN, 1);        
        }
        clock++;
        Update();
    }
    
    private static void MoveObject(Object o, NeuralNetwork n, Graphics2D g, boolean isStatic) {
        if (isStatic) {
            g.fillRect(o.getPlayerPos().x, o.getPlayerPos().y, o.getPlayerSize(), o.getPlayerSize());
            return;
        }
        double[][] direction = o.getDirections(polygonX, polygonY, n, g);
        int chosenDirection = n.aiUpdate(direction, o);
        Point velocity = (start) ? o.getRay(chosenDirection)[1] : new Point(0,0);
        //Point velocity = new Point(-1, -1);
        Point movePos = o.Move(velocity, polygonX, polygonY);
        g.setColor(Color.black);
        g.fillRect(movePos.x - (o.getPlayerSize()/2), movePos.y - (o.getPlayerSize()/2), o.getPlayerSize(), o.getPlayerSize());
    }
    
    private static void MakeGraph(Graphics2D g2, int X, int Y, int length, ArrayList<Double> values, Color c, int range) {
        int hash = 2;
        Point start = new Point(X, Y);
        int yMulti = length/range;
        g2.setColor(Color.black);
        g2.drawLine(start.x, start.y, start.x + length, start.y);
        g2.drawLine(start.x, start.y, start.x, start.y - length);
        // create hatch marks for y axis. 
        for (int i = 0; i < 10; i++) {
            int x0 = start.x + hash;
            int x1 = start.x - hash;
            int y0 = start.y - ((i + 1) * (length/10));
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
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
            g2.drawLine(x0, y0, x1, y1);
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(c);
        for (int i = 0; i < values.size() - 1; i++) {
            int x1 = (int)Math.round(start.x + ((i ) * (length/Double.valueOf(values.size()))));
            int y1 = (int)Math.round(start.y - (values.get(i) * yMulti));
            int x2 = (int)Math.round(start.x + ((i + 1) * (length/Double.valueOf(values.size()))));
            int y2 = (int)Math.round(start.y - (values.get(i + 1) * yMulti));
            g2.drawLine(x1, y1, x2, y2);
        }
    }
    
    private static void UpdatePolygon(boolean wantReset) {
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

    public static void Start() {
        if (!multiNetwork) {
            object.Reset();
            nn.setSeed((int)(Math.random() * 100));
            nn.randomiseNetwork();
        }
        else {
            for (int i = 0; i < numNetoworks; i++) {
                objects[i].Reset();
                networks[i].setSeed((int)(Math.random() * 100));
                networks[i].randomiseNetwork();
            }
        }
        
        start = true;
    }

    public static void Stop() {
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
    }
    
    public static void Save() {
        FileManager.PolygonSave(points);
    }
    
    public static void SaveBrain() {
        FileManager.SaveingBrain(nn.getW(), nn.getB());
    }
    
    public static void LoadBrain() {
        FileManager.LoadBrain(3, 5, 3);
    }
    
    public static String[] FileLoad(String dialog) {
        return FileManager.FileLoading(dialog);
    }
    
    public static void Load() {
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
        gui.Setup();
        Setup();
        Update();
    }
    
    public static void Setup() {
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new Object();
        }
        for (int i = 0; i < networks.length; i++) {
            networks[i] = new NeuralNetwork((int)(Math.random() * 100));
            networks[i].randomiseNetwork();
        }
        FileManager.fileSetup();
    }
}