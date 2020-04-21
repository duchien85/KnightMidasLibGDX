
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
    protected boolean left, right, down, attack, jump;
    
    //Logic
    protected boolean isJumping = false;
    protected boolean canJump = true;
    protected boolean smallJump = false;
    private boolean tookDamage = false;
    private boolean isSpawning = true;
    private boolean hasExitKey = false;
    private boolean finishedLevel = false;
    protected float iFrames = 0;
    
    protected boolean bodyTopCollided = false,bodyLeftCollided = false,
                bodyRightCollided = false, feetBottomCollided = false;
    
    //Health
    protected float health = 20f;
    private float swordDamage = 6f;
    
    //Physics
    protected Vector2 position;
    protected Rectangle body, feet, spriteArea, mainHurtbox, swordHitbox;
    protected List<Rectangle> parts;
    private Vector2 futurePositionOffset;
    protected float xSpeed, ySpeed;
    
    private float jumpHeight = 6f, jumpHalfDurationTime = 0.5f,
            timeToRunSpeed = 6 / 30f;
    private float walkSpeed = 5f, runSpeed = 7.5f;
    private Vector2 knockbackSpeed = new Vector2(3f, 2f);
    private float walkTimer = 0f, jumpTimer = 1f;
    
    private float jumpSpeed, gravity, xAcceleration;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheet;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.IDLE;
    private boolean flipX = false, flipY = false;
    private float animationTimer = 0;

    
    public Player(Level level, float posX, float posY) {
        super(level);
        createBodies(posX, posY);
        createAnimations();
        
        gravity = (-2*jumpHeight) / (jumpHalfDurationTime * jumpHalfDurationTime);
        jumpSpeed = 2 * jumpHeight / jumpHalfDurationTime;
        xAcceleration = runSpeed / timeToRunSpeed;
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
        
        right = down = left = attack = jump = false;
        
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        attack = Gdx.input.isKeyPressed(Input.Keys.X);
        jump = Gdx.input.isKeyPressed(Input.Keys.Z);
        
        if (left && right)
            left = right = false;
        
        if ((jump && down) || down)
            jump = false;
    }
    
    private void iFrames(float dt) {
        
        //iFrames
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
            actualState = PlayerState.SPAWN;
            if (animations.get(actualState).isAnimationFinished(animationTimer))
                isSpawning = false;
        }
        else if (tookDamage && iFrames > 0) {
            actualState = PlayerState.HURT;
            if (iFrames < 0.5f)
                jump = right = down = left = false;
        }
        else if (down && !isJumping)
            actualState = PlayerState.DUCK;
        else if (isJumping)
            actualState = PlayerState.JUMP;
        else if (attack)
            actualState = PlayerState.STAB;
        else if ((right || left) && !attack) {
            if (walkTimer < timeToRunSpeed)
                actualState = PlayerState.HALF_WALK;
            else
                actualState = PlayerState.FULL_WALK;
        }
        else
            actualState = PlayerState.IDLE;
        
        //System.out.println(actualState + " e " + isJumping);
        if (actualState != previousState)
            animationTimer = 0;
    }
    
    private void physics(float dt) {
        
        //Setting speeds
        if (actualState == PlayerState.HURT && iFrames < 0.5f)
            xSpeed = knockbackSpeed.x;
        futurePositionOffset = new Vector2(0, 0);
        
        
        if (actualState == PlayerState.HURT) {
            ySpeed = knockbackSpeed.y;
        } else if (jump && !isJumping && canJump) {
            isJumping = true;
            smallJump = false;
            canJump = false;
            ySpeed = jumpSpeed;
        }
        
        if (isJumping && !canJump && ySpeed > 0) {
            if (!jump && !smallJump) {
                smallJump = true;
                ySpeed = 0;
            }
        }
        
        
        //Calculating position
        if (actualState == PlayerState.HURT) {
                float deltaSpeed =  xAcceleration * dt;
            if (xSpeed + deltaSpeed <= runSpeed)
                xSpeed += deltaSpeed;
            else
                xSpeed = runSpeed;
            futurePositionOffset.x += xSpeed * dt;
            
        } else if (right) {
            if (!down && !attack) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    xSpeed = walkSpeed;
                else
                    xSpeed = runSpeed;
                futurePositionOffset.x += xSpeed * dt;
            }
            flipX = false;
            
        } else if (left) {
            if (!down && !attack) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    xSpeed = walkSpeed;
                else
                    xSpeed = runSpeed;
                futurePositionOffset.x -= xSpeed * dt;
            }
            flipX = true;
        } else {
            walkTimer = 0;
            xSpeed = 0;
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
        
        bodyTopCollided = bodyLeftCollided = bodyRightCollided = feetBottomCollided = false;
        for (Rectangle wall : actualLevel.walls) {
            
            float feetBottom = futureFeetPosition.y;
            float bodyTop = futureBodyPosition.y + futureBodyPosition.height;
            float bodyLeft = futureBodyPosition.x;
            float bodyRight = futureBodyPosition.x + futureBodyPosition.width;
            
            float wallTop = wall.y + wall.height;
            float wallLeft = wall.x;
            float wallRight = wall.x + wall.width;
            float wallBottom = wall.y;
            
            if (futureBodyPosition.overlaps(wall)) {
                
                if (bodyTop >= wallBottom && bodyTop <= wallTop) bodyTopCollided = true;
                if (bodyLeft >= wallLeft && bodyLeft <= wallRight) bodyLeftCollided = true;
                if (bodyRight <= wallRight && bodyRight >= wallLeft) bodyRightCollided = true;
                
                if (bodyTopCollided || bodyLeftCollided || bodyRightCollided)
                    bodyCollided = true;
            }
            
            if (futureFeetPosition.overlaps(wall)) {
                if (feetBottom >= wallBottom && feetBottom <= wallTop) {
                    feetBottomCollided = true;
                    feetCollided = true;
                }
            }  
            
            if (bodyCollided && feetCollided) break;
        }
        
        for (Snake snake : actualLevel.snakes) {
            if (attack && futureSwordPosition.overlaps(snake.body)) {
                if (snake.iFrames == 0)
                    snake.getHurt(swordDamage);
            }
            
            if (futureHurtboxPosition.overlaps(snake.body) && snake.isAlive) {
                if (iFrames == 0)
                    getHurt(snake.damage);
            }
        }
        
        if (futureBodyPosition.overlaps(actualLevel.key) && !hasExitKey) {
            hasExitKey = true;
            System.out.println("Got exit key!");
        }
        
        if (futureBodyPosition.overlaps(actualLevel.chest) && hasExitKey && !finishedLevel) {
            finishedLevel = true;
            System.out.println("Finished level!");
        }
        
        if (!feetBottomCollided && !bodyTopCollided)
            moveOnYAxis();
        
        if (!bodyRightCollided && !bodyLeftCollided)
            moveOnXAxis();
        
        if (bodyTopCollided)
            ySpeed = 0;
        
        if (feetCollided) {
            isJumping = false;
            smallJump = false;
            if (!jump && !isJumping && !canJump)
                canJump = true;
            ySpeed = 0;
            
        } else {
            isJumping = true;
        }
    }
    
    private void sprite(float dt) {
        
        animationTimer += dt;
        actualRegion = animations.get(actualState).getKeyFrame(animationTimer);
        sprite.setRegion(actualRegion);
        sprite.setPosition(spriteArea.x, spriteArea.y);
        sprite.setFlip(flipX, flipY);
    }
    
    
    private void moveOnXAxis() {
        position.x = UnitHelper.roundMeters(position.x + futurePositionOffset.x);
        body.x = UnitHelper.roundMeters(body.x + futurePositionOffset.x);
        feet.x = UnitHelper.roundMeters(feet.x + futurePositionOffset.x);
        spriteArea.x = UnitHelper.roundMeters(spriteArea.x + futurePositionOffset.x);
        mainHurtbox.x = UnitHelper.roundMeters(mainHurtbox.x + futurePositionOffset.x);
        swordHitbox.x = UnitHelper.roundMeters(swordHitbox.x + futurePositionOffset.x);
    }
    
    private void moveOnYAxis() {
        position.y = UnitHelper.roundMeters(position.y + futurePositionOffset.y);
        body.y = UnitHelper.roundMeters(body.y + futurePositionOffset.y);
        feet.y = UnitHelper.roundMeters(feet.y + futurePositionOffset.y);
        spriteArea.y = UnitHelper.roundMeters(spriteArea.y + futurePositionOffset.y);
        mainHurtbox.y = UnitHelper.roundMeters(mainHurtbox.y + futurePositionOffset.y);
        swordHitbox.y = UnitHelper.roundMeters(swordHitbox.y + futurePositionOffset.y);  
    }
    
    private void getHurt(float damage) {
        actualState = PlayerState.HURT;
        health -= damage;
        tookDamage = true;
    }
    
    
    public void createBodies(float posX, float posY) {
        parts = new ArrayList<Rectangle>();
        
        position = new Vector2(posX, posY);
        body = new Rectangle(posX + UnitHelper.pixelsToMeters(24), posY + UnitHelper.pixelsToMeters(2),
                UnitHelper.pixelsToMeters(16), UnitHelper.pixelsToMeters(24));
        
        feet = new Rectangle(posX + UnitHelper.pixelsToMeters(26), posY + UnitHelper.pixelsToMeters(1),
                UnitHelper.pixelsToMeters(12), UnitHelper.pixelsToMeters(3));
        
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
