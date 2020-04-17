
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
            labelPlayerHealth, labelPlayerState;
    private Label labelDown, labelRight, labelLeft, labelAttack, labelJump;

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
        labelPlayerState = new Label("State: ", labelStyle);
        
        labelDown = new Label("down: no", labelStyle);
        labelLeft = new Label("left: no", labelStyle);
        labelRight = new Label("right: no", labelStyle);
        labelAttack = new Label("attack: no", labelStyle);
        labelJump = new Label("jump: no", labelStyle);
        
        
        rootTable.add(labelPosX).expandX().left().padLeft(5);
        rootTable.row();
        
        rootTable.add(labelPosY).expandX().left().padLeft(5);
        rootTable.add(labelDown).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelSpeedX).expandX().left().padLeft(5);
        rootTable.add(labelLeft).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelSpeedY).expandX().left().padLeft(5);
        rootTable.add(labelRight).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPlayerHealth).expandX().left().padLeft(5);
        rootTable.add(labelAttack).expandX().right().padRight(5);
        rootTable.row();
        
        rootTable.add(labelPlayerState).expandX().left().padLeft(5);
        rootTable.add(labelJump).expandX().right().padRight(5);
    }
    
    protected void update(PlayScreen screen) {
        labelPosX.setText("X Pos: " + screen.p1.body.x);
        labelPosY.setText("Y Pos: " + screen.p1.body.y);
        labelSpeedX.setText("X Speed: " + screen.p1.xSpeed);
        labelSpeedY.setText("Y Speed: " + screen.p1.ySpeed);
        labelPlayerHealth.setText("Health: " + screen.p1.health);
        labelPlayerState.setText("State: " + screen.p1.actualState);
        
        labelDown.setText("down: " + screen.p1.down);
        labelLeft.setText("left: " + screen.p1.left);
        labelRight.setText("right: " + screen.p1.right);
        labelAttack.setText("attack: " + screen.p1.attack);
        labelJump.setText("jump: " + screen.p1.jump);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
