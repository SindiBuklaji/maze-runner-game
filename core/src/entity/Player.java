package entity;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;
import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Player {

  //  GameScreen gameScreen;
    //MazeRunnerGame mazeRunnerGame;
    private float x;
    private float y;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;

    private float stateTime;

    public Player(float x, float y, Animation<TextureRegion> upAnimation, Animation<TextureRegion> downAnimation, Animation<TextureRegion> leftAnimation,
                  Animation<TextureRegion> rightAnimation) {
        this.x = x;
        this.y = y;
        this.upAnimation = upAnimation;
        this.downAnimation = downAnimation;
        this.leftAnimation = leftAnimation;
        this.rightAnimation = rightAnimation;
        this.stateTime = 0f;

    }

    public void update(float delta) {
        stateTime += delta; // Update the animation state time

        // Update player's position based on input or game logic
        // For simplicity, let's assume the player moves based on arrow keys.

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += 100 * delta; // Adjust the speed as needed
            currentFrame = upAnimation.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= 100 * delta; // Adjust the speed as needed
            currentFrame = downAnimation.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= 100 * delta; // Adjust the speed as needed
            currentFrame = leftAnimation.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += 100 * delta; // Adjust the speed as needed
            currentFrame = rightAnimation.getKeyFrame(stateTime, true);
        }

        // Add additional logic as needed for different movements or conditions.
    }

    // Render method to draw the player
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, x, y, 64, 64); // Adjust the size as needed
    }
}

