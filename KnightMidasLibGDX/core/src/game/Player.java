
package game;

import game.animations.PlayerState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
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
    protected boolean left, right, attack, jump;
    
    //Logic
    protected boolean isJumping = false;
    protected boolean isAttacking = false;
    protected boolean finishedAttack = false;
    protected boolean isSpinning = false;
    protected boolean canJump = true;
    protected boolean canSpin = true;
    protected boolean smallJump = false;
    protected boolean tookDamage = false;
    protected boolean isSpawning = true;
    protected boolean hasExitKey = false;
    protected boolean finishedLevel = false;
    protected float iFrames = 0;
    
    protected boolean headTopCollided = false, bodyLeftCollided = false,
                bodyRightCollided = false, feetBottomCollided = false;
    
    //Timers
    protected float walkTimer = 0f, jumpTimer = 1f;
    protected float animationTimer = 0;
    
    //Health
    protected float health = 20f;
    private float swordDamage = 6f;
    
    //Audio
    private Sound swordSound;
    
    //Physics
    protected Vector2 position;
    protected Rectangle body, head, feet, spriteArea, mainHurtbox, swordHitbox;
    protected List<Rectangle> parts;
    protected Vector2 futurePositionOffset;
    protected Vector2 velocity = Vector2.Zero;
    
    protected float jumpHeight = 6f, jumpHalfDurationTime = 0.5f,
            timeToRunSpeed = 6 / 30f;
    protected float spinHeight = 6f, spinHalfDurationTime = 0.5f;
    protected float walkSpeed = 3.5f, runSpeed = 8f;
    protected Vector2 knockbackSpeed = new Vector2(3f, 2f);
    
    protected float jumpSpeed, spinSpeed, gravity;
    
    //Render
    protected Sprite sprite;
    private float spriteWidthPixels = 64, spriteHeightPixels = 64;
    private Texture spritesheet;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.SPAWN;
    private boolean flipX = false, flipY = false;
    
    
    public Player(Level level, float posX, float posY) {
        super(level);
        createBodies(posX, posY);
        createAnimations();
        
        swordSound = Gdx.audio.newSound(
                Gdx.files.internal(StringPaths.sound_SwordSwish));
        
        gravity = (-2*jumpHeight) / (jumpHalfDurationTime * jumpHalfDurationTime);
        jumpSpeed = 2 * jumpHeight / jumpHalfDurationTime;
        spinSpeed = 2 * spinHeight / spinHalfDurationTime;
    }
    
    public void update(float dt) {
        
        getInput();
        
        iFrames(dt);
        
        changeState();
        
        logic();
        
        physics(dt);
        
        collisions();
        
        sprite(dt);
    }
    
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    
    
    private void getInput() {
        
        right = left = attack = jump = false;
        
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        attack = Gdx.input.isKeyPressed(Input.Keys.X);
        jump = Gdx.input.isKeyPressed(Input.Keys.Z);
        
        if ((left && right) || 
                (!isJumping &&attack && isAttacking && !finishedAttack)) {
            left = right = false;
        }
        
        
        if (attack && !isAttacking && !tookDamage) {
            if (!isJumping) {
                isAttacking = true;
                swordSound.play();
            } else if (canSpin) {
                isSpinning = true;
            }
        }
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
        else if (health < 0) {
            actualState = PlayerState.DEAD;
        }
        else if (tookDamage && iFrames > 0) {
            actualState = PlayerState.HURT;
            if (iFrames < 0.5f)
                jump = right = left = false;
        }
        else if (isSpinning) {
            if (previousState != PlayerState.SPIN)
                animationTimer = 0;
            actualState = PlayerState.SPIN;
            if (animations.get(actualState).isAnimationFinished(animationTimer))
                isSpinning = false;
        }
        else if (isAttacking) {
            if (previousState != PlayerState.STAB)
                animationTimer = 0;
            actualState = PlayerState.STAB;
            if (animations.get(actualState).isAnimationFinished(animationTimer))
                finishedAttack = true;
        }
        else if (isJumping)
            actualState = PlayerState.JUMP;
        else if ((right || left) && !isAttacking && !attack) {
            if (walkTimer < timeToRunSpeed)
                actualState = PlayerState.HALF_WALK;
            else
                actualState = PlayerState.FULL_WALK;
        }
        else
            actualState = PlayerState.IDLE;
        
        
        if (actualState != previousState)
            animationTimer = 0;
    }
    
    private void logic() {
        if (finishedAttack && !attack && isAttacking) {
            isAttacking = false;
            finishedAttack = false;
        }
    }
    
    private void physics(float dt) {
        
        //Setting speeds
        if (actualState == PlayerState.HURT && iFrames < 0.5f)
            velocity.x = knockbackSpeed.x;
        futurePositionOffset = new Vector2(0, 0);
        
        
        if (actualState == PlayerState.HURT) {
            velocity.y = knockbackSpeed.y;
        } else if (isSpinning && canSpin) {
            velocity.y = spinSpeed;
            canSpin = false;
        } else if (jump && !isJumping && canJump) {
            isJumping = true;
            smallJump = false;
            canJump = false;
            velocity.y = jumpSpeed;
        }
        
        if (isJumping && !canJump && velocity.y > 0) {
            if (!jump && !smallJump) {
                smallJump = true;
                velocity.y = 0;
            }
        }
        
        
        //Calculating position
        if (actualState == PlayerState.HURT) {
            futurePositionOffset.x += knockbackSpeed.x * dt;
            
        } else if (right) {
            if ((!attack && !isAttacking) || isJumping) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    velocity.x = walkSpeed;
                else
                    velocity.x = runSpeed;
                futurePositionOffset.x += velocity.x * dt;
            }
            flipX = false;
            swordHitbox.x = position.x + UnitHelper.pixelsToMeters(36);
            
        } else if (left) {
            if ((!attack && !isAttacking) || isJumping) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    velocity.x = walkSpeed;
                else
                    velocity.x = runSpeed;
                futurePositionOffset.x -= velocity.x * dt;
            }
            flipX = true;
            swordHitbox.x = position.x;
        } else {
            walkTimer = 0;
            velocity.x = 0;
        }
        
        velocity.y += gravity * dt;
        futurePositionOffset.y += velocity.y * dt;
    }
    
    private void collisions() {
        
        boolean bodyCollided = false;
        boolean feetCollided = false;
        boolean headCollided = false;
        Rectangle futureBodyPosition = new Rectangle(
                body.x + futurePositionOffset.x, body.y + futurePositionOffset.y,
                body.width, body.height);
        
        Rectangle futureFeetPosition = new Rectangle(
                feet.x + futurePositionOffset.x, feet.y + futurePositionOffset.y,
                feet.width, feet.height);
        
        Rectangle futureHeadPosition = new Rectangle(
                head.x + futurePositionOffset.x, head.y + futurePositionOffset.y,
                head.width, head.height);
        
        Rectangle futureHurtboxPosition = new Rectangle(
                mainHurtbox.x + futurePositionOffset.x, mainHurtbox.y + futurePositionOffset.y,
                mainHurtbox.width, mainHurtbox.height);
        
        Rectangle futureSwordPosition = new Rectangle(
                swordHitbox.x + futurePositionOffset.x, swordHitbox.y + futurePositionOffset.y,
                swordHitbox.width, swordHitbox.height);
        
        headTopCollided = bodyLeftCollided = bodyRightCollided = feetBottomCollided = false;
        for (Rectangle wall : actualLevel.walls) {
            
            float feetBottom = futureFeetPosition.y;
            float headTop = futureHeadPosition.y + futureHeadPosition.height;
            float bodyLeft = futureBodyPosition.x;
            float bodyRight = futureBodyPosition.x + futureBodyPosition.width;
            
            float wallTop = wall.y + wall.height;
            float wallLeft = wall.x;
            float wallRight = wall.x + wall.width;
            float wallBottom = wall.y;
            
            if (futureBodyPosition.overlaps(wall)) {
                
                if (bodyLeft >= wallLeft && bodyLeft <= wallRight) bodyLeftCollided = true;
                if (bodyRight <= wallRight && bodyRight >= wallLeft) bodyRightCollided = true;
                
                if (bodyLeftCollided || bodyRightCollided)
                    bodyCollided = true;
            }
            
            if (futureFeetPosition.overlaps(wall)) {
                if (feetBottom >= wallBottom && feetBottom <= wallTop) {
                    feetBottomCollided = true;
                    feetCollided = true;
                }
            }
            
            if (futureHeadPosition.overlaps(wall)) {
                if (headTop >= wallBottom && headTop <= wallTop) {
                    headTopCollided = true;
                    headCollided = true;
                }
                
            }
            
            if (bodyCollided && feetCollided) break;
        }
        
        for (Snake snake : actualLevel.snakes) {
            if (isAttacking && !finishedAttack &&
                    futureSwordPosition.overlaps(snake.body)) {
                if (snake.iFrames == 0)
                    snake.getHurt(swordDamage);
            }
            
            if (futureHurtboxPosition.overlaps(snake.body) && snake.isAlive) {
                if (!isSpinning) {
                    if (iFrames == 0)
                        getHurt(snake.damage);
                    
                } else {
                    snake.getHurt(swordDamage);
                }
            }
        }
        
        if (futureBodyPosition.overlaps(actualLevel.key) 
                && !hasExitKey) {
            hasExitKey = true;
        }
        
        if (futureBodyPosition.overlaps(actualLevel.chest)
                && hasExitKey && !finishedLevel) {
            finishedLevel = true;
        }
        
        if (!feetBottomCollided && !headTopCollided)
            moveOnYAxis();
        
        if (!bodyRightCollided && !bodyLeftCollided)
            moveOnXAxis();
        
        if (headTopCollided)
            velocity.y = 0;
        
        if (feetCollided) {
            isJumping = false;
            smallJump = false;
            isSpinning = false;
            canSpin = true;
            if (!jump && !isJumping && !canJump)
                canJump = true;
            velocity.y = 0;
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
        head.x = UnitHelper.roundMeters(head.x + futurePositionOffset.x);
        feet.x = UnitHelper.roundMeters(feet.x + futurePositionOffset.x);
        spriteArea.x = UnitHelper.roundMeters(spriteArea.x + futurePositionOffset.x);
        mainHurtbox.x = UnitHelper.roundMeters(mainHurtbox.x + futurePositionOffset.x);
        swordHitbox.x = UnitHelper.roundMeters(swordHitbox.x + futurePositionOffset.x);
    }
    
    private void moveOnYAxis() {
        position.y = UnitHelper.roundMeters(position.y + futurePositionOffset.y);
        body.y = UnitHelper.roundMeters(body.y + futurePositionOffset.y);
        head.y = UnitHelper.roundMeters(head.y + futurePositionOffset.y);
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
        
        head = new Rectangle(posX + UnitHelper.pixelsToMeters(28), posY + UnitHelper.pixelsToMeters(25),
                UnitHelper.pixelsToMeters(8), UnitHelper.pixelsToMeters(3));
        
        feet = new Rectangle(posX + UnitHelper.pixelsToMeters(28), posY + UnitHelper.pixelsToMeters(1),
                UnitHelper.pixelsToMeters(8), UnitHelper.pixelsToMeters(3));
        
        spriteArea = new Rectangle(posX, posY, 
                UnitHelper.pixelsToMeters(spriteWidthPixels), UnitHelper.pixelsToMeters(spriteHeightPixels));
        
        mainHurtbox = new Rectangle(posX + UnitHelper.pixelsToMeters(24), posY + UnitHelper.pixelsToMeters(2),
                UnitHelper.pixelsToMeters(16), UnitHelper.pixelsToMeters(23));
        
        swordHitbox = new Rectangle(posX + UnitHelper.pixelsToMeters(36), posY + UnitHelper.pixelsToMeters(1),
                UnitHelper.pixelsToMeters(26), UnitHelper.pixelsToMeters(12));
        
        parts.add(body);
        parts.add(feet);
        parts.add(head);
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
            
            if (anim != null) {
                animations.put(state, new Animation(
                    1f/anim.time,
                    AnimationHelper.getTextureRegions(anim.frames, spritesheet),
                    AnimationHelper.getPlayMode(anim.playMode)));
            }
        }
    }
    
    @Override
    public void dispose() {
        spritesheet.dispose();
        swordSound.dispose();
    }
}
