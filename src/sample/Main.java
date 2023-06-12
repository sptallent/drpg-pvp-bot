package sample;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application implements NativeKeyListener {

    private PvpRobot robot;
    private Thread backgroundThread;
    private Runnable task;

    private Label statusLabel;
    private Button actionButton;
    private ComboBox<String> skillComboBox;
    private ComboBox<String> slotOneComboBox;
    private ComboBox<String> slotTwoComboBox;
    private ComboBox<String> slotThreeComboBox;

    private ToggleButton megaToggle;
    private ToggleButton runToggle;

    private ImageView confettiImg;

    private Font font;
    private Label titleLabel;
    private Label titleLabelTwo;
    private Label winLabel;
    private Label loseLabel;

    private StackPane stackPane;
    private MenuBar topMenuBar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));
        Scene scene = new Scene(root, 400, 600);
        font = Font.loadFont(getClass().getResourceAsStream("/fonts/PixelFont.ttf"),16.0);
        titleLabel = (Label) scene.lookup("#titleLabel");
        if(titleLabel != null && font != null)
            titleLabel.setFont(font);
        titleLabelTwo = (Label) scene.lookup("#titleLabelTwo");
        if(titleLabelTwo != null && font != null)
            titleLabelTwo.setFont(font);

        statusLabel = (Label) scene.lookup("#statusLabel");
        actionButton = (Button) scene.lookup("#actionButton");
        skillComboBox = (ComboBox<String>) scene.lookup("#skillComboBox");
        slotOneComboBox = (ComboBox<String>) scene.lookup("#slotOneComboBox");
        slotTwoComboBox = (ComboBox<String>) scene.lookup("#slotTwoComboBox");
        slotThreeComboBox = (ComboBox<String>) scene.lookup("#slotThreeComboBox");

        confettiImg = (ImageView) scene.lookup("#confettiImg");
        if(confettiImg != null) {
            confettiImg.fitWidthProperty().bind(primaryStage.widthProperty());
            confettiImg.fitHeightProperty().bind(primaryStage.heightProperty());
        }
        megaToggle = (ToggleButton) scene.lookup("#megaToggle");
        runToggle = (ToggleButton) scene.lookup("#runToggle");

        winLabel = (Label) scene.lookup("#winText");
        loseLabel = (Label) scene.lookup("#loseText");

        if(slotOneComboBox != null && slotTwoComboBox != null && slotThreeComboBox != null)
            populateComboBoxes();

        if(actionButton != null)
            actionButton.setOnAction(e -> actionPressed());

        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);

        primaryStage.setTitle("Digimon RPG PvP Bot v1.0");
        primaryStage.getIcons().add(new Image("/images/gui_res/logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void populateComboBoxes() {
        skillComboBox.getItems().add("F1");
        skillComboBox.getItems().add("F2");
        skillComboBox.getItems().add("F3");

        slotOneComboBox.getItems().add("First Card");
        slotOneComboBox.getItems().add("Second Card");
        slotOneComboBox.getItems().add("Third Card");
        slotOneComboBox.getItems().add("Fourth Card");
        slotOneComboBox.getItems().add("Fifth Card");
        slotOneComboBox.getItems().add("Sixth Card");
        slotOneComboBox.getItems().add("None");

        slotTwoComboBox.getItems().add("First Card");
        slotTwoComboBox.getItems().add("Second Card");
        slotTwoComboBox.getItems().add("Third Card");
        slotTwoComboBox.getItems().add("Fourth Card");
        slotTwoComboBox.getItems().add("Fifth Card");
        slotTwoComboBox.getItems().add("Sixth Card");
        slotTwoComboBox.getItems().add("None");

        slotThreeComboBox.getItems().add("First Card");
        slotThreeComboBox.getItems().add("Second Card");
        slotThreeComboBox.getItems().add("Third Card");
        slotThreeComboBox.getItems().add("Fourth Card");
        slotThreeComboBox.getItems().add("Fifth Card");
        slotThreeComboBox.getItems().add("Sixth Card");
        slotThreeComboBox.getItems().add("None");
    }

    public void actionPressed() {
        if(actionButton.getText().equals("START ( / )")) {
            actionButton.setText("STOP ( / )");
            slotOneComboBox.setDisable(true);
            slotTwoComboBox.setDisable(true);
            slotThreeComboBox.setDisable(true);
            skillComboBox.setDisable(true);
            megaToggle.setDisable(true);
            runToggle.setDisable(true);
            setStatus("Starting ladder battle");
            try {
                robot = new PvpRobot(statusLabel, winLabel, loseLabel, confettiImg);
                robot.setSkillChoice(skillComboBox.getValue());
                robot.setSlotOneChoice(slotOneComboBox.getValue());
                robot.setSlotTwoChoice(slotTwoComboBox.getValue());
                robot.setSlotThreeChoice(slotThreeComboBox.getValue());
                robot.setMegaToggle(megaToggle.isSelected());
                robot.setRunToggle(runToggle.isSelected());

                //start the robot
                task = () -> robot.run();
                backgroundThread = new Thread(task);
                backgroundThread.setDaemon(true);
                backgroundThread.start();
            }catch(AWTException e) {
                setStatus("AWT Error");
            }
        }else if(actionButton.getText().equals("STOP ( / )")) {
            robot.interupt();
            backgroundThread.stop();
            task = null;
            backgroundThread = null;
            robot = null;

            actionButton.setText("START ( / )");
            slotOneComboBox.setDisable(false);
            slotTwoComboBox.setDisable(false);
            slotThreeComboBox.setDisable(false);
            skillComboBox.setDisable(false);
            megaToggle.setDisable(false);
            runToggle.setDisable(false);
            setStatus("Not running");
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if(e.getKeyCode() == NativeKeyEvent.VC_SLASH) {
            Platform.runLater(() -> actionPressed());
        }
    }
}
