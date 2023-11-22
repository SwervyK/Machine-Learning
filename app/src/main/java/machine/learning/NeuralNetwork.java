package machine.learning;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class NeuralNetwork {
    //TODO remove hardcoded array lengths
    // data
    private double totalError = 0.0;
    private int seed;
    private Random random = new Random();
    private double learningRate = 0.5;
    private int kNumXNodes = 5;
    private int kNumHiddenNodes = 3;
    private int kNumOutNodes = 5;
    // private int numIterations = 5;
    // private int currentIteration = 0;
    private int threshold = 20;
    
    // neural network 
    double[][] in = new double[5][1];
    double[][] w1 = new double[3][5];
    double[][] b1 = new double[3][1];
    double[][] hidden = new double[3][1];
    double[][] b2 = new double[5][1];
    double[][] w2 = new double[5][3];
    double[][] out = new double[5][1];
    
    double[] w = new double[(w1.length * w1[0].length) + (w2.length * w2[0].length)];
    double[] b = new double[(b1.length * b1[0].length) + (b2.length * b2[0].length)];
    double[] error = new double[out.length];
    ArrayList<Double> errorGraph = new ArrayList<>();
    double[] answer = new double[out.length];
    
    public NeuralNetwork(int s) { seed = s; }
    
    public NeuralNetwork(int xNodes, int hiddenNodes, int outNodes, int s) {
        kNumXNodes = xNodes;
        kNumHiddenNodes = hiddenNodes;
        kNumOutNodes = outNodes;
        in = new double[xNodes][1];
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
        
        seed = s;
    }
    
    /**
    Randomizes all w and b values in the network
    */
    public void randomizeNetwork() {
        randomizeMatrices(w1);
        randomizeMatrices(b1);
        randomizeMatrices(w2);
        randomizeMatrices(b2);
    }
    //TODO aiReset()
    /**
    Updates ai
    @param inputs: values to passings the network
    @return the direction the network wants to go in 0-5
    **/
    public int aiUpdate(double[][] inputs, boolean wantAi, Box o) {
        double[][] aiAnswer;
        if (wantAi) {
            aiAnswer = calculateMatrices(inputs);
        }
        else {
            aiAnswer = getBetterAnswer(inputs);
        }
        int chosenDirection = findDirection(aiAnswer, o);
        if (errorGraph.size() >= UserInterface.AXES_LENGTH) {
            errorGraph.remove(0);
            errorGraph.add(totalError); //old errorGraph.add(totalError/(currentIteration + 1));
        } else {
            errorGraph.add(totalError); // old errorGraph.add(totalError/(currentIteration + 1));
        }
        if (errorGraph.size() >= UserInterface.AXES_LENGTH) {
            errorGraph.remove(0);
            errorGraph.add(totalError);
        } else {
            errorGraph.add(totalError);
        }
        /*
        if (currentIteration >= numIterations) {
            totalError /= numIterations;
            if (errorGraph.size() >= MachineLearning.axesLength) {
                errorGraph.remove(0);
                errorGraph.add(totalError);
            } else {
                errorGraph.add(totalError);
            }
            aiLearn();
            currentIteration = 0;
        }
        currentIteration++;
        */
        aiLearn();
        return chosenDirection;
    }
    
    /**
    Changes the ai's values to make the prediction more accurate
    */
    private void aiLearn() {
        // out
        double[][] origW2 = w2.clone();
        for (int i = 0; i < hidden.length; i++) {
            for (int j = 0; j < a2f().length; j++) {
                double eTotalYFinal = -(answer[j] - a2f()[j][0]);
                double yFinalY = a2f()[j][0] * (1 - a2f()[j][0]); //(8)
                double yw = a1f()[i][0];
                double eTotalW = eTotalYFinal * yFinalY * yw;
                w2[j][i] = w2[j][i] - learningRate * eTotalW;
            }
        }
        //hidden layer
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < hidden.length; j++) {
                double e1H1 = (2 * (0.5 * (answer[0] - out[0][0])) * -1 * (out[0][0] * (1 - out[0][0]))) * origW2[0][i];
                double e2H1 = (2 * (0.5 * (answer[1] - out[1][0])) * -1 * (out[1][0] * (1 - out[1][0]))) * origW2[1][i];
                double eTotalHFinal = e1H1 + e2H1;
                double hFinalH = a1f()[j][0] * (1 - a1f()[j][0]);
                double hw = in[i][0];
                double eTotalWH = eTotalHFinal * hFinalH * hw;
                w1[j][i] = w1[j][i] - learningRate * eTotalWH;
            }
        }
    }
    
    // create w array
    public double[] getW() {
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
    public double[] getB() {
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
    /*
    private double[][] z1() {
        double[][] result = multiplyMatrices(w1, x);
        return result;
    }*/
    
    // 1st laver a vales
    private double[][] a1f() {
        double[][] result = multiplyMatrices(w1, in);
        result = f(addMatrices(result, b1));
        return result;
    }
    
    // 2nd laver z vales 
    /*
    private double[][] z2() {
        double[][] result = multiplyMatrices(w2, z1());
        return result;
    }*/
    
    // 2nd laver a vales
    private double[][] a2f() {
        double[][] result = multiplyMatrices(w2, a1f());
        result = f(addMatrices(result, b2));
        return result;
    }
    
    // multiply matrices
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
        double[][] result = new double[secondMatrix.length][secondMatrix[0].length];
        
        for (int row = 0; row < secondMatrix.length; row++) {
            for (int col = 0; col < secondMatrix[0].length; col++) {
                result[row][col] = firstMatrix[row][col] + secondMatrix[row][col];
            }
        }
        
        return result;
    }
    
    // find the hidden layer values, the output values, and the total error
    private double[][] calculateMatrices(double[][] input) {
        double[][] result;
        in = input;
        hidden = a1f();
        out = result = a2f();
        totalError = 0f; // TODO check if this works
        for (int i = 0; i < a2f().length; i++) {
            for (int j = 0; j < answer.length; j++) {
                answer[j] = getBetterAnswer(input)[j][0];
            }
            totalError += error[i] = Math.pow((a2f()[i][0] - answer[i]), 2);
        }
        totalError /= a2f().length;
        return result;
    }
    
    // finds longest input
    private int findDirection(double[][] in, Box o) {
        int result = 0;
        double oldValue = 0.0;
        double value;
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
    
    /*
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
    } */
    
    private double[][] getBetterAnswer(double[][] directions) {
        double[][] result = new double[out.length][1];
        double[][] values = directions;
        int index = 0;
        double old = 0.0;
        for (int i = 0; i < values.length; i++) {
            if (values[i][0] > old) {
                if (((i!=0)&&values[i-1][0]<threshold) || ((i!=values.length-1)&&values[i+1][0]<threshold)) {
                    continue;
                }
                index = i;
                old = values[i][0];
            }
        }
        for (int i = 0; i < result.length; i++) {
            if (i == index) {
                result[i][0] = 1;
            }
            else {
                result[i][0] = 0;
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
        for (double[] row : mat) {
            System.out.println(Arrays.toString(row));
        }
    }
    
    // randomizes a 2d array
    private void randomizeMatrices(double[][] in) {
        random.setSeed(seed);
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                in[row][col] = random.nextDouble();//Math.random();
            }
        }
    }
    
    // sigmoid function
    private double[][] f(double[][] in) {
        double[][] result = new double[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                double x = in[row][col];
                result[row][col] = (1/( 1 + Math.pow(Math.E,(-1*x))));
            }
        }
        
        return result;
    }
    
    public void reset() {
        in = new double[kNumXNodes][1];
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
        errorGraph = new ArrayList<>();
        
        // currentIteration = 0;
        totalError = 0.0;
    }

    public int getOutNodes() {
        return kNumOutNodes;
    }

    public void setSeed(int s) {
        seed = s;
    }
}
