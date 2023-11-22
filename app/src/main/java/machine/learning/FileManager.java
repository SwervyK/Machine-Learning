package machine.learning;

import java.awt.Point;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileManager {
    // saving/files
    private static int currentVersion = 0;
    private static String currentDir = System.getProperty("user.dir");
    private static File version = new File(currentDir, "logs\\version.txt");

    private FileManager() { }
    
    public static void incrementVersion() {
        try {
            if (!version.exists()) {
                version.createNewFile();
            }
            try (Scanner scanner = new Scanner(version)) {
                String nextLn = scanner.nextLine();
                if (!nextLn.equals("0")) {
                    currentVersion = Integer.parseInt(nextLn);
                }
                else {
                    currentVersion = 0;
                }
            }
            try (FileWriter writer = new FileWriter(version)) {
                writer.write(String.valueOf((currentVersion + 1)));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private static void writeData(String data, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void writeData(String[] dataArr, File file) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < dataArr.length; i++) {
            stringBuilder.append(dataArr[i] + "\n");
        }
        writeData(stringBuilder.toString(), file);
    }
    
    private static String[] readData(File file) {
        String[] result = new String[0];
        try (Stream<String> stream = Files.lines(file.toPath())) {
            result = new String[(int)stream.count()];
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try (Scanner scanner = new Scanner(file)) {
            int index = 0;
            while (scanner.hasNextLine()) {
                result[index] = scanner.nextLine();
                index++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void polygonSave(List<Point> points) {
        File polygonSave = new File(currentDir, "logs\\polygonData" + currentVersion + ".txt");
        try {
            if (!polygonSave.exists()) {
                polygonSave.createNewFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String[] polygonPoints = new String[points.size()];
        for (int i = 0; i < polygonPoints.length; i++) {
            polygonPoints[i] = String.valueOf(points.get(i));
        }
        writeData(polygonPoints, polygonSave);
    }
    
    public static void savingBrain(double[] w, double[] b) {
        File wSave = new File(currentDir, "logs\\weights" + currentVersion + ".txt");
        try {
            if (!wSave.exists()) {
                wSave.createNewFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String[] weights = new String[w.length];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = String.valueOf(w[i]);
        }
        writeData(weights, wSave);
        
        File bSave = new File(currentDir, "logs\\biases" + currentVersion + ".txt");
        try {
            if (!bSave.exists()) {
                bSave.createNewFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String[] biases = new String[b.length];
        for (int i = 0; i < biases.length; i++) {
            biases[i] = String.valueOf(b[i]);
        }
        writeData(biases, bSave);
    }
    
    public static String[] fileLoading(String dialog) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(version);
        fc.setDialogTitle(dialog);
        int returnVal = fc.showOpenDialog(fc);
        File file = fc.getSelectedFile();
        String[] data = new String[0];
        if (returnVal == 0) {
            try (Stream<String> stream = Files.lines(file.toPath())) {
                data = new String[(int)stream.count()]; // TODO is this needed
                data = readData(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    
    public static double[][][] loadBrain(int x, int h, int y) {
        String[] w = fileLoading("Weights values");
        double[] wDouble = new double[w.length];
        for (int i = 0; i < wDouble.length; i++) {
            wDouble[i] = Double.parseDouble(w[i]);
        }
        String[] b = fileLoading("Bias values");
        double[] bDouble = new double[b.length];
        for (int i = 0; i < bDouble.length; i++) {
            bDouble[i] = Double.parseDouble(b[i]);
        }
        
        double[][] w1 = new double[x][h];
        double[][] w2 = new double[h][y];
        double[][] b1 = new double[x][1];
        double[][] b2 = new double[h][1];
        
        int wIndex = 0;
        for (int i = 0; i < w1.length; i++) {
            for (int j = 0; j < w1[0].length; j++) {
                w1[i][j] = wDouble[wIndex];
                wIndex++;
            }
        }
        for (int i = 0; i < w2.length; i++) {
            for (int j = 0; j < w2[0].length; j++) {
                w2[i][j] = wDouble[wIndex];
                wIndex++;
            }
        }
        
        int bIndex = 0;
        for (int i = 0; i < b1.length; i++) {
            for (int j = 0; j < b1[0].length; j++) {
                b1[i][j] = bDouble[bIndex];
                bIndex++;
            }
        }
        for (int i = 0; i < b2.length; i++) {
            for (int j = 0; j < b2[0].length; j++) {
                b2[i][j] = bDouble[bIndex];
                bIndex++;
            }
        }

        return new double[][][] {w1, w2, b1, b2};
    }
}
