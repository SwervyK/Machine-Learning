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
    
    // data
    static List<Point> points = new ArrayList<>();
    int[] polygonX = new int[0];
    int[] polygonY = new int[0];
    boolean reset = false;
    double clockSpeed = 100;
    double clock = 0;
    static boolean start = false;
    static boolean awake = false;
    
    // classes
    static FileManager files = new FileManager();
    static NeuralNetwork nn = new NeuralNetwork();
    static Object object = new Object();
    

    
    public void paintComponent(Graphics g) {
        if (clock % clockSpeed == 0 && awake) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            UpdatePolygon(reset);
            Point movePos = new Point(100, 100);
            if (start) {
                int chosenDirection = nn.aiUpdate(object.getDirection(polygonX, polygonY));
                Point velocity = object.getRay(chosenDirection)[1];
                movePos = object.Move(velocity, polygonX, polygonY); 
            }
            reset = false;
            g2.fillPolygon(polygonX, polygonY, polygonX.length);
            g2.fillRect(movePos.x, movePos.y, object.playerSize, object.playerSize);
        }
        clock++;
        repaint(0, 0, getWidth(), getHeight());
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
        startButton.addActionListener(e -> Start());
        stopButton.addActionListener(e -> Stop());
        saveButton.addActionListener(e -> Save());
        loadButton.addActionListener(e -> Load());
        saveBrainButton.addActionListener(e -> SaveBrain());
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveBrainButton);
        
        return buttonPanel;
    }
    
    public void Start() {
        nn.randomiseNetwork();
        start = true;
    }
    
    public void Stop() {
        System.out.println("Stoped");
        start = false;
        reset = true;
        repaint(0, 0, getWidth(), getHeight());
    }
    
    public void Save() {
        files.PolygonSave(points);
    }

    public void SaveBrain() {
        files.SaveingBrain(nn.w(), nn.b());
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
    }
}

/*
public void Load() {
    JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(version);
    fc.setDialogTitle("Select Polygon File");
    int returnVal = fc.showOpenDialog(runner.this);
    File file = fc.getSelectedFile();
    if (returnVal == 0) {
        try {
            String[] polygonData = new String[(int)Files.lines(file.toPath()).count()];
            polygonData =  readData(file);
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
*/