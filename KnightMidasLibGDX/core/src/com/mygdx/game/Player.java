
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import engine.physics.DynamicBody;
import engine.physics.Physics;

public class Player implements Updatable, Renderable, Disposable {
    
    protected Sprite sprite;
    private Texture img;
    private TextureRegion region;
    
    protected DynamicBody body;

    public Player() {
        img = new Texture("sheet_hero_idle.png");
        region = new TextureRegion(img, 0, 0, 64, 64);
        
        sprite = new Sprite();
        sprite.setRegion(region);
        sprite.setSize(region.getRegionWidth(), region.getRegionHeight());
        sprite.setScale(2, 2);
    }
    
    @Override
    public void update(float dt) {
        sprite.setPosition(body.getX(), body.getY());
    }
    
    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    
    public void createBody(Physics physics, float x, float y, float width, float height) {
        body = new DynamicBody(x, y, width, height);
        physics.addDynamicBody(body);
    }
    
    @Override
    public void dispose() {
        img.dispose();
    }
}
