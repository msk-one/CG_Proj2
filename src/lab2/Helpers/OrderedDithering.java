package lab2.Helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by mskas on 05.04.2016.
 */
public class OrderedDithering {
    private static int[][] OffsetPoints =
    {
        {0, 0},
        {1, 1},
        {0, 1},
        {1, 0}
    };

    private static int[][] sample2x2 =
    {
        {1,3},
        {4,2}
    };

    private static int[][] sample3x3 =
    {
        {3,7,4},
        {6,1,9},
        {2,8,5}
    };

    public static BufferedImage orderedDithering(int[][] matrix, BufferedImage image, Color[] grayLvls)
    {
        for(int i = 0; i<image.getWidth(); i++) {
            for (int j = 0; j<image.getHeight(); j++) {
                int n = matrix.length;

                int alpha = new Color(image.getRGB(i,j)).getAlpha();
                int red = new Color(image.getRGB(i,j)).getRed();
                int green = new Color(image.getRGB(i,j)).getGreen();
                int blue = new Color(image.getRGB(i,j)).getBlue();

                double avgColor = (0.21*red) + (0.72*green) + (0.07*blue);

                double shade = Math.floor(((double)grayLvls.length-1)*(avgColor/256));
                double res = (grayLvls.length - 1)*(avgColor/256)-shade;

                double d = 1/((double)n*(double)n+1) ;
                if(res >= (d*matrix[i%n][j%n]))
                {
                    ++shade;
                }

                int greyscaleRed = grayLvls[(int)shade].getRed();
                int greyscaleGreen = grayLvls[(int)shade].getGreen();
                int greyscaleBlue = grayLvls[(int)shade].getBlue();

                image.setRGB(i, j, Helper.colorToRGBint(alpha, greyscaleRed, greyscaleGreen, greyscaleBlue));
            }
        }

        return image;
    }

    public static int[][] prepareMatrix(int size)
    {
        if(size == 2) {
            return sample2x2;
        }
        else if (size == 3) {
            return sample3x3;
        }

        int[][] sampleMatrix;
        int recursCount = 0;

        if (Helper.checkPowerOfTwo(size))
        {
            sampleMatrix = sample2x2;
            Double calc = Helper.logOfBase(size, 2)-1;
            recursCount = calc.intValue();
        }
        else if (((size%3)==0 && (size%4)==0) || size==6)
        {
            sampleMatrix = sample3x3;
            Double calc = Helper.logOfBase(size/3, 2);
            recursCount = calc.intValue();
        }
        else
        {
            Alert alr = new Alert(Alert.AlertType.ERROR, "Wrong matrix size!", ButtonType.OK);
            alr.show();
            return null;
        }

        return prepareMatrixRecursively(recursCount, sampleMatrix.length, sampleMatrix);
    }

    private static int[][] prepareMatrixRecursively(int opCount, int mSize, int[][] sample)
    {
        int [][] processedArr = new int[mSize*2][mSize*2];

        for (int d = 0; d < 4; d++)
        {
            for (int i = 0; i < mSize; i++)
            {
                for (int j = 0; j < mSize; j++)
                {
                    processedArr[i+OffsetPoints[d][0]*mSize][j+OffsetPoints[d][1]*mSize] = 4*(sample[i][j]-1)+d+1;
                }
            }
        }

        if (opCount-- <= 0)
            return processedArr;

        return prepareMatrixRecursively(opCount, mSize*2, processedArr);
    }

}
