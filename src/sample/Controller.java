package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import sample.AllEffects.Filter;
import sample.AllEffects.Noise;
import sample.AllEffects.SpecialEff;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Controller {
    public Canvas CanvasImage;
    public TextField UniformNum;
    public TextField GaussNum;
    public TextField MedianNum;

    private Image image, originalImage;
    private double scaleX = 1;
    private double scaleY = 1;
    
    private Filter uniform;
    private Filter gauss;
    private Filter median;

    private SpecialEff waterColor;
    private SpecialEff embossing;
    private SpecialEff waves;

    private Noise noise;
    private Noise noiseFigure;

    public void getImageAction(ActionEvent actionEvent) {
        startData();
        image = loadImage("image");
        originalImage = copy(image);
        CanvasImage.getGraphicsContext2D().clearRect(0, 0, CanvasImage.getWidth(), CanvasImage.getHeight());
        if (image.getHeight() > CanvasImage.getHeight()) {
            scaleX = image.getHeight() / CanvasImage.getHeight();
        }
        if (image.getWidth() > CanvasImage.getWidth()) {
            scaleY = image.getWidth() / CanvasImage.getWidth();
        }
        CanvasImage.getGraphicsContext2D().drawImage(image, 0, 0, image.getWidth() / (scaleX * scaleY), image.getHeight() / (scaleX * scaleY));
    }

    private Image loadImage(String startDirectory) {
        FileDialog dialog = new FileDialog((JFrame) null, "Выбрать изображение", FileDialog.LOAD);
        dialog.setDirectory(startDirectory);
        dialog.setVisible(true);

        if (dialog.getFile() == null)
            return loadImage(startDirectory);
        try {
            Image resImage = SwingFXUtils.toFXImage(ImageIO.read(dialog.getFiles()[0]), null);
            if (resImage != null) {
                return resImage;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "Ой-ой что то пошло не так, попробуй ещё раз.");
        return loadImage(startDirectory);
    }

    private void startData() {
        uniform = new Filter(Constants.UNIFORM);
        gauss = new Filter(Constants.GAUSS);
        median = new Filter(Constants.MEDIAN);

        waterColor = new SpecialEff(Constants.WATERCOLOR);
        embossing = new SpecialEff(Constants.EMBOSSING);
        waves = new SpecialEff(Constants.WAVES);

        noise = new Noise(false);
        noiseFigure = new Noise(true);

    }

    private void repaint(Image image) {
        CanvasImage.getGraphicsContext2D().clearRect(0, 0, CanvasImage.getWidth(), CanvasImage.getHeight());
        CanvasImage.getGraphicsContext2D().drawImage(image, 0, 0, image.getWidth() / (scaleX * scaleY), image.getHeight() / (scaleX * scaleY));
    }

    public void StartImageAction(ActionEvent actionEvent) {
        image = copy(originalImage);
        //binarizationSlider.setValue(128);
        repaint(image);
    }

    private Image copy(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage res = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        res.getGraphics().drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        return SwingFXUtils.toFXImage(res, null);
    }

    public void UniformAction(ActionEvent actionEvent) {
        image = uniform.make(image, Integer.parseInt(UniformNum.getText()));
        repaint(image);
    }

    public void GaussAction(ActionEvent actionEvent) {
        image = gauss.make(image, Double.parseDouble(GaussNum.getText()));
        repaint(image);
    }

    public void MedianAction(ActionEvent actionEvent) {
        image = median.make(image, Integer.parseInt(MedianNum.getText()));
        repaint(image);
    }

    public void WaterColorAction(ActionEvent actionEvent) {
        image = waterColor.make(image, 0);
        repaint(image);
    }

    public void EmbossingAction(ActionEvent actionEvent) {
        image = embossing.make(image, 0);
        repaint(image);
    }

    public void WavesAction(ActionEvent actionEvent) {
        image = waves.make(image, 0);
        repaint(image);
    }

    public void NoiseAction(ActionEvent actionEvent) {
        image = noise.make(image, 0);
        repaint(image);
    }

    public void FigureAction(ActionEvent actionEvent) {
        image = noiseFigure.make(image, 0);
        repaint(image);
    }
}
