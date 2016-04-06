package lab2.Helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by mskas on 05.03.2016.
 */
public class Helper {
    public static BufferedImage copyBufferedImage(BufferedImage buff) {
        ColorModel col = buff.getColorModel();
        WritableRaster raster = buff.copyData(null);

        return new BufferedImage(col, raster, col.isAlphaPremultiplied(), null);
    }

    public static int colorToRGBint(int alpha, int red, int green, int blue) {
        int RGBint = 0;

        RGBint += alpha;
        RGBint = RGBint << 8;
        RGBint += red;
        RGBint = RGBint << 8;
        RGBint += green;
        RGBint = RGBint << 8;
        RGBint += blue;

        return RGBint;
    }

    public static Color[] genGrayLevels(int levels) {
        Color[] grayLevels;
        if(levels > 1) {
            grayLevels = new Color[levels];
            for (int i = 0; i < levels; i++)
            {
                grayLevels[i] = new Color((int)255.0/(levels - 1)*i, (int)255.0/(levels - 1)*i, (int)255.0/(levels - 1)*i);
            }
        }
        else {
            grayLevels = new Color[1];
            grayLevels[0] = (Color.WHITE);
        }

        return grayLevels;
    }

    public static boolean checkPowerOfTwo(int x)
    {
        return (x & (x - 1))==0;
    }

    public static double logOfBase(int base, int num) {
        return Math.log(num) / Math.log(base);
    }

}