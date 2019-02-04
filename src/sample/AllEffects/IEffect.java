package sample.AllEffects;

import javafx.scene.image.Image;

public interface IEffect {
    Image make(Image image);

    Image make(Image image, int param);


    Image make(Image image, double param);
}
