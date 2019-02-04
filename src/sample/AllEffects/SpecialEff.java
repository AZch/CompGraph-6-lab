package sample.AllEffects;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sample.Constants;
import sample.Main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpecialEff implements IEffect {
    private int curr;

    public SpecialEff(int curr) {
        this.curr = curr;
    }

    @Override
    public Image make(Image image) {
        return null;
    }

    @Override
    public Image make(Image image, int param) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        switch (curr) {
            case Constants.WATERCOLOR:
                for (int i = 0; i < 5; i++)
                    image = (new Filter(Constants.MEDIAN)).make(image, 2);
                image = (new Filter(Constants.HARSHNESS)).make(image, 2);
                resImage = SwingFXUtils.fromFXImage(image, null);
                break;
            case Constants.EMBOSSING:
                (new Filter(Constants.UNIFORM)).make(image, 2);

                int buf = 1;
                double[][] matrix = new double[][] {
                        {0,  1,  0},
                        {1,  0, -1},
                        {0, -1,  0}
                };
                resImage = Filter.filter(bufferedImage, resImage, 1, 128, (x, y) -> matrix[x + buf][y + buf]);
                break;
//            case Constants.DELINEATION:
//                image = (new Filter(Constants.GAUSS)).make(image, 0.7);
//                resImage = delineation(bufferedImage, param);
//                break;
            case Constants.WAVES: {
                resImage = distortion(bufferedImage, resImage, (x, y) -> (int)(x + 20 * Math.sin(2 * Math.PI * y / 128)), (x, y) -> y);
                break;
            }
        }

        return SwingFXUtils.toFXImage(resImage, null);
    }

    @Override
    public Image make(Image image, double param) {
        return make(image, (int) param);
    }

    private BufferedImage distortion(BufferedImage image, BufferedImage res, fun fx, fun fy) {
        BufferedImage copy = createCopy(image);

        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++) {
                int nx = Math.max(0, Math.min(fx.fun(x, y), image.getWidth() - 1));
                int ny = Math.max(0, Math.min(fy.fun(x, y), image.getHeight() - 1));

                res.setRGB(x, y, copy.getRGB(nx, ny));
            }

        return res;
    }

    private BufferedImage delineation(BufferedImage image, int base) {
        double[][] colors = new double[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = Main.parseColor(image.getRGB(x, y));
                colors[x][y] = color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114;
            }

        int buf = 1;
        double[][] massFirst = new double[][] {
                { 1,  2,  1},
                { 0,  0,  0},
                {-1, -2, -1}
        };
        double[][] massSecond = new double[][] {
                   {-1, 0, 1},
                   {-2, 0, 2},
                   {-1, 0, 1}
        };

        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                double firstCount = 0, secCount = 0;

                for (int dx = -buf; dx <= buf; dx++)
                    for (int dy = -buf; dy <= buf; dy++) {
                        firstCount += massFirst[dx + buf][dy + buf] * getPixelColor(image, colors, x + dx, y + dy);
                        secCount += massSecond[dx + buf][dy + buf] * getPixelColor(image, colors, x + dx, y + dy);
                    }

                double bufPerem = Math.sqrt(firstCount * firstCount + secCount * secCount);
                image.setRGB(x, y, bufPerem < base? 0 : 0xFFFFFF);
            }

        return image;
    }

    private static double getPixelColor(BufferedImage image, double[][] colors, int x, int y) {
        if(x < 0) x = image.getWidth() - x;
        if(y < 0) y = image.getHeight() - y;
        return colors[x % image.getWidth()][y % image.getHeight()];
    }

    public static BufferedImage createCopy(BufferedImage source) {
        BufferedImage copy =  new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        copy.getGraphics().drawImage(source, 0, 0, source.getWidth(), source.getHeight(), null);
        return copy;
    }

    private interface fun {
        int fun(int x, int y);
    }
}
