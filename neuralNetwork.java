import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class NeuralNetwork {
    //TODO remove hardcoded array lengths
    // data
    double totalError = 0.0;
    int numDirections = 7;
    public int seed;
    Random random = new Random();
    double learninRate = 0.5;
    public int kNumXNodes = 5;
    public int kNumHiddenNodes = 3;
    public int kNumOutNodes = 5;
    public int numItterations = 5;
    public int currentItteration = 0;
    
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
    ArrayList<Double> errorGraph = new ArrayList<Double>();
    double[] answer = new double[out.length];
    
    public NeuralNetwork(int s) { seed = s; }
    
    public NeuralNetwork(int xNodes, int hiddenNodes, int outNodes, int s) {
        kNumXNodes = xNodes;
        kNumHiddenNodes = hiddenNodes;
        kNumOutNodes = outNodes;
        x = new double[xNodes][1];
        w1 = new double[hiddenNodes][xNodes];
        b1 = new double[hiddenNodes][1];
        hidden = new double[hiddenNodes][1];
        b2 = new double[outNodes][1];
        w2 = new double[outNodes][hiddenNodes];
        out = new double[outNodes][1];
        
        w = new double[(w1.length * w1[0].length) + (w2.length * w2[0].length)];
        b = new double[(b1.length * b1[0].length) + (b2.length * b2[0].length)];
        error = new double[out.length];
        answer = new double[out.length];
        
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
    //TODO aireset()
    /**
    Updates ai
    @param inputs: values to passinto the network
    @return the direction the network wants to go in 0-5
    */
    public int aiUpdate(double[][] inputs, Object o) {
        double[][] answer = getOppisiteAnswer(inputs);//getAnswer(inputs);//calculateMatrices(inputs);
        int chosenDirection = calculateOppisiteOut(answer, o);//calculateOut(answer, o); //OLD (answer)
        //System.out.println(chosenDirection);
        /*
        if (errorGraph.size() >= runner.axesLength) {
            errorGraph.remove(0);
            errorGraph.add(totalError/(currentItteration + 1));
        } else {
            errorGraph.add(totalError/(currentItteration + 1));
        }*/
        if (currentItteration >= numItterations) {
            totalError /= numItterations;
            if (errorGraph.size() >= runner.axesLength) {
                errorGraph.remove(0);
                errorGraph.add(totalError);
            } else {
                errorGraph.add(totalError);
            }
            aiLearn();
            currentItteration = 0;
        }
        currentItteration++;
        return chosenDirection;
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
            answer = getAnswer(input)[0]; //TODO NOW REMOVE THE INDEX OF THIS
            totalError += error[i] = Math.pow((a2f()[i][0] - answer[i]), 2);
        }
        totalError /= a2f().length;
        return result;
    }
    
    // finds longest input
    private int calculateOut(double[][] in, Object o) {
        int result = 0;
        double oldValue = 0.0, value = 0.0;
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                value = in[row][col];
                if (value > oldValue) {
                    result =  row;
                }
                else {
                    value = oldValue;
                }
                oldValue = value;
            }
        }
        result -= 2;
        result = o.getDirection(result);
        return result;
    }

    private int calculateOppisiteOut(double[][] in, Object o) {
        int result = 0;
        double oldValue = 0.0, value = 0.0;
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                value = in[row][col];
                if (value > oldValue) {
                    result =  row;
                }
                else {
                    value = oldValue;
                }
                oldValue = value;
            }
        }
        result -= 2;
        result = o.getDirection(result);
        return result;
    }
    
    // returns array with error for each end node
    private double[][] getAnswer(double[][] directions) { //good
        double[][] result = new double[out.length][1];
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
                result[j][0] = 1;
            }
            else {
                result[j][0] = 0;
            }
        }
        return result;
    }

    public double[][] getOppisiteAnswer(double[][] directions) {
        double[][] result = new double[out.length][1];
        int index = 0;
        double[][] values = directions;
        double old = 101.0;
        int i = 0;
        for (double value[] : values) {
            if (value[0] < old) {
                index = i;
                old = value[0];
            }
            i++;
        }
        for (int j = 0; j < result.length; j++) {
            if (j == index) {
                result[j][0] = 1;
            }
            else {
                result[j][0] = 0;
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
    
    public void Reset() {
        x = new double[kNumXNodes][1];
        w1 = new double[kNumHiddenNodes][kNumXNodes];
        b1 = new double[kNumHiddenNodes][1];
        hidden = new double[kNumHiddenNodes][1];
        b2 = new double[kNumOutNodes][1];
        w2 = new double[kNumOutNodes][kNumHiddenNodes];
        out = new double[kNumOutNodes][1];
        
        w = new double[(w1.length * w1[0].length) + (w2.length * w2[0].length)];
        b = new double[(b1.length * b1[0].length) + (b2.length * b2[0].length)];
        error = new double[out.length];
        answer = new double[out.length];
        errorGraph = new ArrayList<Double>();
        
        currentItteration = 0;
        totalError = 0.0;
    }
}
