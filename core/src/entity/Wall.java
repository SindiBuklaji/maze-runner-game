package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Wall {
    private float x;
    private float y;
    private Texture wallTexture;

    private int tileSize = 100;

    public Wall(float x, float y) {
        this.x = x;
        this.y = y;
        this.wallTexture = determineWallTexture();
    }

    private Texture determineWallTexture() {
        // Implement your logic to determine the wall texture based on the position (x, y)
        // You can use conditions or calculations to decide the appropriate texture

        if (x % tileSize == 0 && y % tileSize == 0) {
            return new Texture("wallup1.png"); // Adjust the file path based on your textures
        } else {
            return new Texture("wallup2.png"); // Another texture for different positions
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(wallTexture, x, y, tileSize, tileSize); // Adjust the size as needed
    }

    // Add more methods as needed based on your requirements
}
