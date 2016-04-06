package lab2.Helpers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class MedianCutQuantization {
    final static int RED = 0;
    final static int GREEN = 1;
    final static int BLUE = 2;

    public static BufferedImage medianCut(BufferedImage orig, int maxColors) {
        int[][] repCols = processColors(orig, maxColors);
        return performQuantization(orig, repCols);
    }

    public static BufferedImage performQuantization(BufferedImage img, int[][] newCols) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int index = 0;
                double closest = distanceToRGB(img.getRGB(x,y), newCols[0][0]);

                for (int i = 1; i < newCols.length; i++) {
                    for (int j = 1; j < newCols.length; j++) {
                        if (closest > distanceToRGB(img.getRGB(x,y), newCols[i][j])) {
                            closest = distanceToRGB(img.getRGB(x,y), newCols[i][j]);
                            index = j;
                        }
                    }
                }
                img.setRGB(x, y, newCols[index][index]);
            }
        }
        return img;
    }

    public static int[][] processColors(BufferedImage orig, int maxColors) {
        HashMap origCols = processOriginalColors(orig);

        if (origCols.size() <= maxColors) {
            int[][] toReturn = new int[origCols.size()][origCols.size()];
            Iterator it = origCols.values().iterator();
            int index = 0;
            while (it.hasNext()) {
                StatColorRep mc = (StatColorRep) it.next();
                toReturn[index][index] = mc.colorCode;
                index++;
            }
            return toReturn;
        } else {
            ArrayList colorBoxes = new ArrayList();
            ColorContainer first = new ColorContainer(origCols, 0);
            colorBoxes.add(first);
            int k = 1;
            boolean done = false;

            while (k < maxColors && !done) {

                ColorContainer next = findBoxToSplit(colorBoxes);
                if (next != null) {
                    ColorContainer[] boxes = splitBox(next);

                    if (colorBoxes.remove(next)) {}
                    colorBoxes.add(boxes[0]);
                    colorBoxes.add(boxes[1]);
                    k++;

                } else {
                    done = true;
                }
            }

            int[][] avgCols = new int[colorBoxes.size()][colorBoxes.size()];
            for (int i = 0; i < avgCols.length; i++) {
                for (int j = 0; j < avgCols.length; j++) {
                    ColorContainer cb = (ColorContainer) colorBoxes.get(i);
                    avgCols[i][j] = averageColor(cb);
                }
            }
            return avgCols;
        }
    }

    public static HashMap processOriginalColors(BufferedImage orig) {
        HashMap origColors = new HashMap();
        for (int i = 0; i < orig.getWidth(); i++) {
            for (int j = 0; j < orig.getHeight(); j++) {
                if (origColors.containsKey(orig.getRGB(i,j))) {
                    StatColorRep temp = (StatColorRep) origColors.get(orig.getRGB(i,j));
                    temp.increment();
                } else {
                    StatColorRep toAdd = new StatColorRep(orig.getRGB(i,j));
                    origColors.put(orig.getRGB(i,j), toAdd);
                }
            }
        }
        return origColors;
    }

    public static ColorContainer findBoxToSplit(ArrayList listOfBoxes) {
        ArrayList canBeSplit = new ArrayList();

        for (int i = 0; i < listOfBoxes.size(); i++) {
            ColorContainer cb = (ColorContainer) listOfBoxes.get(i);
            if (cb.colors.size() > 1) {
                canBeSplit.add(cb);
            }
        }

        if (canBeSplit.size() == 0) {
            return null;
        } else {
            ColorContainer minBox = (ColorContainer) canBeSplit.get(0);
            int minLevel = minBox.level;

            for (int i = 1; i < canBeSplit.size(); i++) {
                ColorContainer test = (ColorContainer) canBeSplit.get(i);
                if (minLevel > test.level) {
                    minLevel = test.level;
                    minBox = test;
                }
            }
            return minBox;
        }
    }

    public static ColorContainer[] splitBox(ColorContainer bx) {
        int m = bx.level;
        int d = findMaxDimension(bx);

        HashMap cols = bx.colors;
        Iterator it = cols.values().iterator();
        double c = 0.0;
        while (it.hasNext()) {
            StatColorRep mc = (StatColorRep) it.next();
            c += mc.rgb[d];
        }

        double median = c / (double)(cols.size());

        HashMap left = new HashMap();
        HashMap right = new HashMap();

        Iterator itr = cols.values().iterator();
        while (itr.hasNext()) {
            StatColorRep mc = (StatColorRep) itr.next();
            if (mc.rgb[d] <= median) {
                left.put(mc.colorCode, mc);
            } else {
                right.put(mc.colorCode, mc);
            }
        }

        ColorContainer[] toReturn = new ColorContainer[2];
        toReturn[0] = new ColorContainer(left, m + 1);
        toReturn[1] = new ColorContainer(right, m + 1);

        return toReturn;
    }

    public static double findMinMax(double[] f, int k) {
        if (f.length > 0) {
            double m = f[0];
            for (int i = 1; i < f.length; i++) {
                if (k == 0) {
                    m = min(m, f[i]);
                } else {
                    m = max(m, f[i]);
                }
            }
            return m;
        } else {
            return 0.0;
        }
    }

    public static int findMaxDimension(ColorContainer bx) {

        double[] dims = new double[3];
        dims[0] = bx.rmax - bx.rmin;
        dims[1] = bx.gmax - bx.gmin;
        dims[2] = bx.bmax - bx.bmin;

        double sizeMax = findMinMax(dims, 1);

        if (sizeMax == dims[0]) {
            return RED;
        } else if (sizeMax == dims[1]) {
            return GREEN;
        } else {
            return BLUE;
        }
    }

    public static int averageColor(ColorContainer bx) {

        HashMap colorMap = bx.colors;
        Iterator it = colorMap.values().iterator();
        double[] rgb = {0.0, 0.0, 0.0};

        while (it.hasNext()) {
            StatColorRep mc = (StatColorRep) it.next();
            for (int i = 0; i < 3; i++) {
                rgb[i] += mc.rgb[i];
            }
        }
        Double avgRed = rgb[RED] / (double)(colorMap.size());
        Double avgGreen = rgb[GREEN] / (double)(colorMap.size());
        Double avgBlue = rgb[BLUE] / (double)(colorMap.size());

        return Helper.colorToRGBint(0, avgRed.intValue(), avgGreen.intValue(), avgBlue.intValue());
    }

    public static double distanceToRGB(int col1, int col2) {
        StatColorRep c1 = new StatColorRep(col1);
        StatColorRep c2 = new StatColorRep(col2);

        double redDiff = abs(c1.rgb[RED] - c2.rgb[RED]);
        double greenDiff = abs(c1.rgb[GREEN] - c2.rgb[GREEN]);
        double blueDiff = abs(c1.rgb[BLUE] - c2.rgb[BLUE]);

        return ((redDiff + greenDiff + blueDiff)/3.0);
    }

    public static class StatColorRep {
        int colorCode;
        int count;
        double [] rgb = new double[3];
        StatColorRep(int colorCode){
            this.colorCode = colorCode;
            rgb[RED]= (colorCode >> 16) & 0xFF;
            rgb[GREEN]=(colorCode >> 8) & 0xFF;
            rgb[BLUE]= colorCode & 0xFF;

            count = 1;
        }

        void increment(){
            count++;
        }
    }

    public static class ColorContainer {
        double rmin, rmax ,gmin, gmax, bmin, bmax;
        int level;
        HashMap colors;

        ColorContainer(HashMap colors, int level){
            this.colors = colors;
            this.level = level;

            double [] redColors = new double[colors.size()];
            double [] greenColors = new double[colors.size()];
            double [] blueColors = new double[colors.size()];

            Iterator it = colors.values().iterator();
            int index = 0;

            while(it.hasNext()){
                StatColorRep mc = (StatColorRep)it.next();
                redColors[index] = mc.rgb[RED];
                greenColors[index] = mc.rgb[GREEN];
                blueColors[index] = mc.rgb[BLUE];
                index++;
            }

            rmin = findMinMax(redColors,0);
            rmax = findMinMax(redColors,1);
            gmin = findMinMax(greenColors,0);
            gmax = findMinMax(greenColors,1);
            bmin = findMinMax(blueColors,0);
            bmax = findMinMax(blueColors,1);
        }
    }
}

