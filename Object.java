import java.awt.Point;
import java.awt.*;

public class Object {
    
    // data
    public int playerSize = 10;
    public int playerX = 100;
    public int playerY = 150;
    int rayLength = 100;
    int direction = 0;
    
    /**
    Gets forward directions of object 0-5
    @return the distances to objects of the network 0-5
    */
    public double[][] getDirection(int[] polygonX, int[] polygonY) {
        double[][] result = new double[5][1];
        int currentDistance = direction;
        for (int i = -2; i <= 2; i++) {
            if (currentDistance + i < 0) {
                currentDistance += 7;
            }
            if (currentDistance + i > 7) {
                currentDistance -= 7;
            }
            result[i + 2][0] = getColissionDistance(currentDistance + i, polygonX, polygonY);
        }
        return result;
    }
    
    public double getColissionDistance(int d, int[] polygonX, int[] polygonY) {
        double result = 0.0;
        result = Double.valueOf(Math.sqrt(Math.pow(getColission(d, false, polygonX, polygonY).x, 2) + Math.pow(getColission(d, false, polygonX, polygonY).y, 2)));
        return result;
    }
    
    public Point getColission(int d, boolean pos, int[] polygonX, int[] polygonY) {
        Point point = getRay(d)[0];
        Point value = getRay(d)[1];
        int x = point.x;
        int y = point.y;
        int i = 0;
        double length = (d % 2 == 0) ? rayLength : rayLength/Math.sqrt(2);
        do {
            x += value.x;
            y += value.y;
            i++;
        }
        while(!getColiding(x, y, polygonX, polygonY) && i < length);
        return (pos) ? new Point(x, y) : new Point(Math.abs(x - point.x), Math.abs(y - point.y));
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
        playerY = 150;
        direction = 0;
    }
    
    private void Die() {
        Reset();
        //TODO
    }
}
