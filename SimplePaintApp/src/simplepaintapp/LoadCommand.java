package simplepaintapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

public class LoadCommand implements Command {

    private ArrayList<Shape> shapes;         //to hold all of the objects that was created from Shape class
    private Canvas canvas;

    public LoadCommand(Canvas canvas) {
        this.canvas = canvas;
        this.shapes = canvas.getShapes();
    }

    public void execute() {
        try {
            shapes.clear();
            BufferedReader reader = new BufferedReader(new FileReader("save.txt"));   //reader to read all text in save.txt
            String line = reader.readLine();
            int tabCount = 0;
            boolean needGroup = false;
            ArrayList<ArrayList<Shape>> groups = new ArrayList();   //to hold all grouped objects that was 
            groups.add(shapes);
            int groupCount = 0;
            RectangleDelegate delegate = RectangleDelegate.getInstance();
            Shape tempObj = new Shape(0, 0, 0, 0, delegate);

            while (line != null) {
                String[] splitted = line.split("\\s+");
                if ("".equals(splitted[0])) {
                    splitted = Arrays.copyOfRange(splitted, 1, splitted.length);
                }

                if (needGroup) {
                    tempObj = lineToShape(splitted);
                    groups.get(groupCount).add(tempObj);
                    groups.add(tempObj.getComponents());
                    groupCount++;
                    needGroup = false;
                }
                int tempTabCount = 0;
                for (char c : line.toCharArray()) {
                    char s = ' ';
                    if (' ' == c) {
                        tempTabCount++;
                    } else {
                        break;
                    }
                }
                if (tempTabCount < tabCount) {
                    groupCount--;
                } else if (tempTabCount > tabCount) {
                    tabCount = tempTabCount;
                }

                if ("group".equals(splitted[0])) {
                    needGroup = true;
                } else if ("Decorator".equals(splitted[0])) {
                    String name = "";
                    for (int i = 1; i < splitted.length - 2; i++) {
                        name += splitted[i];
                        name += " ";

                    }
                    Caption caption = new Caption(name, Integer.parseInt(splitted[splitted.length - 2]), Integer.parseInt(splitted[splitted.length - 1]));
                    tempObj.addText(caption);
                } else {
                    tempObj = lineToShape(splitted);
                    groups.get(groupCount).add(tempObj);
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
            canvas.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Shape lineToShape(String[] splitted) {
        if ("Rectangle".equals(splitted[0])) {
            RectangleDelegate delegate = RectangleDelegate.getInstance();
            Shape obj = new Shape(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), delegate);
            return obj;
        } else {
            EllipseDelegate delegate = EllipseDelegate.getInstance();
            Shape obj = new Shape(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), delegate);
            return obj;
        }
    }
}
