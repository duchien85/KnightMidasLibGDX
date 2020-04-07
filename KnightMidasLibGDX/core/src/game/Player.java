
package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import game.animations.AnimationHelper;
import game.animations.CustomAnimationBundle;
import game.animations.CustomAnimation;
import game.animations.CustomAnimationJsonReader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends GameObject implements Disposable {
    
    //Input
    private boolean up, left, right, down, attack, jump;
    
    //Logic
    private boolean isGrounded = false;
    
    //Physics
    protected Rectangle body, feet;
    protected List<Rectangle> parts;
    private float xSpeed, ySpeed, moveSpeed = 7.5f, jumpSpeed = 7.5f;
    private float gravity = -10f;
    
    //Render
    protected Sprite sprite;
    private Texture spritesheetTexture;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.HERO_IDLE;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;

    
    public Player() {
        
        createBody();
        createRender();
    }
    
    public void update(float dt) {
        
        //Input
        up = Gdx.input.isKeyPressed(Input.Keys.UP);
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        attack = Gdx.input.isKeyPressed(Input.Keys.X);
        jump = Gdx.input.isKeyPressed(Input.Keys.Z);
        
        
        //Logic
        if (left && right)
            left = right = false;
        
        if (!(right || left))
            actualState = PlayerState.HERO_IDLE;
        else
            actualState = PlayerState.HERO_WALK;
        
        
        //Physics
        xSpeed = moveSpeed;
        Vector2 futurePosition = new Vector2(0, 0);
        
        if (right) {
            futurePosition.x += xSpeed * dt;
            flipX = false;
        } else if (left) {
            futurePosition.x -= xSpeed * dt;
            flipX = true;
        }
        
        if (up && isGrounded) {
            isGrounded = false;
            ySpeed = jumpSpeed;
        }
        
        ySpeed += gravity * dt;
        futurePosition.y += ySpeed * dt;
        
        boolean bodyCollided = false;
        boolean feetCollided = false;
        Rectangle futureBodyPosition = new Rectangle(body.x + futurePosition.x, body.y + futurePosition.y,
            body.width, body.height);
        Rectangle futureFeetPosition = new Rectangle(feet.x + futurePosition.x, feet.y + futurePosition.y,
            feet.width, feet.height);
        for (Rectangle wall : actualLevel.walls) {
            
            if (wall.overlaps(futureBodyPosition)) bodyCollided = true;
            if (wall.overlaps(futureFeetPosition)) feetCollided = true;
            
            if (bodyCollided || feetCollided) break;
        }
        
        if (!bodyCollided) {
            body.x = futureBodyPosition.x;
            feet.x = futureFeetPosition.x;
        }
        
        if (!feetCollided) {
            body.y = futureBodyPosition.y;
            feet.y = futureFeetPosition.y;
        } else {
            isGrounded = true;
            ySpeed = 0;
        }
        
        
        //Render
        animationTimer += dt;
        actualRegion = animations.get(actualState).getKeyFrame(animationTimer);
        sprite.setRegion(actualRegion);
        sprite.setPosition(body.x, body.y);
        sprite.setFlip(flipX, flipY);
    }
    
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    
    
    public void createBody() {
        parts = new ArrayList<Rectangle>();
        
        body = new Rectangle(0, 9 + UnitHelper.pixelsToMeters(2), 4, 4);
        feet = new Rectangle(UnitHelper.pixelsToMeters(22), 9,
                UnitHelper.pixelsToMeters(20), UnitHelper.pixelsToMeters(2));
        
        parts.add(body);
        parts.add(feet);
    }
    
    public void createRender() {
        
        sprite = new Sprite();
        sprite.setBounds(0, 0, UnitHelper.pixelsToMeters(64), UnitHelper.pixelsToMeters(64));
        sprite.setScale(1, 1);
        
        spritesheetTexture = new Texture(StringPaths.texture_Hero);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Hero);
        CustomAnimation anim;
        
        for (PlayerState state : PlayerState.values()) {
            anim = bundle.getByName(state.getStateName());
            
            animations.put(state, new Animation(
                1f/anim.framesLength,
                AnimationHelper.getTextureRegions(anim.frames, spritesheetTexture),
                Animation.PlayMode.LOOP));
        }
        
    }
    
    
    @Override
    public void dispose() {
        spritesheetTexture.dispose();
    }
}
