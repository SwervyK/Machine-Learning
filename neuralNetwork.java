import java.awt.Point;
import java.util.Arrays;


public class neuralNetwork {

    // data
    static Point playerMove;
    static int direction = 0;
    static double totalError = 0.0;
    
    // neural network
    static double[][] x = new double[5][1];
    static double[][] w1 = new double[3][5];
    static double[][] b1 = new double[3][1];
    static double[][] hidden = new double[3][1];
    static double[][] b2 = new double[5][1];
    static double[][] w2 = new double[5][3];
    static double[][] out = new double[5][1];
    static double[] w = new double[(w1.length * w1[0].length) + (w2.length * w2[0].length)];
    static double[] b = new double[(b1.length * b1[0].length) + (b2.length * b2[0].length)];
    static double[] error = new double[out.length];

    public void randomiseNetwork() {
        randomiseMatrices(w1);
        randomiseMatrices(b1);
        randomiseMatrices(hidden);
        randomiseMatrices(w2);
        randomiseMatrices(b2);
    }
    
    public int aiUpdate(double[][] inputs) {
        double[][] answer = calculateMatrices(inputs);
        int chosenDirection = calculateOut(answer);
        return chosenDirection;
    }
    
    public void aiLearn() {
        double[] cw2 = new double[w2.length * w2[0].length];
        double[] cb2 = new double[w2.length * w2[0].length];
        
        for (int i = 0; i < w.length; i++) {
            cw2[i] = z1()[i][0] * z2()[i][0] * 2*(z2()[i][0] - out[i][0]);
        }
        
        for (int i = 0; i < b.length; i++) {
            cb2[i] = 1 * z2()[i][0] * 2*(z2()[i][0] - out[i][0]);
        }
        
        double[] cw = new double[w1.length * w1[0].length];
        double[] cb = new double[w1.length * w1[0].length];
        
        for (int i = 0; i < w.length; i++) {
            cw[i] = x[i][0] * z1()[i][0] * 2*(z1()[i][0] - z2()[i][0]);
        }
        
        for (int i = 0; i < b.length; i++) {
            cb[i] = 1 * z1()[i][0] * 2*(z1()[i][0] - z2()[i][0]);
        }
    }
    
    public void w() {
        int index = 0;
        for (int i = 0; i < w1.length; i++) {
            for (int j = 0; j < w1[0].length; j++) {
                w[index] = w1[i][j];
                index++;
            }
        }
        for (int i = 0; i < w2.length; i++) {
            for (int j = 0; j < w2[0].length; j++) {
                w[index] = w2[i][j];
                index++;
            }
        }
    }
    
    public void b() {
        int index = 0;
        for (int i = 0; i < b1.length; i++) {
            for (int j = 0; j < b1[0].length; j++) {
                b[index] = b1[i][j];
                index++;
            }
        }
        for (int i = 0; i < b2.length; i++) {
            for (int j = 0; j < b2[0].length; j++) {
                b[index] = b2[i][j];
                index++;
            }
        }
    }
    
    public double[][] z1() {
        double[][] result = multiplyMatrices(w1, x);
        return result;
    }
    
    public double[][] a1f() {
        double[][] result = multiplyMatrices(w1, x);
        result = f(addMatrices(result, b1));
        return result;
    }
    
    public double[][] z2() {
        double[][] result = multiplyMatrices(w2, z1());
        return result;
    }
    
    public double[][] a2f() {
        double[][] result = multiplyMatrices(w2, a1f());
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
        double result[][] = new double[5][1];
        x = input;
        hidden = a1f();
        out = result = a2f();
        for (int i = 0; i < a2f().length; i++) {
            totalError += error[i] = Math.pow((a2f()[i][0] - getAnswer()[i]), 2);
        }
        totalError /= a2f().length;
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
        result -= 2;
        if (result > 7) {
            result -= 7;
        }
        if (result < 0) {
            result += 7;
        }
        return result;
    }
    
    public double[][] getInputs() {
        return getDirection();
    }
    
    public double[] getAnswer() {
        double[] result = new double[out.length];
        int index = 0;
        double[][] values = getDirection();
        double old = 0.0;
        int i = 0;
        for (double value[] : values) {
            if (value[0] > old) {
                index = i;
            }
            old = value[0];
            i++;
        }
        for (int j = 0; j < result.length; j++) {
            if (j == index) {
                result[j] = 1;
            }
        }
        return result;
    }
    
    public double[][] getDirection() {
        double[][] result = new double[5][1];
        int currentDistance = direction;
        for (int i = -2; i <= 2; i++) {
            if (currentDistance + i < 0) {
                currentDistance += 7;
            }
            if (currentDistance + i > 7) {
                currentDistance -= 7;
            }
            //result[i + 2][0] = getColissionDistance(currentDistance + i);
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
}
