package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Door extends GameObject{
    private TextureRegion textureRegion;
    private float x, y;

    public Door(TextureRegion textureRegion, float x, float y, float width, float height) {
        super(textureRegion,x, y, width, height);
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(textureRegion, x, y, width, height);
    }
}

