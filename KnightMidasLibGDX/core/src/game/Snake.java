
package game;

import game.animations.SnakeState;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import game.animations.AnimationHelper;
import game.animations.CustomAnimation;
import game.animations.CustomAnimationBundle;
import game.animations.CustomAnimationJsonReader;
import java.util.HashMap;
import java.util.List;

public class Snake extends GameObject implements Disposable {

    //Logic
    private boolean walkingLeft = true;
    
    //Physics
    protected Rectangle body, spriteArea;
    protected Vector2 position;
    private float xSpeed, moveSpeed = 5f;
    private float gravity = Main.GRAVITY;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheetTexture;
    private HashMap<SnakeState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected SnakeState actualState = SnakeState.SNAKE_WALK;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;
    
    
    public Snake(float posX, float posY) {
        createBody(posX, posY);
        createRender();
    }
    
    public void update(float dt) {
        
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
        sprite.draw(batch);
    }
    
    
    public void createBody(float posX, float posY) {
        
        position = new Vector2(posX, posY);
        body = new Rectangle(posX + UnitHelper.pixelsToMeters(11), posY,
            UnitHelper.pixelsToMeters(42), UnitHelper.pixelsToMeters(21));
        
        spriteArea = new Rectangle(posX, posY, 
                UnitHelper.pixelsToMeters(spriteWidthPixels), UnitHelper.pixelsToMeters(spriteHeightPixels));
        
    }
    
    public void createRender() {
        
        sprite = new Sprite();
        sprite.setBounds(spriteArea.x, spriteArea.y, spriteArea.width, spriteArea.height);
        sprite.setScale(1, 1);
        
        spritesheetTexture = new Texture(StringPaths.texture_Snake);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Snake);
        CustomAnimation anim;
        
        for (SnakeState state : SnakeState.values()) {
            anim = bundle.getByName(state.getStateName());
            
            animations.put(state, new Animation(
                1f/anim.time,
                AnimationHelper.getTextureRegions(anim.frames, spritesheetTexture),
                AnimationHelper.getPlayMode(anim.playMode)));
        }
    }
    
    
    @Override
    public void dispose() {
        
    }
}
