
package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud implements Disposable {
    
    public Stage stage;
    private Viewport viewport;
    
    private Label guidoLabel;

    public Hud(SpriteBatch batch) {
        this.viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, new OrthographicCamera());
        stage = new Stage(this.viewport, batch);
        
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        BitmapFont font = new BitmapFont(Gdx.files.internal("m5x7.fnt"));
        labelStyle.font = font;
        labelStyle.fontColor = Color.ORANGE;
        
        guidoLabel = new Label("GUIDO", labelStyle);
        guidoLabel.setSize(0.5f, 0.5f);
        
        table.add(guidoLabel).expandX().padTop(Main.METERS_PER_PIXEL);
        
        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
