
package game;

import com.badlogic.gdx.Gdx;
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

public class Hud implements Disposable {
    
    public Stage stage;
    private Viewport viewport;
    
    private BitmapFont font;
    private Table rootTable;
    
    private Label labelPosX, labelPosY,
            labelSpeedX, labelSpeedY,
            labelPlayerHealth, labelSnakeHealth, labelPlayerState, labelIFrames;
    //private Label labelDown, labelRight, labelLeft, labelAttack;
    private Label labelBodyRight, labelBodyLeft, labelBodyTop, labelFeetBottom;
    private Label labelJump, labelCanJump, labelIsJumping, labelSmallJump;

    public Hud(SpriteBatch batch) {
        this.viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(this.viewport, batch);
        
        font();
        
        layout();
        
        stage.addActor(rootTable);
    }
    
    private void font() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("m5x7.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();
    }
    
    private void layout() {
        
        rootTable = new Table();
        rootTable.bottom();
        rootTable.setFillParent(true);
        rootTable.setDebug(false);
        
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        
        labelPosX = new Label("X Pos: ", labelStyle);
        labelPosY = new Label("Y Pos: ", labelStyle);
        labelSpeedX = new Label("X Speed: ", labelStyle);
        labelSpeedY = new Label("Y Speed: ", labelStyle);
        labelPlayerHealth = new Label("Health: ", labelStyle);
        labelSnakeHealth = new Label("Snake Health: ", labelStyle);
        labelPlayerState = new Label("State: ", labelStyle);
        labelIFrames = new Label("iFrames: ", labelStyle);
        
        //labelDown = new Label("down: false", labelStyle);
        //labelLeft = new Label("left: false", labelStyle);
        //labelRight = new Label("right: false", labelStyle);
        //labelAttack = new Label("attack: false", labelStyle);
        
        labelFeetBottom = new Label("feetBottom: false", labelStyle);
        labelBodyLeft = new Label("bodyLeft: false", labelStyle);
        labelBodyRight = new Label("bodyRight: false", labelStyle);
        labelBodyTop = new Label("bodyTop: false", labelStyle);
        labelJump = new Label("jump: false", labelStyle);
        labelCanJump = new Label("canJump: false", labelStyle);
        labelIsJumping = new Label("isJumping: false", labelStyle);
        labelSmallJump = new Label("smallJump: false", labelStyle);
        
        
        rootTable.add(labelIFrames).expandX().left().padLeft(5);
        //rootTable.add(labelDown).expandX().right().padRight(5);
        rootTable.add(labelFeetBottom).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPosX).expandX().left().padLeft(5);
        //rootTable.add(labelLeft).expandX().right().padRight(5);
        rootTable.add(labelBodyLeft).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPosY).expandX().left().padLeft(5);
        //rootTable.add(labelRight).expandX().right().padRight(5);
        rootTable.add(labelBodyRight).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelSpeedX).expandX().left().padLeft(5);
        //rootTable.add(labelAttack).expandX().right().padRight(5);
        rootTable.add(labelBodyTop).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelSpeedY).expandX().left().padLeft(5);
        rootTable.add(labelJump).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPlayerHealth).expandX().left().padLeft(5);
        rootTable.add(labelCanJump).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelSnakeHealth).expandX().left().padLeft(5);
        rootTable.add(labelIsJumping).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPlayerState).expandX().left().padLeft(5);
        rootTable.add(labelSmallJump).expandX().right().padRight(5);
    }
    
    protected void update(PlayScreen screen) {
        labelPosX.setText("X Pos: " + screen.p1.body.x);
        labelPosY.setText("Y Pos: " + screen.p1.body.y);
        labelSpeedX.setText("X Speed: " + screen.p1.xSpeed);
        labelSpeedY.setText("Y Speed: " + screen.p1.ySpeed);
        labelPlayerHealth.setText("Health: " + screen.p1.health);
        labelSnakeHealth.setText("Snake Health: " + screen.s1.health);
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
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
