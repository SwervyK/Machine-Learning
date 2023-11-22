package machine.learning;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Box {
    
    // data
    private boolean debug = true;
    private int playerSize = 10;
    // private int playerPosition.x = 100;
    // private int playerPosition.y = 150;
    private Point playerStart = new Point(100, 150);
    private Point playerPosition = new Point(100, 150);
    private int rayLength = 100;
    private int direction = 0;
    private int oldDirection = 0;
    private ArrayList<Double> directionGraph = new ArrayList<>(); 
    
    public Box() { }
    
    public Box(int startX, int startY) {
        playerStart = new Point(startX, startY);
        playerPosition = playerStart;
    }
    
    /**
    Gets forward directions of object 0-5
    @return the distances to objects of the network 0-5
    */
    public double[][] getDirections(int[] polygonX, int[] polygonY, NeuralNetwork nn) {
        double[][] result = new double[nn.getOutNodes()][1];
        int currentDirection;
        for (int i = -2; i <= 2; i++) {
            currentDirection = direction;
            currentDirection += i;
            if (currentDirection < 0) {
                currentDirection += 8;
            }
            if (currentDirection > 7) {
                currentDirection -= 8;
            }
            result[i + 2][0] = getCollisionDistance(currentDirection, polygonX, polygonY);
        }
        return result;
    }
    
    public int getDirection(int d) {
        int currentDirection = direction + d;
        if (currentDirection < 0) {
            currentDirection += 8;
        }
        if (currentDirection > 7) {
            currentDirection -= 8;
        }
        
        //System.out.print("Current Dir: " + currentDirection + " :Dir: " + direction + " :Old: " + oldDirection + " :Result(Current - Old): ");
        //System.out.println(Math.abs(currentDirection-oldDirection));
        
        if (Math.abs(currentDirection-oldDirection)>=2) {
            return direction;
        }
        return currentDirection;
    }
    
    private Point getCollisionPoint(int d, boolean distance, int[] polygonX, int[] polygonY) {
        Point[] ray = getRay(d);
        Point point = ray[0]; //getRay(d)[0];
        Point value = ray[1]; //getRay(d)[1];
        double x = point.x;
        double y = point.y;
        int i = 0;
        double length = rayLength;//(d % 2 == 0) ? rayLength : rayLength / Math.sqrt(2);
        do {
            x += value.x * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.x
            y += value.y * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.y
            i++;
            if (debug) {
                UserInterface.debugLines((int)x, (int)y);
            }
        }
        while(!getColliding((int)x, (int)y, polygonX, polygonY) && i < length);
        return (distance) ? new Point(i, i) : new Point(Math.abs((int)x - point.x), Math.abs((int)y - point.y));
    }
    
    private boolean getColliding(int x, int y, int[] polygonX, int[] polygonY) {
        return new Polygon(polygonX, polygonY, polygonX.length).contains(x, y);
    }
    
    private int getCollisionDistance(int d, int[] polygonX, int[] polygonY) {
        return getCollisionPoint(d, true, polygonX, polygonY).x;
    }
    
    public Point[] getRay(int d) {
        Point[] point = new Point[2];
        switch(d) {
            case 2:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize, playerPosition.y + playerSize/2
            point[1] = new Point(1, 0);
            return point;
            case 6:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x, playerPosition.y + playerSize/2
            point[1] = new Point(-1, 0);
            return point;
            case 1:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize, playerPosition.y
            point[1] = new Point(1, -1);
            return point;
            case 7:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x, playerPosition.y
            point[1] = new Point(-1, -1);
            return point;
            case 0:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize/2, playerPosition.y
            point[1] = new Point(0, -1);
            return point;
            case 3:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize, playerPosition.y + playerSize
            point[1] = new Point(1, 1);
            return point;
            case 5:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x, playerPosition.y + playerSize
            point[1] = new Point(-1, 1);
            return point;
            case 4:
            point[0] = new Point(playerPosition.x, playerPosition.y); //playerPosition.x + playerSize/2, playerPosition.y + playerSize
            point[1] = new Point(0, 1);
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
            return new Point(0, 0);
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
        //TODO
    }
    
    public Point getPlayerPos() {
        return new Point(playerPosition.x, playerPosition.y);
    }
    
    public List<Double> getDirectionGraph() {
        for (int i = 0; i < directionGraph.size(); i++) {
            System.out.println(directionGraph.get(i));
        }
        return directionGraph;
    }
    
    public int getPlayerSize() {
        return playerSize;
    }
}
