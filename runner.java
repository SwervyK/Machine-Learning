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
import java.util.Arrays;
import java.util.List;

public class runner extends JPanel {
    
    List<Point> points = new ArrayList<>();
    int[] polygonX = new int[0];
    int[] polygonY = new int[0];
    boolean draw = false;
    boolean reset = false;
    int playerSize = 10;
    int playerX = 100;
    int playerY = 150;
    int rayLength = 100;
    double clockSpeed = 100;
    double clock = 0;
    Point playerMove;
    int direction = 0;
    double error = 0.0;

    double[][] x = new double[5][1];
    double[][] w1 = new double[3][5];
    double[][] b1 = new double[3][1];
    double[][] hidden = new double[3][1];
    double[][] b2 = new double[5][1];
    double[][] w2 = new double[5][3];
    double[][] out = new double[5][1];

    public void aiStart() {
        randomiseMatrices(x);
        randomiseMatrices(w1);
        randomiseMatrices(b1);
        randomiseMatrices(hidden);
        randomiseMatrices(w2);
        randomiseMatrices(b2);
        double[][] in = {{1.0},
                         {0.0},
                         {0.0},
                         {1.0},
                         {0.0}
                        };
        double[][] answer = calculateMatrices(in);
        Point[] result = getRay(calculateOut(answer));
        playerMove = result[1];
    }

    public double[][] a1() {
        double[][] result = multiplyMatrices(w1, x);
        result = f(addMatrices(result, b1));
        return result;
    }

    public double[][] a2() {
        double[][] result = multiplyMatrices(w2, a1());
        result = f(addMatrices(result, b2));
        return result;
    }

    public double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }
    
        return result;
    }

    double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    public double[][] addMatrices(double[][] firstMatrix, double[][] secondMatrix)
    {
        double result[][] = new double[secondMatrix.length][secondMatrix[0].length];
 
        for (int row = 0; row < secondMatrix.length; row++)
            for (int col = 0; col < secondMatrix[0].length; col++)
                result[row][col] = firstMatrix[row][col] + secondMatrix[row][col];
 
        return result;
    }

    public double[][] calculateMatrices(double[][] input) {
        double result[][] = new double[1][5];
        x = input;
        hidden = a1();
        out = result = a2();
        for (int i = 0; i < a2().length; i++) {
            error += Math.pow((a2()[i][0] - getAnswer()[i]), 2);
        }
        error /= a2().length;
        return result;
    }

    public int calculateOut(double[][] in) {
        int result = 0;
        double oldValue = 0.0, value = 0.0;
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                value = in[row][col];
                if (value > oldValue) {
                    result = row;
                }
                else {
                    value = oldValue;
                }
                oldValue = value;
            }
        }
        return result;
    }

    public double[] getInputs() {
        return getDirection();
    }

    public double[] getAnswer() {
        double[] result = new double[out.length];
        int index = 0;
        double[] values = getDirection();
        double old = 0.0;
        int i = 0;
        for (double value : values) {
            if (value > old) {
                index = i;
            }
            old = value;
            i++;
        }
        int j = 0;
        for (double value : result) {
            if (j == index) {
                result[j] = 1;
            }
            j++;
        }
        return result;
    }

    public double[] getDirection() {
        double[] result = new double[5];
        int currentDistance = direction;
        for (int i = -2; i < 2; i++) {
            if (currentDistance + i < 0) {
                currentDistance += 8 + i;
            }
            result[i + 2] = getColissionDistance(currentDistance + i);
        }
        return result;
    }

    public void print2D(double[][] mat)
    {
        for (double[] row : mat)
            System.out.println(Arrays.toString(row));
    }

    public void randomiseMatrices(double[][] in) {
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                in[row][col] = Math.random();
            }
        }
    }

    public double[][] f(double[][] in) {
        double[][] result = new double[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                double x = in[row][col];
                result[row][col] = (1/( 1 + Math.pow(Math.E,(-1*x))));
            }
        }

        return result;
    }

    enum rays {
        middleRight,
        middleLeft,
        topRight,
        topLeft,
        top,
        bottomRight,
        bottomLeft,
        bottom,
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
    
    public void paintComponent(Graphics g) {
        if (clock % clockSpeed == 0) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            UpdatePolygon(reset);
            Move(playerMove);
            aiStart();
            reset = false;
            //g2.drawPolygon(polygonX, polygonY, polygonX.length);
            g2.fillPolygon(polygonX, polygonY, polygonX.length);
            //g2.drawRect(playerX, playerX, playerSize, playerSize);
            g2.fillRect(playerX, playerY, playerSize, playerSize);
        }
        clock++;
        repaint(0, 0, getWidth(), getHeight());
    }
    
    public JPanel Buttons() {
        var startButton = new JButton("Start");
        var stopButton = new JButton("Stop");
        startButton.addActionListener(e -> Start());
        stopButton.addActionListener(e -> Stop());
        
        var buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        return buttonPanel;
    }
    
    public void Start() {
        
    }
    
    public void Stop() {
        System.out.println("Stoped");
        reset = true;
        playerX = 100;
        playerY = 100;
        repaint(0, 0, getWidth(), getHeight());
    }

    public void Move(Point p) {
        if (p == null) {
            return;
        }
        Move(p.x, p.y);
    }
    
    public void Move(int x, int y) {
        direction =  ((int)Math.toDegrees(Math.atan((playerY-y)/(playerX-x)))/45) - 1;
        if (getColiding(playerX + x + playerSize/2, playerY + y + playerSize/2)) {
            Stop();
            return;
        }
        else {
            if (0 < x + playerX && x + playerX + playerSize < getWidth()) {
                playerX += x;
            }
            else {
                int sign = Integer.signum(x);
                playerX = ((sign < 0) ? 0 : getWidth() - playerSize);
            }
            if (0 < y + playerY && y + playerY + 10 < getHeight()) {
                playerY += y;
            }
            else {
                int sign = Integer.signum(y);
                playerY = ((sign < 0) ? 0 : getHeight() - 10);
            }
        }
        repaint(0, 0, getWidth(), getHeight());
    }

    public Point[] getRay(int d) {
        Point[] point = new Point[2];
        switch(d) {
            case 2:
                point[0] = new Point(playerX + playerSize, playerY + playerSize/2);
                point[1] = new Point(1, 0);
                return point;
            case 6:
                point[0] = new Point(playerX, playerY + playerSize/2);
                point[1] = new Point(-1, 0);
                return point;
            case 1:
                point[0] = new Point(playerX + playerSize, playerY);
                point[1] = new Point(1, -1);
                return point;
            case 7:
                point[0] = new Point(playerX, playerY);
                point[1] = new Point(-1, -1);
                return point;
            case 0:
                point[0] = new Point(playerX + playerSize/2, playerY);
                point[1] = new Point(0, -1);
                return point;
            case 3:
                point[0] = new Point(playerX + playerSize, playerY + playerSize);
                point[1] = new Point(1, 1);
                return point;
            case 5:
                point[0] = new Point(playerX, playerY + playerSize);
                point[1] = new Point(-1, 1);
                return point;
            case 4:
                point[0] = new Point(playerX + playerSize/2, playerY + playerSize);
                point[1] = new Point(0, 1);
                return point;
            default:
                System.out.println("Error passes in improper state - getRay");
                break;
        }
        return point;
    }

    public boolean getColiding(int x, int y) {
        return new Polygon(polygonX, polygonY, polygonX.length).contains(x, y);
    }
    
    public Point getColission(int d) {
        Point point = getRay(d)[0];
        Point value = getRay(d)[1];
        int x = point.x;
        int y = point.y;
        int i = 0;

        do {
            x += value.x;
            y += value.y;
            i++;
        }
        while(!getColiding(x, y) && i <= rayLength);

        return (i <= rayLength) ? new Point(x, y) : new Point(x + rayLength, y + rayLength);
    }

    public double getColissionDistance(int d) {
        double result = 0.0;
        result = Double.valueOf(Math.abs(getColission(d).x - playerX/ getColission(d).y - playerY));
        return result;
    }

    public static void main(String[] args) {
        runner r = new runner(); 
        r.Setup();
        r.aiStart();
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
}

/*
public Point[] getRay(rays r) {
    Point[] point = new Point[2];
    switch(r) {
        case middleRight:
            point[0] = new Point(playerX + playerSize, playerY + playerSize/2);
            point[1] = new Point(1, 0);
            direction = 2;
            return point;
        case middleLeft:
            point[0] = new Point(playerX, playerY + playerSize/2);
            point[1] = new Point(-1, 0);
            direction = 6;
            return point;
        case topRight:
            point[0] = new Point(playerX + playerSize, playerY);
            point[1] = new Point(1, -1);
            direction = 1;
            return point;
        case topLeft:
            point[0] = new Point(playerX, playerY);
            point[1] = new Point(-1, -1);
            direction = 7;
            return point;
        case top:
            point[0] = new Point(playerX + playerSize/2, playerY);
            point[1] = new Point(0, -1);
            direction = 0;
            return point;
        case bottomRight:
            point[0] = new Point(playerX + playerSize, playerY + playerSize);
            point[1] = new Point(1, 1);
            direction = 3;
            return point;
        case bottomLeft:
            point[0] = new Point(playerX, playerY + playerSize);
            point[1] = new Point(-1, 1);
            direction = 5;
            return point;
        case bottom:
            point[0] = new Point(playerX + playerSize/2, playerY + playerSize);
            point[1] = new Point(0, 1);
            direction = 4;
            return point;
        default:
            break;
    }
    System.out.println("Error passes in improper state - getRay");
    return point;
}
*/