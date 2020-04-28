
package game;

import utils.Units;
import utils.StringPaths;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import game.animations.CustomAnimationHelper;
import game.animations.CustomAnimation;
import game.animations.CustomAnimationBundle;
import game.animations.CustomAnimationJsonReader;
import java.util.HashMap;

public class Snake extends GameObject implements Disposable {

    //Logic
    private boolean walkingLeft = true;
    private boolean tookDamage = false;
    protected boolean isAlive = true;
    
    //Health
    protected float health = 5f;
    protected float damage = 3f;
    protected float iFrames = 0;
    
    //Physics
    protected Rectangle body, spriteArea;
    protected Vector2 position;
    private float xSpeed, moveSpeed = 4f;
    private float gravity = Main.GRAVITY;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheet;
    private HashMap<SnakeState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected SnakeState actualState = SnakeState.SNAKE_WALK;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;
    
    
    public Snake(Level level, float posX, float posY) {
        super(level);
        createBody(posX, posY);
        createRender();
    }
    
    public void update(float dt) {
        
        //Health
        if (tookDamage)
            iFrames += dt;
        else
            iFrames = 0;
        
        if (iFrames >= 1f) {
            tookDamage = false;
            iFrames = 0;
            actualState = SnakeState.SNAKE_WALK;
        }
        
        
        //Physics
        xSpeed = moveSpeed;
        Vector2 futurePositionOffset = new Vector2(0, 0);
        
        if (!walkingLeft) {
            futurePositionOffset.x += xSpeed * dt;
            flipX = true;
        } else if (walkingLeft) {
            futurePositionOffset.x -= xSpeed * dt;
            flipX = false;
        }
        
        boolean bodyCollided = false;
        Rectangle futureBodyPosition = new Rectangle(
                body.x + futurePositionOffset.x,
                body.y + futurePositionOffset.y,
                body.width, body.height);
        for (Rectangle wall : actualLevel.walls) {
            
            if (futureBodyPosition.overlaps(wall)) bodyCollided = true;
            
            if (bodyCollided) break;
        }
        
        if (!bodyCollided) {
            position.x += futurePositionOffset.x;
            body.x += futurePositionOffset.x;
            spriteArea.x += futurePositionOffset.x;
        } else
            walkingLeft = !walkingLeft;
        
        
        //Render
        animationTimer += dt;
        actualRegion = animations.get(actualState).getKeyFrame(animationTimer);
        sprite.setRegion(actualRegion);
        sprite.setPosition(spriteArea.x, spriteArea.y);
        sprite.setFlip(flipX, flipY);
    }
    
    public void render(SpriteBatch batch) {
        if (health > 0)
            sprite.draw(batch);
    }
    
    
    public void getHurt(float damage) {
        if (!tookDamage) {
            health -= damage;
            actualState = SnakeState.SNAKE_HURT;
        }
        
        tookDamage = true;
        
        if (health < 0)
            isAlive = false;
    }
    
    public void createBody(float posX, float posY) {
        
        position = new Vector2(posX, posY);
        body = new Rectangle(posX + Units.pixelsMeters(11), posY,
            Units.pixelsMeters(36), Units.pixelsMeters(16));
        
        spriteArea = new Rectangle(posX, posY, 
                Units.pixelsMeters(spriteWidthPixels), Units.pixelsMeters(spriteHeightPixels));
        
    }
    
    public void createRender() {
        
        sprite = new Sprite();
        sprite.setBounds(spriteArea.x, spriteArea.y, spriteArea.width, spriteArea.height);
        sprite.setScale(1, 1);
        
        spritesheet = new Texture(StringPaths.texture_Snake);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Snake);
        CustomAnimation anim;
        
        for (SnakeState state : SnakeState.values()) {
            anim = bundle.getByName(state.getStateName());
            
            animations.put(state, new Animation(
                1f/anim.time,
                CustomAnimationHelper.getTextureRegions(anim.frames, spritesheet),
                CustomAnimationHelper.getPlayMode(anim.playMode)));
        }
    }
    
    
    @Override
    public void dispose() {
        spritesheet.dispose();
    }
}
