package entity;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends Entity {

    GameScreen gameScreen;
    MazeRunnerGame mazeRunnerGame;

    public Player(GameScreen gameScreen, MazeRunnerGame mazeRunnerGame) {
        this.gameScreen = gameScreen;
        this.mazeRunnerGame = mazeRunnerGame;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    // we will implement images with package
   public void getPlayerImage() {
        try {
            up1 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/up1.png"));
            up2 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/up2.png"));
            up3 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/up3.png"));
            up4 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/up4.png"));
            down1 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/down1.png"));
            down2 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/down2.png"));
            down3 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/down3.png"));
            down4 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/down4.png"));
            left1 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
            left2 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
            left3 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/left3.png"));
            left4 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/left4.png"));
            right1 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
            right2 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
            right3 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/right3.png"));
            right4 = (ApplicationListener) ImageIO.read(getClass().getResourceAsStream("/player/right4.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void update() {
        if (mazeRunnerGame.upPressed == true) {
            direction = "up";
            y -= speed;
        }
        else if (mazeRunnerGame.downPressed == true) {
            direction = "down";
            y += speed;
        }
        else if (mazeRunnerGame.leftPressed == true) {
            direction = "left";
            x -= speed;
        }
        else if (mazeRunnerGame.rightPressed == true) {
            direction = "right";
            x += speed;
        }
    }
     public void draw(SpriteBatch g2) {


     }


}

