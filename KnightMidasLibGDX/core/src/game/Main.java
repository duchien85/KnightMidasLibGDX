package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {

    protected SpriteBatch batch;
    
    public static final float PPM = 16;
    public static final float PIXELS_PER_METER = 16;
    public static final float METERS_PER_PIXEL = 1 / PPM;
    
    public static final float DISPLAY_WIDTH = 30;
    public static final float DISPLAY_HEIGHT = 30;
    
    public static final float DISPLAY_PIXEL_WIDTH = DISPLAY_WIDTH * PPM;
    public static final float DISPLAY_PIXELS_HEIGHT = DISPLAY_HEIGHT * PPM;
    
    public static final float WORLD_WIDTH = 150;
    public static final float WORLD_HEIGHT = 30;
    
    public static final float GRAVITY = -10f;
    

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
