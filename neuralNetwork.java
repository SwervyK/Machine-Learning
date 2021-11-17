import java.util.Arrays;
import java.util.Random;


public class NeuralNetwork {
    //TODO remove hardcoded array lengths
    // data
    double totalError = 0.0;
    int numDirections = 7;
    public int seed;
    Random random = new Random();
    double learninRate = 0.5;

    /*
    double[][] x = new double[2][1];
    double[][] w1 = {{0.15,0.20},{0.25,0.30}};
    double[][] b1 = {{0.35},{0.35}};
    double[][] hidden = new double[2][1];
    double[][] b2 = {{0.60},{0.60}};
    double[][] w2 = {{0.40,0.45,},{0.50,0.55}};
    double[][] out = new double[2][1];
    */
    // neural network 
    double[][] x = new double[5][1];
    double[][] w1 = new double[3][5];
    double[][] b1 = new double[3][1];
    double[][] hidden = new double[3][1];
    double[][] b2 = new double[5][1];
    double[][] w2 = new double[5][3];
    double[][] out = new double[5][1];

    double[] w = new double[(w1.length * w1[0].length) + (w2.length * w2[0].length)];
    double[] b = new double[(b1.length * b1[0].length) + (b2.length * b2[0].length)];
    double[] error = new double[out.length];
    double[] answer = new double[out.length];
    
    public NeuralNetwork(int s) { seed = s; }
    
    public NeuralNetwork(int xNodes, int hiddenNodes, int outNodes, int s) {
        x = new double[xNodes][1];
        w1 = new double[hiddenNodes][xNodes];
        b1 = new double[hiddenNodes][1];
        hidden = new double[hiddenNodes][1];
        b2 = new double[outNodes][hiddenNodes];
        w2 = new double[outNodes][1];
        out = new double[outNodes][1];
        s = seed;
    }
    
    /**
    Randomises all w and b values in the network
    */
    public void randomiseNetwork() {
        randomiseMatrices(w1);
        randomiseMatrices(b1);
        randomiseMatrices(w2);
        randomiseMatrices(b2);
    }
    
    /**
    Updates ai
    @param inputs: values to passinto the network
    @return the direction the network wants to go in 0-5
    */
    public int aiUpdate(double[][] inputs) {
        double[][] answer = calculateMatrices(inputs);
        int chosenDirection = calculateOut(answer);
        return chosenDirection;
    }

    public void ai(double[][] inputs) {
        double[][] output = calculateMatrices(inputs);
        System.out.println("Output");
        print2D(output);
        System.out.println("/Output");
        double[] newAnswer = {0.01,0.99};
        answer = newAnswer;
        aiLearn();
        System.out.println("Answer");
        for (int i = 0; i < w.length; i++) {
            System.out.print("[");
            System.out.print(w()[i]);
            System.out.println("]");
        }
        System.out.println("/Answer");
    }
    
    /**
    Changes the ai's values to make the prediction more accurate
    */
    public void aiLearn() {
        int outLayer1 = a2f().length;
        int outLayer2 = hidden.length;
        double[][] EtotalYFinal = new double[outLayer1][outLayer2]; 
        double[][] YFinalY = new double[outLayer1][outLayer2]; 
        double[][] YW = new double[outLayer1][outLayer2]; 
        double[][] EtotalW = new double[outLayer1][outLayer2]; 
        
        for (int i = 0; i < hidden.length; i++) {
            for (int j = 0; j < a2f().length; j++) {
                EtotalYFinal[j][i] = -(answer[j] - a2f()[j][0]);
                YFinalY[j][i] = a2f()[j][0] * (1 - a2f()[j][0]); //(8)
                YW[j][i] = a1f()[i][0];
                EtotalW[j][i] = EtotalYFinal[j][i] * YFinalY[j][i] * YW[j][i];
                w2[j][i] = w2[j][i] - learninRate * EtotalW[j][i];
            }
        }
        //hidden layer
        int hiddenLayer1 = hidden.length;
        int hiddenLayer2 = x.length;
        double[][] EY = new double[hiddenLayer1][hiddenLayer2];
        double[][] EHF = new double[hiddenLayer1][hiddenLayer2];
        double[][] EtotalHFinal = new double[hiddenLayer1][hiddenLayer2]; 
        double[][] HFinalH = new double[hiddenLayer1][hiddenLayer2];
        double[][] HW = new double[hiddenLayer1][hiddenLayer2];
        double[][] EtotalWH = new double[hiddenLayer1][hiddenLayer2];
        
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < hidden.length; j++) {
                EY[j][i] = -(answer[i] - a2f()[i][0]) * a2f()[i][0] * (1 - a2f()[i][0]);
                EHF[j][i] = EY[j][i] * w1[j][i];
                EtotalHFinal[j][i] += EHF[j][i];
                HFinalH[j][i] = a1f()[j][0] * (1 - a1f()[j][0]);
                HW[j][i] = x[i][0];
                EtotalWH[j][i] = EtotalHFinal[j][i] * HFinalH[j][i] * HW[j][i];
                w1[j][i] = w1[j][i] - learninRate * EtotalWH[j][i];
            }
        }
        /*
        double[][] EtotalYFinal = new double[hidden.length][a2f().length]; 
        double[][] YFinalY = new double[hidden.length][a2f().length];
        double[][] YW = new double[hidden.length][a2f().length];
        double[][] EtotalW = new double[hidden.length][a2f().length];
        
        for (int i = 0; i < hidden.length; i++) {
            for (int j = 0; j < a2f().length; j++) {
                EtotalYFinal[i][j] = -(answer[j] - a2f()[j][0]);
                YFinalY[i][j] = a2f()[j][0] * (1 - a2f()[j][0]); //(8)
                YW[i][j] = a1f()[i][0];
                EtotalW[i][j] = EtotalYFinal[i][j] * YFinalY[i][j] * YW[i][j];
                w2[j][i] = w2[j][i] - learninRate * EtotalW[i][j];
            }
        }
        
        //hidden layer
        double[][] EY = new double[x.length][hidden.length];
        double[][] EHF = new double[x.length][hidden.length]; 
        double[][] EtotalHFinal = new double[x.length][hidden.length]; 
        double[][] HFinalH = new double[x.length][hidden.length]; 
        double[][] HW = new double[x.length][hidden.length]; 
        double[][] EtotalWH = new double[x.length][hidden.length];
        
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < hidden.length; j++) {
                EY[i][j] = -(answer[i] - a2f()[i][0]) * a2f()[i][0] * (1 - a2f()[i][0]);
                EHF[i][j] = EY[i][j] * w1[j][i];
                EtotalHFinal[i][j] += EHF[i][j];
                HFinalH[i][j] = a1f()[j][0] * (1 - a1f()[j][0]);
                HW[i][j] = x[i][0];
                EtotalWH[i][j] = EtotalHFinal[i][j] * HFinalH[i][j] * HW[i][j];
                w1[j][i] = w1[j][i] - learninRate * EtotalWH[i][j];
            }
        }
        */
    }
    
    // create w array
    public double[] w() {
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
        return w;
    }
    
    // create b array
    public double[] b() {
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
        return b;
    }
    
    // 1st laver z vales
    public double[][] z1() {
        double[][] result = multiplyMatrices(w1, x);
        return result;
    }
    
    // 1st laver a vales
    public double[][] a1f() {
        double[][] result = multiplyMatrices(w1, x);
        result = f(addMatrices(result, b1));
        return result;
    }
    
    // 2nd laver z vales
    public double[][] z2() {
        double[][] result = multiplyMatrices(w2, z1());
        return result;
    }
    
    // 2nd laver a vales
    public double[][] a2f() {
        double[][] result = multiplyMatrices(w2, a1f());
        result = f(addMatrices(result, b2));
        return result;
    }
    
    // mutilpy matrices
    private double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }
        
        return result;
    }
    
    private double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }
    
    // add matrices
    private double[][] addMatrices(double[][] firstMatrix, double[][] secondMatrix)
    {
        double result[][] = new double[secondMatrix.length][secondMatrix[0].length];
        
        for (int row = 0; row < secondMatrix.length; row++)
        for (int col = 0; col < secondMatrix[0].length; col++)
        result[row][col] = firstMatrix[row][col] + secondMatrix[row][col];
        
        return result;
    }
    
    // find the hidden layer values, the output values, and the total error
    private double[][] calculateMatrices(double[][] input) {
        double result[][] = new double[a2f().length][a2f()[0].length];
        x = input;
        hidden = a1f();
        out = result = a2f();
        for (int i = 0; i < a2f().length; i++) {
            answer = getAnswer(input);
            totalError += error[i] = Math.pow((a2f()[i][0] - answer[i]), 2);
        }
        totalError /= a2f().length;
        return result;
    }
    
    // finds longest input
    private int calculateOut(double[][] in) {
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
        if (result > numDirections) {
            result -= numDirections;
        }
        if (result < 0) {
            result += numDirections;
        }
        return result;
    }
    
    // returns array with error for each end node
    private double[] getAnswer(double[][] directions) {
        double[] result = new double[out.length];
        int index = 0;
        double[][] values = directions;
        double old = 0.0;
        int i = 0;
        for (double value[] : values) {
            if (value[0] > old) {
                index = i;
                old = value[0];
            }
            i++;
        }
        for (int j = 0; j < result.length; j++) {
            if (j == index) {
                result[j] = 1;
            }
            else {
                result[j] = 0;
            }
        }
        return result;
    }
    
    /**
    Prints a 2d array
    @param mat: 2d array to print
    */
    public static void print2D(double[][] mat)
    {
        for (double[] row : mat)
        System.out.println(Arrays.toString(row));
    }
    
    // randomises a 2d array
    public void randomiseMatrices(double[][] in) {
        random.setSeed(seed);
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                in[row][col] = random.nextDouble();//Math.random();
            }
        }
    }
    
    // sigmoid function
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
