package sample.AllEffects;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.Image;
import sample.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Noise implements IEffect {
    private static final Random random = new Random();
    private boolean isFigureNoise;

    public Noise(boolean isFigureNoise) {
        this.isFigureNoise = isFigureNoise;
    }

    @Override
    public Image make(Image image) {
        return null;
    }

    @Override
    public Image make(Image image, int param) {

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        resImage = createCopy(bufferedImage);

        if (isFigureNoise) {
            Graphics2D graphics = (Graphics2D) resImage.getGraphics();

            for (int i = 0; i < 50; i++) {
                graphics.setColor(randomColor());

                int x = random.nextInt(resImage.getWidth()),
                    y = random.nextInt(resImage.getHeight());
                int w = random.nextInt(30),
                    h = random.nextInt(30);

                graphics.drawLine(x, y, x + w, x + h);
            }

            for (int i = 0; i < 20; i++) {
                graphics.setColor(randomColor());

                int x = random.nextInt(resImage.getWidth()),
                    y = random.nextInt(resImage.getHeight());
                int d = random.nextInt(30);

                graphics.drawOval(x, y, d, d);
            }
        } else {
            for (int x = 0; x < bufferedImage.getWidth(); x++)
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    Color color = Main.parseColor(bufferedImage.getRGB(x, y));

                    color = new Color(addNoise(color.getRed()), addNoise(color.getGreen()), addNoise(color.getBlue()));
                    resImage.setRGB(x, y, color.getRGB());
                }
        }

        return SwingFXUtils.toFXImage(resImage, null);
    }

    public static BufferedImage createCopy(BufferedImage source) {
        BufferedImage copy =  new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        copy.getGraphics().drawImage(source, 0, 0, source.getWidth(), source.getHeight(), null);
        return copy;
    }

    @Override
    public Image make(Image image, double param) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        if (isFigureNoise) {
            Graphics2D graphics = (Graphics2D) resImage.getGraphics();

            for (int i = 0; i < param; i++) {
                graphics.setColor(randomColor());

                int x = random.nextInt(resImage.getWidth()),
                        y = random.nextInt(resImage.getHeight());
                int w = random.nextInt((int)param),
                        h = random.nextInt((int)param);

                graphics.drawLine(x, y, x + w, x + h);
            }

            for (int i = 0; i < param; i++) {
                graphics.setColor(randomColor());

                int x = random.nextInt(resImage.getWidth()),
                        y = random.nextInt(resImage.getHeight());
                int d = random.nextInt((int)param);

                graphics.drawOval(x, y, d, d);
            }
        } else {
            for (int x = 0; x < resImage.getHeight(); x++)
                for (int y = 0; y < resImage.getHeight(); y++) {
                    Color color = Main.parseColor(resImage.getRGB(x, y));

                    color = new Color(addNoise(color.getRed()), addNoise(color.getGreen()), addNoise(color.getBlue()));
                    resImage.setRGB(x, y, color.getRGB());
                }
        }

        return SwingFXUtils.toFXImage(resImage, null);
    }

    private int addNoise(int v) {
        int p = (int)(256D / 100 * 12.5);
        return Math.max(0, Math.min(v + (random.nextInt(p * 2) - p), 255));
    }

    private Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
