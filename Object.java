import java.awt.Point;
import java.util.ArrayList;
import java.awt.*;

public class Object {
    
    // data
    private boolean debug = true;
    private int playerSize = 10;
    private int playerX = 100;
    private int playerY = 150;
    private Point playerStart = new Point(100, 150);
    private int rayLength = 100;
    private int direction = 0;
    private int oldDirection = 0;
    private ArrayList<Double> directionGraph = new ArrayList<Double>(); 
    
    public Object() { }

    public Object(int StartX, int StartY) {
        playerStart = new Point(StartX, StartY);
        playerX = StartX;
        playerY = StartY;
    }

    /**
    Gets forward directions of object 0-5
    @return the distances to objects of the network 0-5
    */
    public double[][] getDirections(int[] polygonX, int[] polygonY, NeuralNetwork nn, Graphics g) {
        double[][] result = new double[nn.getOutNodes()][1];
        int currentDirection = direction;
        for (int i = -2; i <= 2; i++) {
            currentDirection = direction;
            currentDirection += i;
            if (currentDirection < 0) {
                currentDirection += 8;
            }
            if (currentDirection > 7) {
                currentDirection -= 8;
            }
            result[i + 2][0] = getColissionDistance(currentDirection, polygonX, polygonY, g);
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
        
        System.out.print("Currend Dir: " + currentDirection + " :Dir: " + direction + " :Old: " + oldDirection + " :Result(Current - Old): ");
        System.out.println(Math.abs(currentDirection-oldDirection));
        
        if (Math.abs(currentDirection-oldDirection)>=2) {
            return direction;
        }
        return currentDirection;
    }
    
    private Point getColissionPoint(int d, boolean distance, int[] polygonX, int[] polygonY, Graphics g) {
        Point[] ray = getRay(d);
        Point point = ray[0]; //getRay(d)[0];
        Point value = ray[1]; //getRay(d)[1];
        double x = point.x;
        double y = point.y;
        int i = 0;
        double length = rayLength;//(d % 2 == 0) ? rayLength : rayLength / Math.sqrt(2);
        do {
            x += (double)value.x * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.x
            y += (double)value.y * ((d % 2 == 0) ? 1 : Math.sqrt(2)/2); // old: +=value.y
            i++;
            if (g != null && debug) {
                g.setColor(Color.RED);
                g.drawLine((int)x, (int)y, (int)x, (int)y);
            }
        }
        while(!getColiding((int)x, (int)y, polygonX, polygonY) && i < length);
        return (distance) ? new Point(i, i) : new Point(Math.abs((int)x - point.x), Math.abs((int)y - point.y));
    }
    
    private boolean getColiding(int x, int y, int[] polygonX, int[] polygonY) {
        return new Polygon(polygonX, polygonY, polygonX.length).contains(x, y);
    }
    
    private int getColissionDistance(int d, int[] polygonX, int[] polygonY, Graphics g) {
        return getColissionPoint(d, true, polygonX, polygonY, g).x;
    }
    
    public Point[] getRay(int d) {
        Point[] point = new Point[2];
        switch(d) {
            case 2:
            point[0] = new Point(playerX, playerY); //playerX + playerSize, playerY + playerSize/2
            point[1] = new Point(1, 0);
            return point;
            case 6:
            point[0] = new Point(playerX, playerY); //playerX, playerY + playerSize/2
            point[1] = new Point(-1, 0);
            return point;
            case 1:
            point[0] = new Point(playerX, playerY); //playerX + playerSize, playerY
            point[1] = new Point(1, -1);
            return point;
            case 7:
            point[0] = new Point(playerX, playerY); //playerX, playerY
            point[1] = new Point(-1, -1);
            return point;
            case 0:
            point[0] = new Point(playerX, playerY); //playerX + playerSize/2, playerY
            point[1] = new Point(0, -1);
            return point;
            case 3:
            point[0] = new Point(playerX, playerY); //playerX + playerSize, playerY + playerSize
            point[1] = new Point(1, 1);
            return point;
            case 5:
            point[0] = new Point(playerX, playerY); //playerX, playerY + playerSize
            point[1] = new Point(-1, 1);
            return point;
            case 4:
            point[0] = new Point(playerX, playerY); //playerX + playerSize/2, playerY + playerSize
            point[1] = new Point(0, 1);
            return point;
            default:
            System.out.println("Error passes in improper state: " + d + " : - getRay");
            break;
        }
        return point;
    }
    
    public Point Move(Point p, int[] polygonX, int[] polygonY) {
        if (p == null) {
            return new Point(0 ,0);
        }
        return Move(p.x, p.y, polygonX, polygonY);
    }
    
    /**
    Moves object with x and y values ONLY use -1 0 1
    @param x x value to move
    @param y y value to move
    */
    public Point Move(int x, int y, int[] polygonX, int[] polygonY) {
        oldDirection = direction;
        direction = (((int)Math.toDegrees(Math.atan2(((y + playerY)-playerY),((x + playerX)-playerX))/45)) + 2);
        direction = (direction < 0) ? direction + 8 : direction;
        if (directionGraph.size() >= MachineLearning.axesLength) {
            directionGraph.remove(0);
            directionGraph.add(Double.valueOf(direction));
        } else {
            directionGraph.add(Double.valueOf(direction));
        }
        if (getColiding(playerX + x + playerSize/2, playerY + y + playerSize/2, polygonX, polygonY)) {
            Die();
            return new Point(0, 0);
        }
        else {
            playerX += x; 
            playerY += y;
        }
        return new Point(playerX, playerY);
    }
    
    public void Reset() {
        playerX = playerStart.x;
        playerY = playerStart.y;
        direction = 0;
        directionGraph = new ArrayList<Double>();
    }
    
    private void Die() {
        Reset();
        //TODO
    }
    
    public Point getPlayerPos() {
        return new Point(playerX, playerY);
    }
    
    public ArrayList<Double> getDirectionGraph() {
        return directionGraph;
    }
    
    public int getPlayerSize() {
        return playerSize;
    }
}
