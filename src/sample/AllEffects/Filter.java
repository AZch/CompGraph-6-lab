package sample.AllEffects;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sample.Constants;
import sample.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Filter implements IEffect {
    private int type = Constants.UNIFORM;

    public Filter(int type) {
        this.type = type;
    }

    @Override
    public Image make(Image image) {
        return make(image, 1);
    }

    @Override
    public Image make(Image image, int param) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        switch (type) {
            case Constants.UNIFORM:
                resImage = filter(bufferedImage, resImage, param, 0, (x, y) -> 1D / ((param * 2 + 1) * (param * 2 + 1)));
                break;
            case Constants.GAUSS:
                int r = 2;
                double sigma = (double) param;

                double buf = 0;
                for (int i = -r; i <= r; i++)
                    for (int j = -r; j <= r; j++)
                        buf += Math.exp(-(i * i + j * j) / (2 * sigma * sigma));
                buf = 1 / buf;

                double[][] matrix = new double[r * 2 + 1][r * 2 + 1];
                for (int i = -r; i < r; i++)
                    for (int j = -r; j < r; j++)
                        matrix[i + r][j + r] = buf * Math.exp(-(i * i + j * j) / (2 * sigma * sigma));

                resImage = filter(bufferedImage, resImage, r, 0, (x, y) -> matrix[x + r][y + r]);
                break;
            case Constants.MEDIAN:
                resImage = filter(bufferedImage, resImage, param, 0, null);
                break;
            case Constants.HARSHNESS:
                int buffer = 1;

                resImage = filter(bufferedImage, resImage, buffer, 0, (x, y) -> {
                    if (x == buffer && y == buffer)
                        return param + 1;
                    return  -param / 8D;
                });
                break;
        }

        return SwingFXUtils.toFXImage(resImage, null);
    }

    @Override
    public Image make(Image image, double param) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        switch (type) {
            case Constants.UNIFORM:
                resImage = filter(bufferedImage, resImage, (int)param, 0, (x, y) -> 1D / ((param * 2 + 1) * (param * 2 + 1)));
                break;
            case Constants.GAUSS:
                int r = 2;
                double sigma = (double) param;

                double buf = 0;
                for (int i = -r; i <= r; i++)
                    for (int j = -r; j <= r; j++)
                        buf += Math.exp(-(i * i + j * j) / (2 * sigma * sigma));
                buf = 1 / buf;

                double[][] matrix = new double[r * 2 + 1][r * 2 + 1];
                for (int i = -r; i < r; i++)
                    for (int j = -r; j < r; j++)
                        matrix[i + r][j + r] = buf * Math.exp(-(i * i + j * j) / (2 * sigma * sigma));

                resImage = filter(bufferedImage, resImage, r, 0, (x, y) -> matrix[x + r][y + r]);
                break;
            case Constants.MEDIAN:
                resImage = filter(bufferedImage, resImage, (int)param, 0, null);
                break;
            case Constants.HARSHNESS:
                int buffer = 1;

                resImage = filter(bufferedImage, resImage, buffer, 0, (x, y) -> {
                    if (x == buffer && y == buffer)
                        return param + 1;
                    return  -param / 8D;
                });
                break;
        }

        return SwingFXUtils.toFXImage(resImage, null);
    }

    public static BufferedImage filter(BufferedImage image, BufferedImage res, int param, int nextParam, WeightFunction weight) {
        Color[][] colors = new Color[image.getWidth()][image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++)
                colors[x][y] = Main.parseColor(image.getRGB(x, y));

        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                double r = 0, g = 0, b = 0;
                if (weight != null) {
                    for (int dx = -param; dx <= param; dx++)
                        for (int dy = -param; dy <= param; dy++) {
                            Color color = getPixelColor(image, colors, x + dx, y + dy);

                            double w = weight.getWeightForPixel(dx, dy);
                            r += color.getRed() * w;
                            g += color.getGreen() * w;
                            b += color.getBlue() * w;
                        }
                } else {
                    int buf = param * 2 + 1, avg = buf * buf / 2 - 1;
                    int[][] med = new int[3][buf * buf];

                    for (int dx = -param; dx <= param; dx++)
                        for (int dy = -param; dy <= param; dy++) {
                            Color color = getPixelColor(image, colors, x + dx, y + dy);
                            int index = (dx + param) * buf + (dy + param);

                            med[0][index] = color.getRed();
                            med[1][index] = color.getGreen();
                            med[2][index] = color.getBlue();
                        }
                    for (int i = 0; i < 3; i++)
                        Arrays.sort(med[i]);
                    r = med[0][avg];
                    g = med[1][avg];
                    b = med[2][avg];
                }

                Color color = new Color(normalize(r + nextParam), normalize(g + nextParam), normalize(b + nextParam));
                res.setRGB(x, y, color.getRGB());
            }
            return res;
    }

    public void setUniform() {
        type = Constants.UNIFORM;
    }

    public void setGAUSS() {
        type = Constants.GAUSS;
    }

    public void setMEDIAN() {
        type = Constants.MEDIAN;
    }

    public void setHAESHNESS() {
        type = Constants.HARSHNESS;
    }

    private static Color getPixelColor(BufferedImage image, Color[][] colors, int x, int y) {
        if(x < 0) x = image.getWidth() - x;
        if(y < 0) y = image.getHeight() - y;
        return colors[x % image.getWidth()][y % image.getHeight()];
    }

    private static int normalize(double d) {
        return Math.max(0, Math.min(255, (int)Math.round(d)));
    }

    public interface WeightFunction {

        double getWeightForPixel(int dx, int dy);

    }
}
