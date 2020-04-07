
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
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

public class Player extends GameObject implements Disposable {
    
    //Input
    private boolean up, left, right, down, attack, jump;
    
    //Physics
    protected Rectangle body;
    private float speed, moveSpeed = 1/8f;
    
    //Render
    protected Sprite sprite;
    private Texture spritesheetTexture;
    private HashMap<PlayerState, Animation<TextureRegion>> animations;
    protected TextureRegion actualRegion;
    protected PlayerState actualState = PlayerState.HERO_IDLE;
    
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
        speed = moveSpeed;
        
        if (right)
            body.x += speed;
        else if (left)
            body.x -= speed;
        
        
        //Render
        animationTimer += dt;
        actualRegion = animations.get(actualState).getKeyFrame(animationTimer);
        sprite.setRegion(actualRegion);
        sprite.setPosition(body.x, body.y);
    }
    
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    
    
    public void createBody() {
        body = new Rectangle(0, 0, 4, 4);
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
