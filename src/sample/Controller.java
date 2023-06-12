package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private MenuBar menuBar;
    @FXML
    Menu menu;
    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem about;

    Stage stage;

    public Controller() {

    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }

    @FXML
    private void handleAboutAction(ActionEvent event) {
        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle("About");
        ButtonType type = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Text text = new Text("This is a bot intended to automate PvP in Digimon RPG. The main use case for this application is to " +
                "gather ladder points without having to interact with the game. This bot is not intended to boost your ladder rank. There is only support for your leader digimon but summoning and battling with up to three digimon could be added in the future. If there is any interest in this application, there " +
                "may be further development with more complex features. Please select the skill you want to use as well as the cards you plan to use for the battles. You should start using the bot from the battle lobby " +
                "or during a battle that you started. If you experience lag, the bot might skip picking cards or mega evolution(like when gankoo is on the screen). If the bot has any problems, try pressing stop and then start again. It will understand what is happening and respond accordingly. Please report any errors or issues to the developer at the forum/location you downloaded this application. Enjoy! :)");
        text.setWrappingWidth(500.0);
        //Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/PixelFont.ttf"),14.0);
        //text.setFont(font);
        dialog.getDialogPane().setContent(text);
        dialog.getDialogPane().setPadding(new Insets(24.0));
        stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/gui_res/logo.png"));
        dialog.getDialogPane().getButtonTypes().add(type);

        dialog.showAndWait();
    }

}
