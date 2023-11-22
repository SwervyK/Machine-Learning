package machine.learning;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Box {
    
    // data
    private boolean debug = true;
    private int playerSize = 10;
    private Point playerStart = new Point(100, 150);
    private Point playerPosition = new Point(100, 150);
    private int rayLength = 100;
    private int direction = 0;
    private int oldDirection = 0;
    private ArrayList<Double> directionGraph = new ArrayList<>(); 
    
    public Box(int startX, int startY) {
        playerStart = new Point(startX, startY);
        playerPosition = playerStart;
    }
    
    /**
    Gets forward directions of object 0-5
    @return the distances to objects of the network 0-5
    */
    public double[][] getDirections(int[] polygonX, int[] polygonY, NeuralNetwork nn, Graphics g) {
        double[][] result = new double[nn.getOutNodes()][1];
        int currentDirection;
        for (int i = -2; i <= 2; i++) {
            currentDirection = direction;
            currentDirection += i;
            if (currentDirection < 0) {
                System.out.print("1");
                currentDirection += 8;
            }
            if (currentDirection > 7) {
                System.out.print("2");
                currentDirection -= 8;
            }
            result[i + 2][0] = getCollisionDistance(currentDirection, polygonX, polygonY, g);
        }
        return result;
    }
    
    public int getDirection(int d) {
        int currentDirection = direction + d;
        if (currentDirection < 0) {
            System.out.print("3");
            currentDirection += 8;
        }
        if (currentDirection > 7) {
            System.out.print("4");
            currentDirection -= 8;
        }
        
        if (Math.abs(currentDirection-oldDirection)>=2) {
            System.out.print("5");
            return direction;
        }
        return currentDirection;
    }
    
    private double getCollisionDistance(int d, int[] polygonX, int[] polygonY, Graphics g) {
        Point[] ray = getRay(d);
        Point point = ray[0];
        Point value = ray[1];
        double x = point.x;
        double y = point.y;
        int i = 0;
        do {
            x += value.x * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.x
            y += value.y * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.y
            i++;
            if (debug) {
                g.setColor(Color.RED);
                g.drawLine((int)x, (int)y, (int)x, (int)y);
            }
        }
        while(!getColliding((int)x, (int)y, polygonX, polygonY) && i < rayLength);
        return Math.hypot(Math.abs((int)x - point.x), Math.abs((int)y - point.y));
    }
    
    private boolean getColliding(int x, int y, int[] polygonX, int[] polygonY) {
        return new Polygon(polygonX, polygonY, polygonX.length).contains(x, y);
    }
    
    public Point[] getRay(int d) {
        Point[] point = new Point[2];
        point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize, playerPosition.y + playerSize/2
        if (d > 4) {
            System.out.print("6");
        }
        switch(d) {
            case 0:
            point[1] = new Point(0, -1);
            return point;
            case 1:
            point[1] = new Point(1, -1);
            return point;
            case 2:
            point[1] = new Point(1, 0);
            return point;
            case 3:
            point[1] = new Point(1, 1);
            return point;
            case 4:
            point[1] = new Point(0, 1);
            return point;
            case 5:
            point[1] = new Point(-1, 1);
            return point;
            case 6:
            point[1] = new Point(-1, 0);
            return point;
            case 7:
            point[1] = new Point(-1, -1);
            return point;
            default:
            System.out.println("Error passes in improper state: " + d + " : - getRay");
            break;
        }
        return point;
    }
    
    public Point move(Point p, int[] polygonX, int[] polygonY) {
        if (p == null) {
            return new Point(0 ,0);
        }
        return move(p.x, p.y, polygonX, polygonY);
    }
    
    /**
    Moves object with x and y values ONLY use -1 0 1
    @param x x value to move
    @param y y value to move
    */
    public Point move(int x, int y, int[] polygonX, int[] polygonY) {
        oldDirection = direction;
        direction = (((int)Math.toDegrees(Math.atan2(((y + playerPosition.y)-playerPosition.y),((x + playerPosition.x)-playerPosition.x))/45)) + 2);
        direction = (direction < 0) ? direction + 8 : direction;
        if (directionGraph.size() >= UserInterface.AXES_LENGTH) {
            directionGraph.remove(0);
            directionGraph.add(Double.valueOf(direction));
        } else {
            directionGraph.add(Double.valueOf(direction));
        }
        if (getColliding(playerPosition.x + x + playerSize/2, playerPosition.y + y + playerSize/2, polygonX, polygonY)) {
            die();
            return playerStart;
        }
        else {
            playerPosition.translate(x, y);
        }
        return new Point(playerPosition.x, playerPosition.y);
    }
    
    public void reset() {
        playerPosition = playerStart;
        direction = 0;
        directionGraph = new ArrayList<>();
    }
    
    private void die() {
        reset();
    }
    
    public List<Double> getDirectionGraph() {
        return directionGraph;
    }
    
    public int getPlayerSize() {
        return playerSize;
    }
}
