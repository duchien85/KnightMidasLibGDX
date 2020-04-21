
package game;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

public class Hud implements Disposable {
    
    //Input
    private final int numberOfDebugKeys = 7;
    private int actualHudPage = 1, firstHudPage = 1, lastHudPage = 7;
    
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
    
    private Label down, right, left, attack, jump;
    private Label isJumping, canJump, smallJump, tookDamage, isSpawning,
            hasExitKey, finishedLevel;
    private Label bodyTopCollided, bodyRightCollided, bodyLeftCollided,
            feetBottomCollided;
    private Label position, futurePositionOffset, velocity, jumpSpeed, gravity;
    private Label jumpHeight, jumpHalfDurationTime, timeToRunSpeed,
            walkSpeed, runSpeed, knockbackSpeed;
    private Label walkTimer, jumpTimer, actualState, animationTimer, iFrames;
    
    
    
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
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("m5x7.ttf"));
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
        
        //P�gina 2
        down = new Label("down: ", labelStyle);
        right = new Label("right: ", labelStyle);
        left = new Label("left: ", labelStyle);
        attack = new Label("attack: ", labelStyle);
        jump = new Label("jump: ", labelStyle);
        
        //P�gina 3
        isJumping = new Label("isJumping: ", labelStyle);
        canJump = new Label("canJump: ", labelStyle);
        smallJump = new Label("smallJump: ", labelStyle);
        tookDamage = new Label("tookDamage: ", labelStyle);
        isSpawning = new Label("isSpawning: ", labelStyle);
        hasExitKey = new Label("hasExitKey: ", labelStyle);
        finishedLevel = new Label("finishedLevel: ", labelStyle);
        
        //P�gina 4
        bodyTopCollided = new Label("bodyTopCollided: ", labelStyle);
        bodyRightCollided = new Label("bodyRightCollided: ", labelStyle);
        bodyLeftCollided = new Label("bodyLeftCollided: ", labelStyle);
        feetBottomCollided = new Label("feetBottomCollided: ", labelStyle);
        
        //P�gina 5
        position = new Label("position: ", labelStyle);
        futurePositionOffset = new Label("futurePositionOffset: ", labelStyle);
        velocity = new Label("velocity: ", labelStyle);
        jumpSpeed = new Label("jumpSpeed: ", labelStyle);
        gravity = new Label("gravity: ", labelStyle);
        
        //P�gina 6
        jumpHeight = new Label("jumpHeight: ", labelStyle);
        jumpHalfDurationTime = new Label("jumpHalfDurationTime: ", labelStyle);
        timeToRunSpeed = new Label("timeToRunSpeed: ", labelStyle);
        walkSpeed = new Label("walkSpeed: ", labelStyle);
        runSpeed = new Label("runSpeed: ", labelStyle);
        knockbackSpeed = new Label("knockbackSpeed: ", labelStyle);
        
        //P�gina 7
        walkTimer = new Label("walkTimer: ", labelStyle);
        jumpTimer = new Label("jumpTimer: ", labelStyle);
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
        
        //P�gina 1
        temp.add(new Label("placeholder", labelStyle));
        addToTable(0, temp);
        temp.clear();
        
        //P�gina 2
        temp.add(down);
        temp.add(right);
        temp.add(left);
        temp.add(attack);
        temp.add(jump);
        addToTable(1, temp);
        temp.clear();
        
        //P�gina 3
        temp.add(isJumping);
        temp.add(canJump);
        temp.add(smallJump);
        temp.add(tookDamage);
        temp.add(isSpawning);
        temp.add(hasExitKey);
        temp.add(finishedLevel);
        addToTable(2, temp);
        temp.clear();
        
        //P�gina 4
        temp.add(bodyTopCollided);
        temp.add(bodyLeftCollided);
        temp.add(bodyRightCollided);
        temp.add(feetBottomCollided);
        addToTable(3, temp);
        temp.clear();
        
        //P�gina 5
        temp.add(position);
        temp.add(futurePositionOffset);
        temp.add(velocity);
        temp.add(jumpSpeed);
        temp.add(gravity);
        addToTable(4, temp);
        temp.clear();
        
        //P�gina 6
        temp.add(jumpHeight);
        temp.add(jumpHalfDurationTime);
        temp.add(timeToRunSpeed);
        temp.add(walkSpeed);
        temp.add(runSpeed);
        temp.add(knockbackSpeed);
        addToTable(5, temp);
        temp.clear();
        
        //P�gina 7
        temp.add(actualState);
        temp.add(walkTimer);
        temp.add(jumpTimer);
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
        
        //P�gina 2
        down.setText("down: " + screen.p1.down);
        right.setText("right: " + screen.p1.right);
        left.setText("left: " + screen.p1.left);
        attack.setText("attack: " + screen.p1.attack);
        jump.setText("jump: " + screen.p1.jump);
        
        //P�gina 3
        isJumping.setText("isJumping: " + screen.p1.isJumping);
        canJump.setText("canJump: " + screen.p1.canJump);
        smallJump.setText("smallJump: " + screen.p1.smallJump);
        tookDamage.setText("tookDamage: " + screen.p1.tookDamage);
        isSpawning.setText("isSpawning: " + screen.p1.isSpawning);
        hasExitKey.setText("hasExitKey: " + screen.p1.hasExitKey);
        finishedLevel.setText("finishedLevel: " + screen.p1.finishedLevel);
        
        //P�gina 4
        bodyTopCollided.setText("bodyTopCollided: " + screen.p1.bodyTopCollided);
        bodyRightCollided.setText("bodyRightCollided: " + screen.p1.bodyRightCollided);
        bodyLeftCollided.setText("bodyLeftCollided: " + screen.p1.bodyLeftCollided);
        feetBottomCollided.setText("feetBottomCollided: " + screen.p1.feetBottomCollided);
        
        //P�gina 5
        position.setText("position: " 
                + String.format("%.4f", screen.p1.position.x) + ","
                + String.format("%.4f", screen.p1.position.y));
        futurePositionOffset.setText("futurePositionOffset: " 
                + String.format("%.4f", screen.p1.futurePositionOffset.x) + ","
                + String.format("%.4f", screen.p1.futurePositionOffset.y));
        velocity.setText("velocity: " 
                + String.format("%.4f", screen.p1.velocity.x) + ","
                + String.format("%.4f", screen.p1.velocity.y));
        jumpSpeed.setText("jumpSpeed: " + screen.p1.jumpSpeed);
        gravity.setText("gravity: " + screen.p1.gravity);
        
        //P�gina 6
        jumpHeight.setText("jumpHeight: " + screen.p1.jumpHeight);
        jumpHalfDurationTime.setText("jumpHalfDurationTime: " + screen.p1.jumpHalfDurationTime);
        timeToRunSpeed.setText("timeToRunSpeed: " + screen.p1.timeToRunSpeed);
        walkSpeed.setText("walkSpeed: " + screen.p1.walkSpeed);
        runSpeed.setText("runSpeed: " + screen.p1.runSpeed);
        knockbackSpeed.setText("knockbackSpeed: " 
                + String.format("%.4f", screen.p1.knockbackSpeed.x) + ","
                + String.format("%.4f", screen.p1.knockbackSpeed.y));
        
        //P�gina 7
        actualState.setText("actualState: " + screen.p1.actualState);
        walkTimer.setText("walkTimer: " + screen.p1.walkTimer);
        jumpTimer.setText("jumpTimer: " + screen.p1.jumpTimer);
        animationTimer.setText("animationTimer: " + screen.p1.animationTimer);
        iFrames.setText("iFrames: " + screen.p1.iFrames);
        
        stage.clear();
        stage.addActor(tables[actualHudPage-1]);
        
        /*
        labelPosX.setText("X Pos: " + screen.p1.body.x);
        labelPosY.setText("Y Pos: " + screen.p1.body.y);
        labelSpeedX.setText("X Speed: " + screen.p1.velocity.x);
        labelSpeedY.setText("Y Speed: " + screen.p1.velocity.y);
        labelPlayerHealth.setText("Health: " + screen.p1.health);
        //labelSnakeHealth.setText("Snake Health: " + screen.s1.health);
        labelPlayerState.setText("State: " + screen.p1.actualState);
        labelIFrames.setText("iFrames: " + screen.p1.iFrames);
        
        //labelDown.setText("down: " + screen.p1.down);
        //labelLeft.setText("left: " + screen.p1.left);
        //labelRight.setText("right: " + screen.p1.right);
        //labelAttack.setText("attack: " + screen.p1.attack);
        labelFeetBottom.setText("feetBottom: " + screen.p1.feetBottomCollided);
        labelBodyRight.setText("bodyRight: " + screen.p1.bodyRightCollided);
        labelBodyLeft.setText("bodyLeft: " + screen.p1.bodyLeftCollided);
        labelBodyTop.setText("bodyTop: " + screen.p1.bodyTopCollided);
        labelJump.setText("jump: " + screen.p1.jump);
        labelCanJump.setText("canJump: " + screen.p1.canJump);
        labelIsJumping.setText("isJumping: " + screen.p1.isJumping);
        labelSmallJump.setText("smallJump: " + screen.p1.smallJump);
        */
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
