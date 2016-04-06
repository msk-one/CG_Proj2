package lab2.Helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Created by Msk on 4/6/2016.
 */
public class PopularityAlgQuantization {

    public static BufferedImage performQuantization(BufferedImage img, ArrayList<StatColorRep> newCols) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                double minDist = distanceToRGB(0, 16777215);
                int colIdx = 0;
                for (int i = 0; i < newCols.size(); i++) {
                    double localDist = distanceToRGB(img.getRGB(x,y), newCols.get(i).colorCode);
                    if(localDist < minDist) {
                        minDist = localDist;
                        colIdx = i;
                    }
                }
                img.setRGB(x, y, newCols.get(colIdx).colorCode);
            }
        }
        return img;
    }

    public static ArrayList<StatColorRep> prepareColorArray(BufferedImage image, int arrSize) {
        ArrayList<StatColorRep> colorsArr = new ArrayList<StatColorRep>();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int colorCode = image.getRGB(x,y);
                StatColorRep currColor = new StatColorRep(colorCode);
                if(colorsArr.contains(currColor)) {
                    colorsArr.get(colorsArr.indexOf(currColor)).incrementCount();
                }
                else {
                    colorsArr.add(currColor);
                }
            }
        }

        Collections.sort(colorsArr, Collections.reverseOrder(new CountComparator()));
        ArrayList<StatColorRep> tempArr = new ArrayList<StatColorRep>(colorsArr.subList(0, arrSize));
        return tempArr;
    }

    public static double distanceToRGB(int col1, int col2) {
        StatColorRep c1 = new StatColorRep(col1);
        StatColorRep c2 = new StatColorRep(col2);

        return Math.sqrt((c1.r - c2.r)*(c1.r - c2.r)+(c1.g - c2.g)*(c1.g - c2.g)+(c1.b - c2.b)*(c1.b - c2.b));
    }

    public static class CountComparator implements Comparator<StatColorRep>
    {
        public int compare(StatColorRep p1, StatColorRep p2)
        {
            int c1 = p1.count;
            int c2 = p2.count;

            if (c1 == c2)
                return 0;
            else if (c1 > c2)
                return 1;
            else
                return -1;
        }
    }

    public static class StatColorRep {
        int colorCode;
        int count;
        double r,g,b;

        StatColorRep(int colorCode){
            this.colorCode = colorCode;
            r = (colorCode >> 16) & 0xFF;
            g =(colorCode >> 8) & 0xFF;
            b = colorCode & 0xFF;
            count = 1;
        }

        void incrementCount(){
            count++;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean sameSame = false;

            if (object != null && object instanceof StatColorRep)
            {
                sameSame = (this.colorCode == ((StatColorRep) object).colorCode)
                && (this.r == ((StatColorRep) object).r)
                && (this.g == ((StatColorRep) object).g)
                && (this.b == ((StatColorRep) object).b);
            }

            return sameSame;
        }
    }
}
