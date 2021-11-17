import java.awt.Point;
import java.awt.*;

public class Object {
    
    // data
    public int playerSize = 10;
    public int playerX = 100;
    public int playerY = 150; // old 150
    int rayLength = 100;
    int direction = 0;
    
    public void drawRays(Graphics g, int[] polygonX, int[] polygonY) {
        int x;
        int y;
        int currentDirection = direction;
        double[] distance = new double[getDirection(polygonX, polygonY).length];
        Point[][] ray = new Point[distance.length][2];
        double[] rayNum = new double[distance.length];
        for (int i = -2; i <= 2; i++) {
            currentDirection = direction;
            currentDirection += i;
            if (currentDirection < 0) {
                currentDirection += 8;
            }
            if (currentDirection > 7) {
                currentDirection -= 8;
            }
            ray[i + 2] = getRay(currentDirection);
            rayNum[i + 2] = currentDirection;
            distance[i + 2] = getColission(currentDirection, true, polygonX, polygonY, g).x; 
        }
        
        for (int i = 0; i < ray.length; i++) {
            x = (int)(playerX + (distance[i] * ray[i][1].x));
            y = (int)(playerY + (distance[i] * ray[i][1].y));
            g.setColor(Color.RED);
            g.drawLine(playerX, playerY, x, y);
        }
        
    }

    /**
    Gets forward directions of object 0-5
    @return the distances to objects of the network 0-5
    */
    public double[][] getDirection(int[] polygonX, int[] polygonY) {
        double[][] result = new double[5][1];
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
            result[i + 2][0] = getColissionDistance(currentDirection, polygonX, polygonY);
        }
        return result;
    }
    
    public Point getColission(int d, boolean distance, int[] polygonX, int[] polygonY, Graphics g) {
        Point[] ray = getRay(d);
        Point point = ray[0]; //getRay(d)[0];
        Point value = ray[1]; //getRay(d)[1];
        double x = point.x;
        double y = point.y;
        int i = 0;
        double length = rayLength;//(d % 2 == 0) ? rayLength : rayLength / Math.sqrt(2);
        do {
            x += (double)value.x * ((d % 2 == 0) ? 1 : Math.sqrt(2)); // old: +=value.x
            y += (double)value.y * ((d % 2 == 0) ? 1 : Math.sqrt(2)); // old: +=value.y
            i++;
            if (g != null) {
                g.drawLine((int)x, (int)y, (int)x, (int)y);
            }
        }
        while(!getColiding((int)x, (int)y, polygonX, polygonY) && i < length);
        return (distance) ? new Point(i, i) : new Point(Math.abs((int)x - point.x), Math.abs((int)y - point.y));
    }
    
    public int getColissionDistance(int d, int[] polygonX, int[] polygonY) {
        //int result = 0;
        //result = (int)(Math.sqrt(Math.pow(getColission(d, false, polygonX, polygonY, null).x, 2) + Math.pow(getColission(d, false, polygonX, polygonY, null).y, 2)));
        //return result;
        return getColission(d, true, polygonX, polygonY, null).x;
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
    
    public boolean getColiding(int x, int y, int[] polygonX, int[] polygonY) {
        return new Polygon(polygonX, polygonY, polygonX.length).contains(x, y);
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
        direction = (((int)Math.toDegrees(Math.atan2(((y + playerY)-playerY),((x + playerX)-playerX))/45)) + 2);
        direction = (direction < 0) ? direction + 8 : direction;
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
        playerX = 100;
        playerY = 150; //old 150
        direction = 0;
    }
    
    private void Die() {
        Reset();
        //TODO
    }
}
