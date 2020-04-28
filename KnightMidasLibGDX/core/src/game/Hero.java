
package game;

import game.animations.PlayerAnim;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import game.animations.CustomAnimationHelper;
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

public class Hero extends GameObject implements Disposable {
    
    //Input
    protected boolean left, right, attack, jump;
    
    //Logic
    protected boolean isJumping = false, isAttacking = false, finishedAttack = false, 
            isSpinning = false, canJump = true, canSpin = true, 
            canMoveLeft = true, canMoveRight = true, smallJump = false, 
            tookDamage = false, isSpawning = true, hasExitKey = false, 
            finishedLevel = false;
    protected float iFrames = 0;
    
    protected boolean headTopCollided = false, bodyLeftCollided = false,
                bodyRightCollided = false, feetBottomCollided = false;
    
    //Timers
    protected float walkTimer = 0f, jumpTimer = 1f, animationTimer = 0;
    
    //Health
    protected float health = 20f;
    private float swordDamage = 6f;
    
    //Audio
    private Sound swordSound;
    
    //Physics
    protected Vector2 pos;
    protected Vector2 bodyOffset, headOffset, feetOffset, spriteRectOffset,
            hurtboxOffset, swordHitboxOffset;
    protected Rectangle body, head, feet, spriteRect, hurtbox, swordHitbox;
    protected List<Rectangle> parts;
    protected Vector2 futurePosOffset;
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
    private HashMap<PlayerAnim, Animation<TextureRegion>> animations;
    
    protected TextureRegion currentRegion;
    protected PlayerAnim currentAnim = PlayerAnim.SPAWN;
    private boolean flipX = false, flipY = false;
    
    
    public Hero(Level level, float posX, float posY) {
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
        PlayerAnim previousState = currentAnim;
        
        if (isSpawning) {
            currentAnim = PlayerAnim.SPAWN;
            if (animations.get(currentAnim).isAnimationFinished(animationTimer))
                isSpawning = false;
        }
        else if (health < 0) {
            currentAnim = PlayerAnim.DEAD;
        }
        else if (tookDamage && iFrames > 0) {
            currentAnim = PlayerAnim.HURT;
            if (iFrames < 0.5f)
                jump = right = left = false;
        }
        else if (isSpinning) {
            if (previousState != PlayerAnim.SPIN)
                animationTimer = 0;
            currentAnim = PlayerAnim.SPIN;
            if (animations.get(currentAnim).isAnimationFinished(animationTimer))
                isSpinning = false;
        }
        else if (isAttacking) {
            if (previousState != PlayerAnim.STAB)
                animationTimer = 0;
            currentAnim = PlayerAnim.STAB;
            if (animations.get(currentAnim).isAnimationFinished(animationTimer))
                finishedAttack = true;
        }
        else if (isJumping)
            currentAnim = PlayerAnim.JUMP;
        else if ((right || left)
                && (!isAttacking && !attack) && (canMoveLeft && canMoveRight)) {
            if (walkTimer < timeToRunSpeed)
                currentAnim = PlayerAnim.HALF_WALK;
            else
                currentAnim = PlayerAnim.FULL_WALK;
        }
        else
            currentAnim = PlayerAnim.IDLE;
        
        
        if (currentAnim != previousState)
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
        if (currentAnim == PlayerAnim.HURT && iFrames < 0.5f)
            velocity.x = knockbackSpeed.x;
        futurePosOffset = new Vector2(0, 0);
        
        
        if (currentAnim == PlayerAnim.HURT) {
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
        if (currentAnim == PlayerAnim.HURT) {
            futurePosOffset.x += knockbackSpeed.x * dt;
            
        } else if (right) {
            if ((!attack && !isAttacking) || isJumping) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    velocity.x = walkSpeed;
                else
                    velocity.x = runSpeed;
                futurePosOffset.x += velocity.x * dt;
            }
            flipX = false;
            swordHitbox.x = pos.x + Units.pixelsMeters(36);
            
        } else if (left) {
            if ((!attack && !isAttacking) || isJumping) {
                walkTimer += dt;
                if (walkTimer < timeToRunSpeed)
                    velocity.x = walkSpeed;
                else
                    velocity.x = runSpeed;
                futurePosOffset.x -= velocity.x * dt;
            }
            flipX = true;
            swordHitbox.x = pos.x;
        } else {
            walkTimer = 0;
            velocity.x = 0;
        }
        
        velocity.y += gravity * dt;
        futurePosOffset.y += velocity.y * dt;
    }
    
    private void collisions() {
        
        boolean bodyCollided = false;
        boolean feetCollided = false;
        boolean headCollided = false;
        Rectangle futureBodyPosition = new Rectangle(
                body.x + futurePosOffset.x, body.y + futurePosOffset.y,
                body.width, body.height);
        
        Rectangle futureFeetPosition = new Rectangle(
                feet.x + futurePosOffset.x, feet.y + futurePosOffset.y,
                feet.width, feet.height);
        
        Rectangle futureHeadPosition = new Rectangle(
                head.x + futurePosOffset.x, head.y + futurePosOffset.y,
                head.width, head.height);
        
        Rectangle futureHurtboxPosition = new Rectangle(
                hurtbox.x + futurePosOffset.x, hurtbox.y + futurePosOffset.y,
                hurtbox.width, hurtbox.height);
        
        Rectangle futureSwordPosition = new Rectangle(
                swordHitbox.x + futurePosOffset.x, swordHitbox.y + futurePosOffset.y,
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
        
        if (!bodyRightCollided && !bodyLeftCollided) {
            moveOnXAxis();
            canMoveRight = true;
            canMoveLeft = true;
        } else {
            canMoveRight = flipX;
            canMoveLeft = !flipX;
        }
        
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
        currentRegion = animations.get(currentAnim).getKeyFrame(animationTimer);
        sprite.setRegion(currentRegion);
        sprite.setPosition(spriteRect.x, spriteRect.y);
        sprite.setFlip(flipX, flipY);
    }
    
    
    private void moveOnXAxis() {
        pos.x = Units.roundMeters(pos.x + futurePosOffset.x);
        body.x = Units.roundMeters(body.x + futurePosOffset.x);
        head.x = Units.roundMeters(head.x + futurePosOffset.x);
        feet.x = Units.roundMeters(feet.x + futurePosOffset.x);
        spriteRect.x = Units.roundMeters(spriteRect.x + futurePosOffset.x);
        hurtbox.x = Units.roundMeters(hurtbox.x + futurePosOffset.x);
        swordHitbox.x = Units.roundMeters(swordHitbox.x + futurePosOffset.x);
    }
    
    private void moveOnYAxis() {
        pos.y = Units.roundMeters(pos.y + futurePosOffset.y);
        body.y = Units.roundMeters(body.y + futurePosOffset.y);
        head.y = Units.roundMeters(head.y + futurePosOffset.y);
        feet.y = Units.roundMeters(feet.y + futurePosOffset.y);
        spriteRect.y = Units.roundMeters(spriteRect.y + futurePosOffset.y);
        hurtbox.y = Units.roundMeters(hurtbox.y + futurePosOffset.y);
        swordHitbox.y = Units.roundMeters(swordHitbox.y + futurePosOffset.y);  
    }
    
    private void getHurt(float damage) {
        currentAnim = PlayerAnim.HURT;
        health -= damage;
        tookDamage = true;
    }
    
    
    public void createBodies(float posX, float posY) {
        parts = new ArrayList<Rectangle>();
        
        bodyOffset = new Vector2(Units.pixelsMeters(24), Units.pixelsMeters(2));
        headOffset = new Vector2(Units.pixelsMeters(28), Units.pixelsMeters(25));
        feetOffset = new Vector2(Units.pixelsMeters(28), Units.pixelsMeters(1));
        spriteRectOffset = new Vector2(0, 0);
        hurtboxOffset = new Vector2(Units.pixelsMeters(24), Units.pixelsMeters(2));
        swordHitboxOffset = new Vector2(Units.pixelsMeters(36), Units.pixelsMeters(1));
        
        pos = new Vector2(posX, posY);
        body = new Rectangle(pos.x + bodyOffset.x, posY + bodyOffset.y,
                Units.pixelsMeters(16), Units.pixelsMeters(24));
        
        head = new Rectangle(pos.x + headOffset.x, posY + headOffset.y,
                Units.pixelsMeters(8), Units.pixelsMeters(3));
        
        feet = new Rectangle(posX + feetOffset.x, posY + feetOffset.y,
                Units.pixelsMeters(8), Units.pixelsMeters(3));
        
        spriteRect = new Rectangle(posX, posY, 
                Units.pixelsMeters(spriteWidthPixels), Units.pixelsMeters(spriteHeightPixels));
        
        hurtbox = new Rectangle(posX + hurtboxOffset.x, posY + hurtboxOffset.y,
                Units.pixelsMeters(16), Units.pixelsMeters(23));
        
        swordHitbox = new Rectangle(posX + swordHitboxOffset.x, posY + swordHitboxOffset.y,
                Units.pixelsMeters(26), Units.pixelsMeters(12));
        
        parts.add(body);
        parts.add(feet);
        parts.add(head);
        parts.add(spriteRect);
        parts.add(hurtbox);
        parts.add(swordHitbox);
    }
    
    public void createAnimations() {
        
        sprite = new Sprite();
        sprite.setBounds(spriteRect.x, spriteRect.y,
                spriteRect.width, spriteRect.height);
        sprite.setScale(1, 1);
        
        spritesheet = new Texture(StringPaths.texture_Hero);
        
        animations = new HashMap<>();
        
        CustomAnimationBundle bundle = CustomAnimationJsonReader.getFrames(StringPaths.json_Hero);
        CustomAnimation anim;
        
        for (PlayerAnim state : PlayerAnim.values()) {
            anim = bundle.getByName(state.getStateName());
            
            if (anim != null) {
                animations.put(state, new Animation(
                    1f/anim.time,
                    CustomAnimationHelper.getTextureRegions(anim.frames, spritesheet),
                    CustomAnimationHelper.getPlayMode(anim.playMode)));
            }
        }
    }
    
    @Override
    public void dispose() {
        spritesheet.dispose();
        swordSound.dispose();
    }
}