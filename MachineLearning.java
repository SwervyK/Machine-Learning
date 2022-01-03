import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {
    
    // polygon
    static List<Point> points = new ArrayList<>();
    static int[] polygonX = new int[0];
    static int[] polygonY = new int[0];
    
    // graph
    public static int axesLength = 180;
    
    // states
    static boolean reset = false;
    static boolean start = false;
    static double clockSpeed = 20;
    static boolean multiNetwork = false;
    static boolean ai = true;
    
    // classes
    static NeuralNetwork nn = new NeuralNetwork(5, 7, 5, (int)Math.random() * 100);
    static Object object = new Object();
    static int numNetoworks = 50;
    static NeuralNetwork[] networks = new NeuralNetwork[numNetoworks];
    static Object[] objects = new Object[numNetoworks];
    
    public static void Update() {
        UpdatePolygon(reset);
        gui.MakeGraph(150, 200, axesLength, object.getDirectionGraph(), Color.RED, 7);
        gui.MakeGraph(150, 200, axesLength, nn.errorGraph, Color.GREEN, 1);
        gui.drawPolygon(polygonX, polygonY, Color.BLACK);
        if (start) {
            if (!multiNetwork) {
                MoveObject(object, nn, false);
            }
            else {
                for (int i = 0; i < numNetoworks; i++) {
                    MoveObject(objects[i], networks[i], false);
                }
            }
        }
        else {
            MoveObject(object, nn, true);
        }
        reset = false;

        try {
            Thread.sleep((long)clockSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Update();
    }
    
    private static void MoveObject(Object o, NeuralNetwork n, boolean isStatic) {
        if (isStatic) {
            return;
        }
        double[][] direction = o.getDirections(polygonX, polygonY, n);
        int chosenDirection = n.aiUpdate(direction, ai, o);
        Point velocity = (start) ? o.getRay(chosenDirection)[1] : new Point(0,0);
        //Point velocity = new Point(-1, -1);
        Point movePos = o.Move(velocity, polygonX, polygonY);
        gui.drawCube(movePos.x - (o.getPlayerSize()/2), movePos.y - (o.getPlayerSize()/2), o.getPlayerSize(), o.getPlayerSize(), Color.BLACK);
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
    
    public static void setPoints(Point point) {
        points.add(point);
    }

    public static void debugLines(int x, int y) {
        //graphics.resetLine();
        //graphics.drawLine(x, y, x, y, Color.RED);
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

    public static int[] getCube() {
        int[] result = {object.getPlayerPos().x, object.getPlayerPos().y, object.getPlayerSize(), object.getPlayerSize()}; 
        return result;
    }

    public static int[][] getPolygon() {
        int[][] result = {polygonX, polygonY}; 
        return result;
    }
}