
package game;

import game.animations.PlayerState;
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
    private boolean isAttacking = false;
    
    //Physics
    protected Rectangle body, feet, spriteArea, mainHurtbox, swordHitbox;
    protected Vector2 position;
    protected List<Rectangle> parts;
    private float xSpeed, ySpeed, moveSpeed = 7.5f, jumpSpeed = 7.5f;
    private float gravity = Main.GRAVITY;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheetTexture;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.HERO_IDLE;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;

    
    public Player(float posX, float posY) {
        
        createBody(posX, posY);
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
        
        isAttacking = attack;
        
        if (attack)
            actualState = PlayerState.HERO_STAB;
        else if (!(right || left))
            actualState = PlayerState.HERO_IDLE;
        else
            actualState = PlayerState.HERO_WALK;
        
        
        //Physics
        xSpeed = moveSpeed;
        Vector2 futurePositionOffset = new Vector2(0, 0);
        
        if (right) {
            futurePositionOffset.x += xSpeed * dt;
            flipX = false;
        } else if (left) {
            futurePositionOffset.x -= xSpeed * dt;
            flipX = true;
        }
        
        if (up && isGrounded) {
            isGrounded = false;
            ySpeed = jumpSpeed;
        }
        
        ySpeed += gravity * dt;
        futurePositionOffset.y += ySpeed * dt;
        
        boolean bodyCollided = false;
        boolean feetCollided = false;
        Rectangle futureBodyPosition = new Rectangle(
                body.x + futurePositionOffset.x, body.y + futurePositionOffset.y,
                body.width, body.height);
        
        Rectangle futureFeetPosition = new Rectangle(
                feet.x + futurePositionOffset.x, feet.y + futurePositionOffset.y,
                feet.width, feet.height);
        
        Rectangle futureSwordPosition = new Rectangle(
                swordHitbox.x + futurePositionOffset.x, swordHitbox.y + futurePositionOffset.y,
                swordHitbox.width, swordHitbox.height);
        
        for (Rectangle wall : actualLevel.walls) {
            if (futureBodyPosition.overlaps(wall)) bodyCollided = true;
            if (futureFeetPosition.overlaps(wall)) feetCollided = true;
            
            if (bodyCollided && feetCollided) break;
        }
        
        for (Snake snake : actualLevel.snakes) {
            if (isAttacking && futureSwordPosition.overlaps(snake.body)) {
                System.out.println("Acertou a cobra!!");
            }
        }
        
        if (!bodyCollided) {
            position.x += futurePositionOffset.x;
            body.x += futurePositionOffset.x;
            feet.x += futurePositionOffset.x;
            spriteArea.x += futurePositionOffset.x;
            mainHurtbox.x += futurePositionOffset.x;
            swordHitbox.x += futurePositionOffset.x;
        }
        
        if (!feetCollided) {
            position.y += futurePositionOffset.y;
            body.y += futurePositionOffset.y;
            feet.y += futurePositionOffset.y;
            spriteArea.y += futurePositionOffset.y;
            mainHurtbox.y += futurePositionOffset.y;
            swordHitbox.y += futurePositionOffset.y;
            
            isGrounded = false;
            
        } else {
            isGrounded = true;
            ySpeed = 0;
        }
        
        
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
        parts = new ArrayList<Rectangle>();
        
        position = new Vector2(posX, posY);
        body = new Rectangle(posX + UnitHelper.pixelsToMeters(24), posY + UnitHelper.pixelsToMeters(1),
                UnitHelper.pixelsToMeters(16), UnitHelper.pixelsToMeters(24));
        
        feet = new Rectangle(posX + UnitHelper.pixelsToMeters(25), posY,
                UnitHelper.pixelsToMeters(14), UnitHelper.pixelsToMeters(1));
        
        spriteArea = new Rectangle(posX, posY, 
                UnitHelper.pixelsToMeters(spriteWidthPixels), UnitHelper.pixelsToMeters(spriteHeightPixels));
        
        mainHurtbox = new Rectangle(posX + UnitHelper.pixelsToMeters(22), posY + UnitHelper.pixelsToMeters(2),
                UnitHelper.pixelsToMeters(20), UnitHelper.pixelsToMeters(23));
        
        swordHitbox = new Rectangle(posX + UnitHelper.pixelsToMeters(44), posY + UnitHelper.pixelsToMeters(1),
                UnitHelper.pixelsToMeters(19), UnitHelper.pixelsToMeters(8));
        
        parts.add(body);
        parts.add(feet);
        parts.add(spriteArea);
        parts.add(mainHurtbox);
        parts.add(swordHitbox);
    }
    
    public void createRender() {
        
        sprite = new Sprite();
        sprite.setBounds(spriteArea.x, spriteArea.y, spriteArea.width, spriteArea.height);
        sprite.setScale(1, 1);
        
        spritesheetTexture = new Texture(StringPaths.texture_Hero);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Hero);
        CustomAnimation anim;
        
        for (PlayerState state : PlayerState.values()) {
            anim = bundle.getByName(state.getStateName());
            
            animations.put(state, new Animation(
                1f/anim.time,
                AnimationHelper.getTextureRegions(anim.frames, spritesheetTexture),
                AnimationHelper.getPlayMode(anim.playMode)));
        }
    }
    
    
    @Override
    public void dispose() {
        spritesheetTexture.dispose();
    }
}
