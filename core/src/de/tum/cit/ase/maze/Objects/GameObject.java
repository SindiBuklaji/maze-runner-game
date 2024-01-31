package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameObject {

    // Position Attributes
    float x;
    float y;
    float width;
    float height;
    TextureRegion textureRegion;
    float tileSize;

    public GameObject(TextureRegion textureRegion, float x, float y, float width, float height) {
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileSize = 68f;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
