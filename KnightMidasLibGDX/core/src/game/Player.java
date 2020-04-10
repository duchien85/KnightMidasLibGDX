
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
    private boolean tookDamage = false;
    private boolean isSpawning = true;
    protected float iFrames = 0;
    
    //Health
    private float health = 20f;
    private float swordDamage = 6f;
    
    //Physics
    protected Vector2 position;
    protected Rectangle body, feet, spriteArea, mainHurtbox, swordHitbox;
    protected List<Rectangle> parts;
    private float xSpeed, ySpeed,
            moveSpeed = 7.5f, jumpSpeed = 7.5f,
            knockbackSpeedX = 3f, knockbackSpeedY = 2f;
    private float gravity = Main.GRAVITY;
    private Vector2 futurePositionOffset;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheet;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.HERO_IDLE;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;

    
    public Player(float posX, float posY) {
        createBodies(posX, posY);
        createAnimations();
    }
    
    public void update(float dt) {
        
        getInput();
        
        iFrames(dt);
        
        changeState();
        
        physics(dt);
        
        collisions();
        
        sprite(dt);
    }
    
    public void render(SpriteBatch batch) {
        if (health > 0)
            sprite.draw(batch);
    }
    
    
    private void getInput() {
        
        up = Gdx.input.isKeyPressed(Input.Keys.UP);
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        attack = Gdx.input.isKeyPressed(Input.Keys.X);
        jump = Gdx.input.isKeyPressed(Input.Keys.Z);
        
        if (left && right)
            left = right = false;
        
        if ((up && down) || down)
            up = false;
    }
    
    private void iFrames(float dt) {
        
        if (tookDamage)
            iFrames += dt;
        else
            iFrames = 0;
        
        if (iFrames >= 1f) {
            iFrames = 0;
            tookDamage = false;
        }
    }
    
    private void changeState() {
        PlayerState previousState = actualState;
        
        if (isSpawning) {
            actualState = PlayerState.HERO_SPAWN;
            if (animations.get(actualState).isAnimationFinished(animationTimer))
                isSpawning = false;
        }
        else if (tookDamage && iFrames > 0) {
            actualState = PlayerState.HERO_HURT;
            if (iFrames < 0.3f)
                up = right = down = left = false;
        }
        else if (down)
            actualState = PlayerState.HERO_DUCK;
        else if (!isGrounded)
            actualState = PlayerState.HERO_JUMP;
        else if (attack)
            actualState = PlayerState.HERO_STAB;
        else if (right || left)
            actualState = PlayerState.HERO_WALK;
        else
            actualState = PlayerState.HERO_IDLE;
        
        if (actualState != previousState)
            animationTimer = 0;
    }
    
    private void physics(float dt) {
        
        if (!tookDamage)
            xSpeed = moveSpeed;
        else if (iFrames < 0.1f)
            xSpeed = knockbackSpeedX;
        futurePositionOffset = new Vector2(0, 0);
        
        
        if (tookDamage) {
            futurePositionOffset.x += xSpeed * dt;
        } else if (right) {
            if (!down)
                futurePositionOffset.x += xSpeed * dt;
            flipX = false;
        } else if (left) {
            if (!down)
                futurePositionOffset.x -= xSpeed * dt;
            flipX = true;
        }
        
        
        if (tookDamage) {
            ySpeed = knockbackSpeedY;
        } else if (up && isGrounded) {
            isGrounded = false;
            ySpeed = jumpSpeed;
        }
        
        ySpeed += gravity * dt;
        futurePositionOffset.y += ySpeed * dt;
    }
    
    private void collisions() {
        
        boolean bodyCollided = false;
        boolean feetCollided = false;
        Rectangle futureBodyPosition = new Rectangle(
                body.x + futurePositionOffset.x, body.y + futurePositionOffset.y,
                body.width, body.height);
        
        Rectangle futureFeetPosition = new Rectangle(
                feet.x + futurePositionOffset.x, feet.y + futurePositionOffset.y,
                feet.width, feet.height);
        
        Rectangle futureHurtboxPosition = new Rectangle(
                mainHurtbox.x + futurePositionOffset.x, mainHurtbox.y + futurePositionOffset.y,
                mainHurtbox.width, mainHurtbox.height);
        
        Rectangle futureSwordPosition = new Rectangle(
                swordHitbox.x + futurePositionOffset.x, swordHitbox.y + futurePositionOffset.y,
                swordHitbox.width, swordHitbox.height);
        
        for (Rectangle wall : actualLevel.walls) {
            if (futureBodyPosition.overlaps(wall)) bodyCollided = true;
            if (futureFeetPosition.overlaps(wall)) feetCollided = true;
            
            if (bodyCollided && feetCollided) break;
        }
        
        for (Snake snake : actualLevel.snakes) {
            if (attack && futureSwordPosition.overlaps(snake.body)) {
                if (snake.iFrames == 0)
                    snake.getHurt(swordDamage);
            }
            
            if (futureHurtboxPosition.overlaps(snake.body)) {
                if (iFrames == 0)
                    getHurt(snake.damage);
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
    }
    
    private void sprite(float dt) {
        
        animationTimer += dt;
        actualRegion = animations.get(actualState).getKeyFrame(animationTimer);
        sprite.setRegion(actualRegion);
        sprite.setPosition(spriteArea.x, spriteArea.y);
        sprite.setFlip(flipX, flipY);
    }
    
    
    private void getHurt(float damage) {
        actualState = PlayerState.HERO_HURT;
        health -= damage;
        tookDamage = true;
    }
    
    
    public void createBodies(float posX, float posY) {
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
    
    public void createAnimations() {
        
        sprite = new Sprite();
        sprite.setBounds(spriteArea.x, spriteArea.y, spriteArea.width, spriteArea.height);
        sprite.setScale(1, 1);
        
        spritesheet = new Texture(StringPaths.texture_Hero);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Hero);
        CustomAnimation anim;
        
        for (PlayerState state : PlayerState.values()) {
            anim = bundle.getByName(state.getStateName());
            
            animations.put(state, new Animation(
                1f/anim.time,
                AnimationHelper.getTextureRegions(anim.frames, spritesheet),
                AnimationHelper.getPlayMode(anim.playMode)));
        }
    }
    
    @Override
    public void dispose() {
        spritesheet.dispose();
    }
}
