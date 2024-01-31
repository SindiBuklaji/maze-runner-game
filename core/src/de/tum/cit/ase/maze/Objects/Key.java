package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends GameObject {
    private TextureRegion closedTextureRegion;
    private TextureRegion openTextureRegion;
    private float x, y;

    private boolean isOpen;

    public Key(TextureRegion closedTextureRegion, TextureRegion openTextureRegion, float x, float y, float width
            , float height) {
        super(openTextureRegion, x, y, width, height);
        this.closedTextureRegion = closedTextureRegion;
        this.openTextureRegion = openTextureRegion;
        this.x = x;
        this.y = y;

        this.isOpen = false;
    }

    public void render(SpriteBatch spriteBatch, boolean isKeyCollected) {
        if (isKeyCollected) {
            spriteBatch.draw(openTextureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
        } else {
            spriteBatch.draw(closedTextureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
        }
    }

}
