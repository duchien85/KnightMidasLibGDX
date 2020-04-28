
package game;

import utils.StringPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

public class Hud implements Disposable {
    
    //Input
    private final int numberOfDebugKeys = 7;
    private int actualHudPage = 1, firstHudPage = 1, lastHudPage = 7;
    
    protected int frameCounter = 0;
    protected int frameUpdateDelay = 3;
    
    protected boolean[] debug;
    protected boolean showHud = false;
    
    private int[] debugKeys;
    private final int showHudKey = Input.Keys.NUMPAD_9;
    private final int nextPageKey = Input.Keys.PLUS,
            previousPageKey = Input.Keys.MINUS;
    
    
    //Scene
    public Stage stage;
    private Viewport viewport;
    
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    
    private Table[] tables;
    private Label[] pageLabels;
    
    private Label right, left, attack, jump;
    private Label isJumping, canJump, canSpin, smallJump, tookDamage, isSpawning,
            isAttacking, isSpinning, finishedAttack, hasExitKey, finishedLevel;
    private Label headTopCollided, bodyRightCollided, bodyLeftCollided,
            feetBottomCollided;
    private Label position, futurePositionOffset, velocity, jumpSpeed, gravity;
    private Label jumpHeight, jumpHalfDurationTime, timeToRunSpeed,
            walkSpeed, runSpeed, knockbackSpeed;
    private Label walkTimer, actualState, animationTimer, iFrames;
    
    
    public Hud(SpriteBatch batch) {
        this.viewport = new FitViewport(Main.DISPLAY_PIXEL_WIDTH,
                Main.DISPLAY_PIXELS_HEIGHT, new OrthographicCamera());
        stage = new Stage(this.viewport, batch);
        
        createInput();
        
        font();
        
        createComponents();
        
        layout();
        
        stage.addActor(tables[0]);
    }
    
    private void createInput() {
        
        debug = new boolean[numberOfDebugKeys];
        debugKeys = new int[numberOfDebugKeys];
        for (int i = 1; i <= numberOfDebugKeys; i++) {
            debug[i-1] = false;
            debugKeys[i-1] = Input.Keys.NUM_0 + i;
        }
    }
    
    private void font() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(StringPaths.font_m5x7));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();
        
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
    }
    
    private void createComponents() {
        
        //Pagina 2
        right = new Label("right: ", labelStyle);
        left = new Label("left: ", labelStyle);
        attack = new Label("attack: ", labelStyle);
        jump = new Label("jump: ", labelStyle);
        
        //Pagina 3
        isJumping = new Label("isJumping: ", labelStyle);
        isAttacking = new Label("isAttacking: ", labelStyle);
        isSpinning = new Label("isSpinning: ", labelStyle);
        finishedAttack = new Label("finishedAttack", labelStyle);
        canJump = new Label("canJump: ", labelStyle);
        canSpin = new Label("canSpin: ", labelStyle);
        smallJump = new Label("smallJump: ", labelStyle);
        tookDamage = new Label("tookDamage: ", labelStyle);
        isSpawning = new Label("isSpawning: ", labelStyle);
        hasExitKey = new Label("hasExitKey: ", labelStyle);
        finishedLevel = new Label("finishedLevel: ", labelStyle);
        
        //Pagina 4
        headTopCollided = new Label("headTopCollided: ", labelStyle);
        bodyRightCollided = new Label("bodyRightCollided: ", labelStyle);
        bodyLeftCollided = new Label("bodyLeftCollided: ", labelStyle);
        feetBottomCollided = new Label("feetBottomCollided: ", labelStyle);
        
        //Pagina 5
        position = new Label("position: ", labelStyle);
        futurePositionOffset = new Label("futurePositionOffset: ", labelStyle);
        velocity = new Label("velocity: ", labelStyle);
        jumpSpeed = new Label("jumpSpeed: ", labelStyle);
        gravity = new Label("gravity: ", labelStyle);
        
        //Pagina 6
        jumpHeight = new Label("jumpHeight: ", labelStyle);
        jumpHalfDurationTime = new Label("jumpHalfDurationTime: ", labelStyle);
        timeToRunSpeed = new Label("timeToRunSpeed: ", labelStyle);
        walkSpeed = new Label("walkSpeed: ", labelStyle);
        runSpeed = new Label("runSpeed: ", labelStyle);
        knockbackSpeed = new Label("knockbackSpeed: ", labelStyle);
        
        //Pagina 7
        walkTimer = new Label("walkTimer: ", labelStyle);
        actualState = new Label("actualState: ", labelStyle);
        animationTimer = new Label("animationTimer: ", labelStyle);
        iFrames = new Label("iFrames: ", labelStyle);
        
        pageLabels = new Label[lastHudPage];
        tables = new Table[lastHudPage];
        for (int i = 0; i < lastHudPage; i++) {
            pageLabels[i] = new Label("Page " + (i+1), labelStyle);
            
            tables[i] = new Table();
            tables[i].bottom();
            tables[i].setFillParent(true);
        }
    }
    
    private void layout() {
        
        ArrayList<Label> temp = new ArrayList<>();
        
        //Pagina 1
        temp.add(new Label("placeholder", labelStyle));
        addToTable(0, temp);
        temp.clear();
        
        //Pagina 2
        temp.add(right);
        temp.add(left);
        temp.add(attack);
        temp.add(jump);
        addToTable(1, temp);
        temp.clear();
        
        //Pagina 3
        temp.add(isJumping);
        temp.add(canJump);
        temp.add(smallJump);
        temp.add(isAttacking);
        temp.add(finishedAttack);
        temp.add(isSpinning);
        temp.add(canSpin);
        temp.add(tookDamage);
        temp.add(isSpawning);
        temp.add(hasExitKey);
        temp.add(finishedLevel);
        addToTable(2, temp);
        temp.clear();
        
        //Pagina 4
        temp.add(headTopCollided);
        temp.add(bodyLeftCollided);
        temp.add(bodyRightCollided);
        temp.add(feetBottomCollided);
        addToTable(3, temp);
        temp.clear();
        
        //Pagina 5
        temp.add(position);
        temp.add(futurePositionOffset);
        temp.add(velocity);
        temp.add(jumpSpeed);
        temp.add(gravity);
        addToTable(4, temp);
        temp.clear();
        
        //Pagina 6
        temp.add(jumpHeight);
        temp.add(jumpHalfDurationTime);
        temp.add(timeToRunSpeed);
        temp.add(walkSpeed);
        temp.add(runSpeed);
        temp.add(knockbackSpeed);
        addToTable(5, temp);
        temp.clear();
        
        //Pagina 7
        temp.add(actualState);
        temp.add(walkTimer);
        temp.add(animationTimer);
        temp.add(iFrames);
        addToTable(6, temp);
        temp.clear();
    }
    
    private void addToTable(int index, ArrayList<Label> temp) {
        
        int i = 1;
        for (Label label : temp) {
            tables[index].add(label).expandX().left().padLeft(5);
            if (i == temp.size()) tables[index].add(pageLabels[index]).right().padRight(5);
            tables[index].row();
            
            i++;
        }
    }
    
    
    protected void update(PlayScreen screen) {
        
        input();
        
        if (frameCounter % frameUpdateDelay == 0) {
            
            frameCounter = 0;
            
            //Pagina 2
            right.setText("right: " + screen.h1.right);
            left.setText("left: " + screen.h1.left);
            attack.setText("attack: " + screen.h1.attack);
            jump.setText("jump: " + screen.h1.jump);

            //Pagina 3
            isJumping.setText("isJumping: " + screen.h1.isJumping);
            isAttacking.setText("isAttacking: " + screen.h1.isAttacking);
            isSpinning.setText("isSpinning: " + screen.h1.isSpinning);
            finishedAttack.setText("finishedAttack: " + screen.h1.finishedAttack);
            canJump.setText("canJump: " + screen.h1.canJump);
            canSpin.setText("canSpin: " + screen.h1.canSpin);
            smallJump.setText("smallJump: " + screen.h1.smallJump);
            tookDamage.setText("tookDamage: " + screen.h1.tookDamage);
            isSpawning.setText("isSpawning: " + screen.h1.isSpawning);
            hasExitKey.setText("hasExitKey: " + screen.h1.hasExitKey);
            finishedLevel.setText("finishedLevel: " + screen.h1.finishedLevel);

            //Pagina 4
            headTopCollided.setText("headTopCollided: " + screen.h1.headTopCollided);
            bodyRightCollided.setText("bodyRightCollided: " + screen.h1.bodyRightCollided);
            bodyLeftCollided.setText("bodyLeftCollided: " + screen.h1.bodyLeftCollided);
            feetBottomCollided.setText("feetBottomCollided: " + screen.h1.feetBottomCollided);

            //Pagina 5
            position.setText("position: " 
                    + String.format("%.4f", screen.h1.pos.x) + "  "
                    + String.format("%.4f", screen.h1.pos.y));
            futurePositionOffset.setText("futurePositionOffset: " 
                    + String.format("%.4f", screen.h1.futurePosOffset.x) + "  "
                    + String.format("%.4f", screen.h1.futurePosOffset.y));
            velocity.setText("velocity: " 
                    + String.format("%.2f", screen.h1.velocity.x) + "  "
                    + String.format("%.2f", screen.h1.velocity.y));
            jumpSpeed.setText("jumpSpeed: " + screen.h1.jumpSpeed);
            gravity.setText("gravity: " + screen.h1.gravity);

            //Pagina 6
            jumpHeight.setText("jumpHeight: " + screen.h1.jumpHeight);
            jumpHalfDurationTime.setText("jumpHalfDurationTime: " + screen.h1.jumpHalfDurationTime);
            timeToRunSpeed.setText("timeToRunSpeed: " + screen.h1.timeToRunSpeed);
            walkSpeed.setText("walkSpeed: " + screen.h1.walkSpeed);
            runSpeed.setText("runSpeed: " + screen.h1.runSpeed);
            knockbackSpeed.setText("knockbackSpeed: " 
                    + String.format("%.2f", screen.h1.knockbackSpeed.x) + "  "
                    + String.format("%.2f", screen.h1.knockbackSpeed.y));

            //Pagina 7
            actualState.setText("actualState: " + screen.h1.currentAnim);
            walkTimer.setText("walkTimer: (" 
                    + screen.h1.walkTimer.active + ") " +
                    + screen.h1.walkTimer.time);
            
            animationTimer.setText("animationTimer: (" 
                    + screen.h1.animationTimer.active + ") " +
                    + screen.h1.animationTimer.time);
            
            iFrames.setText("iFrames: (" + screen.h1.iFramesTimer.active 
                    + ") " + screen.h1.iFramesTimer.time);

            stage.clear();
            stage.addActor(tables[actualHudPage-1]);
        }
        
        frameCounter++;
    }
    
    private void input() {
        
        for (int i = 1; i <= numberOfDebugKeys; i++) {
            if (Gdx.input.isKeyJustPressed(debugKeys[i-1])) debug[i-1] = !debug[i-1];
        }
        
        boolean nextPage = Gdx.input.isKeyJustPressed(nextPageKey);
        boolean previousPage = Gdx.input.isKeyJustPressed(previousPageKey);
        
        if (nextPage) {
            actualHudPage = Math.min(actualHudPage+1, lastHudPage);
        } else if (previousPage) {
            actualHudPage = Math.max(actualHudPage-1, firstHudPage);
        }
        
        if (Gdx.input.isKeyJustPressed(showHudKey)) showHud = !showHud;
    }
    

    @Override
    public void dispose() {
        stage.dispose();
    }
}
