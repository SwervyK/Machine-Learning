import java.awt.Point;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    // saving/files
    static int currentVersion = 0;
    String currentDir = System.getProperty("user.dir");
    File version = new File(currentDir, "logs\\version.txt");
    File polygonSave;
    File wSave;
    File bSave;
    
    public void fileSetup() {
        try {
            if (!version.exists()) {
                version.createNewFile();
            }
            Scanner scanner = new Scanner(version);
            String nextLn = scanner.nextLine();
            if (!nextLn.equals("0")) {
                currentVersion = Integer.parseInt(nextLn);
            }
            else {
                currentVersion = 0;
            }
            FileWriter writer = new FileWriter(version);
            writer.write(String.valueOf((currentVersion + 1)));
            writer.close();
            scanner.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
    
    public String[] readData(File file) {
        String[] result = new String[0];
        try {
            result = new String[(int)Files.lines(file.toPath()).count()];
            Scanner scanner = new Scanner(file);
            int index = 0;
            while (scanner.hasNextLine()) {
                result[index] = scanner.nextLine();
                index++;
            }
            scanner.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void PolygonSave(List<Point> points) {
        polygonSave = new File(currentDir, "logs\\polygonData" + currentVersion + ".txt");
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

    public void SaveingBrain(double[] w, double[] b) {
        wSave = new File(currentDir, "logs\\weights" + currentVersion + ".txt");
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

        bSave = new File(currentDir, "logs\\biases" + currentVersion + ".txt");
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
            weights[i] = String.valueOf(b[i]);
        }
        writeData(biases, bSave);
    }

    public String[] FileLoading(String dialog, runner r) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(version);
        fc.setDialogTitle(dialog);
        int returnVal = fc.showOpenDialog(r);
        File file = fc.getSelectedFile();
        String[] data = new String[0];
        if (returnVal == 0) {
            try {
                data = new String[(int)Files.lines(file.toPath()).count()];
                data =  readData(file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
