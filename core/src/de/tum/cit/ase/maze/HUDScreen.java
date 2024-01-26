package de.tum.cit.ase.maze;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HUDScreen {

    private final Camera camera;
    private final BitmapFont font;
    private int livesRemaining;
    private boolean keyCollected;
    private final SpriteBatch hudSpriteBatch;

    public HUDScreen (Skin skin, SpriteBatch hudSpriteBatch) {
        this.camera = new OrthographicCamera();
        this.font = skin.getFont("font");  // Adjust this based on your skin setup
        this.hudSpriteBatch = hudSpriteBatch;
    }

    public void update(int livesRemaining, boolean keyCollected) {
        this.livesRemaining = livesRemaining;
        this.keyCollected = keyCollected;
    }

    public Camera getCamera() {
        return camera;
    }

    public void render(SpriteBatch spriteBatch) {
        // You can adjust the positioning based on your preference
        float x = 10;
        float y = camera.viewportHeight - 10;

        // Draw lives remaining
        font.draw(spriteBatch, "Lives: " + livesRemaining, x, y);

        // Draw key collected status
        String keyStatus = keyCollected ? "Key Collected" : "Key Not Collected";
        font.draw(spriteBatch, keyStatus, x, y - 20);
    }
}