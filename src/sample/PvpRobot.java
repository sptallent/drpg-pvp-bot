package sample;

import com.github.jaiimageio.impl.common.ImageUtil;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PvpRobot extends Robot implements Runnable {

    private Label statusL;
    private Label winLabel;
    private Label loseLabel;
    private ImageView confettiImg;
    private sample.ConfettiRunnable confettiRunnable;
    private Thread confettiThread;

    private BufferedImage screen;
    private BufferedImage f1;
    private BufferedImage f2;
    private BufferedImage f3;
    private BufferedImage win;
    private BufferedImage lose;
    private BufferedImage card_slash;
    private BufferedImage ladder_battle;
    private BufferedImage attack;
    private BufferedImage attack2;
    private BufferedImage close;
    private BufferedImage run_away;
    private BufferedImage evp_bar;
    private BufferedImage drpg_text;
    private boolean running = false;
    private final URL f1_url = ImageUtil.class.getResource("/images/recognition/f1.png");
    private final URL f2_url = ImageUtil.class.getResource("/images/recognition/f2.png");
    private final URL f3_url = ImageUtil.class.getResource("/images/recognition/f3.png");
    private final URL win_url = ImageUtil.class.getResource("/images/recognition/win.png");
    private final URL lose_url = ImageUtil.class.getResource("/images/recognition/lose.png");
    private final URL close_url = ImageUtil.class.getResource("/images/recognition/close.png");
    private final URL run_away_url = ImageUtil.class.getResource("/images/recognition/run_away.png");
    private final URL card_slots_url = ImageUtil.class.getResource("/images/recognition/card_slots.png");
    private final URL attack_url = ImageUtil.class.getResource("/images/recognition/attack.png");
    private final URL attack2_url = ImageUtil.class.getResource("/images/recognition/attack2.png");
    private final URL ladder_battle_url = ImageUtil.class.getResource("/images/recognition/ladder_battle.png");
    private final URL card_slash_url = ImageUtil.class.getResource("/images/recognition/card_slash.png");
    private final URL evp_bar_url = ImageUtil.class.getResource("/images/recognition/evp_bar.png");
    private final URL drpg_text_url = ImageUtil.class.getResource("/images/recognition/drpg_text.png");

    private Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    //private final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final int width = (int)screenBounds.getWidth();
    private final int height = (int)screenBounds.getHeight();

    private String skillChoice = "";
    private String slotOneChoice = "";
    private String slotTwoChoice = "";
    private String slotThreeChoice = "";
    private boolean megaChoice = true;
    private boolean runChoice = false;
    private boolean isTop = true;//top = true, bot = false

    private int winCount = 0;
    private int loseCount = 0;

    private boolean battleEnded = false;

    private Rectangle rect;

    private int x_off = 0;
    private int y_off = 0;

    public PvpRobot(Label l, Label winL, Label loseL, ImageView i) throws AWTException {
        statusL = l;
        confettiImg = i;
        confettiRunnable = new sample.ConfettiRunnable(confettiImg);
        winLabel = winL;
        loseLabel = loseL;
        rect = new Rectangle(width, height);
    }

    public void updateScreen() {
        screen = createScreenCapture(rect);//stackoverflow

        /*Point offsetPoint = match(drpg_text, screen);
        if(offsetPoint != null) {
            x_off = offsetPoint.x;
            y_off = offsetPoint.y;
            screen = screen.getSubimage(offsetPoint.x, offsetPoint.y, 798, 630);//width-height of drpg window
        }*/

        if(match(win, screen) != null && !battleEnded) {
            winCount++;
            battleEnded = true;
            updateStatus("Battle won!");
            Platform.runLater(() -> {
                confettiImg.setVisible(true);
                confettiThread = new Thread(confettiRunnable);
                confettiThread.setDaemon(true);
                confettiThread.start();
            });
        }
        if(match(lose, screen) != null && !battleEnded) {
            loseCount++;
            battleEnded = true;
            updateStatus("The battle was a loss :(");
        }
    }


    public void updateStatus(String status) {
        Platform.runLater(() -> {
            statusL.setText(status);
            if(Integer.parseInt(winLabel.getText()) != winCount)
                winLabel.setText(String.valueOf(winCount));
            if(Integer.parseInt(loseLabel.getText()) != loseCount)
                loseLabel.setText(String.valueOf(loseCount));
        });
    }

    public static Point match(BufferedImage subimage, BufferedImage image) {
        // brute force N^2 check all places in the image
        for (int i = 0; i <= image.getWidth() - subimage.getWidth(); i++) {
            check_subimage:
            for (int j = 0; j <= image.getHeight() - subimage.getHeight(); j++) {
                for (int ii = 0; ii < subimage.getWidth(); ii++) {
                    for (int jj = 0; jj < subimage.getHeight(); jj++) {
                        if (subimage.getRGB(ii, jj) != image.getRGB(i + ii, j + jj)) {
                            continue check_subimage;
                        }
                    }
                }
                // if here, all pixels matched
                return new Point(i, j);
            }
        }
        return null;
    }

    public void findLadderBattle() {
        updateScreen();
        Point closePoint = match(close, screen);
        if(closePoint != null) {
            updateStatus("Closing results");
            mouseMove((int)closePoint.getX()+5+x_off, (int)closePoint.getY()+5+y_off);
            delay(200);
            mousePress(KeyEvent.BUTTON1_DOWN_MASK);
            delay(200);
            mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
            delay(200);
            mouseMove((int)closePoint.getX()-200+x_off, (int)closePoint.getY()+y_off);
            delay(500);
        }
        updateScreen();
        Point p = match(ladder_battle, screen);
        if(running) {
            if (p != null) {
                updateStatus("Attempting to start battle");
                mouseMove((int) p.getX()+10+x_off, (int) p.getY()+10+y_off);
                startBattle();
            } else {
                delay(500);
                findLadderBattle();
            }
        }
    }

    public void startBattle() {
        mousePress(KeyEvent.BUTTON1_DOWN_MASK);
        delay(200);
        mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
        delay(200);
        mousePress(KeyEvent.BUTTON1_DOWN_MASK);
        delay(200);
        mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
        delay(200);

        updateScreen();
        while(match(card_slash, screen) == null) {
            updateStatus("Waiting for battle to start");
            delay(200);
            updateScreen();
        }
        if(running) {
            updateStatus("Battle started");
            megaEvolution();
            prepareCards();
            chooseAttack();
            checkTopBottom();
            attackOpponent();
        }
    }

    public void attackOpponent() {
        updateScreen();
        Point p = match(card_slash, screen);
        while(running && p != null) {
            //we are in a match
            Point attackPoint = match(attack, screen);
            Point attack2Point = match(attack2, screen);
            if (runChoice) {
                Point rp = match(run_away, screen);
                if (rp != null) {
                    updateStatus("Running away from battle");
                    mouseMove(((int) rp.getX() + (run_away.getWidth() / 2) + x_off), ((int) rp.getY() + (run_away.getHeight() / 2) + y_off));
                    mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(100);
                    mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(10000);
                }
            } else if(attackPoint == null && attack2Point == null && !battleEnded) {
                updateStatus("Preparing for attack");

                if (!isTop) {
                    //attack top side
                    //try attack top side
                    mouseMove((int) p.getX() + 192 + x_off, (int) p.getY() + 170 + y_off);
                    delay(100);
                    mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(100);
                    mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(200);
                    updateScreen();
                    attackPoint = match(attack, screen);
                    attack2Point = match(attack2, screen);
                    if(attackPoint == null && attack2Point == null && !battleEnded) {
                        //try attack top left side alternatives
                        mouseMove((int) p.getX() + 64 + x_off, (int) p.getY() + 238 + y_off);
                        delay(100);
                        mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                        delay(100);
                        mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                        delay(200);
                        updateScreen();
                        attackPoint = match(attack, screen);
                        attack2Point = match(attack2, screen);
                        if(attackPoint == null && attack2Point == null && !battleEnded) {
                            //try attack top right side alternative 128 x 68
                            mouseMove((int) p.getX() + 320 + x_off, (int) p.getY() + 102 + y_off);
                            delay(100);
                            mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                            delay(100);
                            mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                } else {
                    //try attack bottom side
                    mouseMove((int) p.getX() + 540 + x_off, (int) p.getY() + 340 + y_off);
                    delay(100);
                    mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(100);
                    mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                    delay(200);
                    updateScreen();
                    attackPoint = match(attack, screen);
                    attack2Point = match(attack2, screen);
                    if(attackPoint == null && attack2Point == null && !battleEnded) {
                        //try attack bottom right side alternative
                        mouseMove((int) p.getX() + 668 + x_off, (int) p.getY() + 272 + y_off);
                        delay(100);
                        mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                        delay(100);
                        mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                        delay(200);
                        updateScreen();
                        attackPoint = match(attack, screen);
                        attack2Point = match(attack2, screen);
                        if(attackPoint == null && attack2Point == null && !battleEnded) {
                            //try attack bottom left side alternatives
                            mouseMove((int) p.getX() + 412 + x_off, (int) p.getY() + 408 + y_off);
                            delay(100);
                            mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                            delay(100);
                            mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                }
            }
            updateStatus("Waiting");
            updateScreen();
            p = match(card_slash, screen);
        }
        battleEnded = false;
        findLadderBattle();
    }

    public void megaEvolution() {
        if(megaChoice) {
            updateStatus("Mega Evolution");
            keyPress(KeyEvent.VK_SHIFT);
            keyPress(KeyEvent.VK_Z);
            delay(100);
            keyRelease(KeyEvent.VK_SHIFT);
            keyRelease(KeyEvent.VK_Z);
        }
    }

    public void prepareCards() {
        if(slotOneChoice != null || slotTwoChoice != null || slotThreeChoice != null || !slotOneChoice.equals("None") || !slotTwoChoice.equals("None") || !slotThreeChoice.equals("None")) {
            updateStatus("Preparing Cards");

            //Pick the first card slot
            if (slotOneChoice != null && !slotOneChoice.equals("None")) {
                delay(100);
                keyPress(KeyEvent.VK_1);
                delay(100);
                keyRelease(KeyEvent.VK_1);
                delay(100);
                switch (slotOneChoice) {
                    case "First Card":
                        keyPress(KeyEvent.VK_1);
                        delay(100);
                        keyRelease(KeyEvent.VK_1);
                        delay(100);
                        break;
                    case "Second Card":
                        keyPress(KeyEvent.VK_2);
                        delay(100);
                        keyRelease(KeyEvent.VK_2);
                        delay(100);
                        break;
                    case "Third Card":
                        keyPress(KeyEvent.VK_3);
                        delay(100);
                        keyRelease(KeyEvent.VK_3);
                        delay(100);
                        break;
                    case "Fourth Card":
                        keyPress(KeyEvent.VK_4);
                        delay(100);
                        keyRelease(KeyEvent.VK_4);
                        delay(100);
                        break;
                    case "Fifth Card":
                        keyPress(KeyEvent.VK_5);
                        delay(100);
                        keyRelease(KeyEvent.VK_5);
                        delay(100);
                        break;
                    case "Sixth Card":
                        keyPress(KeyEvent.VK_6);
                        delay(100);
                        keyRelease(KeyEvent.VK_6);
                        delay(100);
                        break;
                    default:
                    /*keyPress(KeyEvent.VK_1);
                    delay(100);
                    keyRelease(KeyEvent.VK_1);
                    delay(100);
                    break;*/
                }
            }

            if (slotTwoChoice != null && !slotTwoChoice.equals("None")) {
                //Pick the second card slot
                keyPress(KeyEvent.VK_2);
                delay(100);
                keyRelease(KeyEvent.VK_2);
                delay(100);
                switch (slotTwoChoice) {
                    case "First Card":
                        keyPress(KeyEvent.VK_1);
                        delay(100);
                        keyRelease(KeyEvent.VK_1);
                        delay(100);
                        break;
                    case "Second Card":
                        keyPress(KeyEvent.VK_2);
                        delay(100);
                        keyRelease(KeyEvent.VK_2);
                        delay(100);
                        break;
                    case "Third Card":
                        keyPress(KeyEvent.VK_3);
                        delay(100);
                        keyRelease(KeyEvent.VK_3);
                        break;
                    case "Fourth Card":
                        keyPress(KeyEvent.VK_4);
                        delay(100);
                        keyRelease(KeyEvent.VK_4);
                        delay(100);
                        break;
                    case "Fifth Card":
                        keyPress(KeyEvent.VK_5);
                        delay(100);
                        keyRelease(KeyEvent.VK_5);
                        delay(100);
                        break;
                    case "Sixth Card":
                        keyPress(KeyEvent.VK_6);
                        delay(100);
                        keyRelease(KeyEvent.VK_6);
                        delay(100);
                        break;
                    default:
                    /*keyPress(KeyEvent.VK_1);
                    delay(100);
                    keyRelease(KeyEvent.VK_1);
                    delay(100);*/
                        break;
                }
            }

            if (slotThreeChoice != null && !slotThreeChoice.equals("None")) {
                //Pick the second card slot
                keyPress(KeyEvent.VK_3);
                delay(100);
                keyRelease(KeyEvent.VK_3);
                delay(100);
                switch (slotThreeChoice) {
                    case "First Card":
                        keyPress(KeyEvent.VK_1);
                        delay(100);
                        keyRelease(KeyEvent.VK_1);
                        delay(100);
                        break;
                    case "Second Card":
                        keyPress(KeyEvent.VK_2);
                        delay(100);
                        keyRelease(KeyEvent.VK_2);
                        delay(100);
                        break;
                    case "Third Card":
                        keyPress(KeyEvent.VK_3);
                        delay(100);
                        keyRelease(KeyEvent.VK_3);
                        delay(100);
                        break;
                    case "Fourth Card":
                        keyPress(KeyEvent.VK_4);
                        delay(100);
                        keyRelease(KeyEvent.VK_4);
                        delay(100);
                        break;
                    case "Fifth Card":
                        keyPress(KeyEvent.VK_5);
                        delay(100);
                        keyRelease(KeyEvent.VK_5);
                        delay(100);
                        break;
                    case "Sixth Card":
                        keyPress(KeyEvent.VK_6);
                        delay(100);
                        keyRelease(KeyEvent.VK_6);
                        delay(100);
                        break;
                    default:
                    /*keyPress(KeyEvent.VK_1);
                    delay(100);
                    keyRelease(KeyEvent.VK_1);
                    delay(100);*/
                        break;
                }
            }
        }
    }

    public void chooseAttack() {
        //Press the function key for the selected skill
        switch (skillChoice) {
            case "F1":
                keyPress(KeyEvent.VK_F1);
                delay(100);
                keyRelease(KeyEvent.VK_F1);
                break;
            case "F2":
                keyPress(KeyEvent.VK_F2);
                delay(100);
                keyRelease(KeyEvent.VK_F2);
                break;
            case "F3":
                keyPress(KeyEvent.VK_F3);
                delay(100);
                keyRelease(KeyEvent.VK_F3);
                break;
            default:
                break;
        }
    }

    public void checkTopBottom() {
        delay(100);
        updateScreen();
        Point topBottom = match(evp_bar, screen);
        if(topBottom != null) {
            if (topBottom.getY() > (screen.getHeight() / 2))
                isTop = false;
            else
                isTop = true;
        }else{
            delay(100);
            checkTopBottom();
        }
    }

    public void setSlotOneChoice(String s) {
        slotOneChoice = s;
    }

    public void setSlotTwoChoice(String s) {
        slotTwoChoice = s;
    }

    public void setSkillChoice(String s) {
        skillChoice = s;
    }

    public void setSlotThreeChoice(String s) {
        slotThreeChoice = s;
    }

    public void setRunToggle(boolean b) {
        runChoice = b;
    }

    public void setMegaToggle(boolean b) {
        megaChoice = b;
    }

    public void interupt() {
        running = false;
    }

    @Override
    public void run() {
        try {
            close = ImageIO.read(close_url);
            card_slash = ImageIO.read(card_slash_url);
            ladder_battle = ImageIO.read(ladder_battle_url);
            win = ImageIO.read(win_url);
            lose = ImageIO.read(lose_url);
            f1 = ImageIO.read(f1_url);
            f2 = ImageIO.read(f2_url);
            f3 = ImageIO.read(f3_url);
            attack = ImageIO.read(attack_url);
            attack2 = ImageIO.read(attack2_url);
            run_away = ImageIO.read(run_away_url);
            evp_bar = ImageIO.read(evp_bar_url);
            drpg_text = ImageIO.read(drpg_text_url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        running = true;
        while (running) {
            updateScreen();
            battleEnded = false;
            if (match(card_slash, screen) == null)
                findLadderBattle();
            else
                attackOpponent();
        }
    }

}
