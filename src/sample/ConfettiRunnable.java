package sample;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class ConfettiRunnable implements Runnable {

    ImageView confetti;

    public ConfettiRunnable() {}

    public ConfettiRunnable(ImageView i) {
        confetti = i;
    }

    public void setImageView(ImageView i) {
        confetti = i;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        }catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        Platform.runLater(() -> confetti.setVisible(false));
    }
}
