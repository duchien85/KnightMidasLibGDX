
package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {
    
    protected Main main;
    private Player p1;
    private Snake s1;
    
    //Graphics
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private ShapeRenderer debugRenderer;
    private Hud hud;
    
    //Level
    private Level level;
    private final OrthogonalTiledMapRenderer mapRenderer;

    
    public PlayScreen(Main main) {
        
        this.main = main;
        
        //Graphics - Render
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        debugRenderer = new ShapeRenderer();
        hud = new Hud(main.batch);
        
        //Level
        level = new Level(StringPaths.tiled_ExampleLevel);
        mapRenderer = new OrthogonalTiledMapRenderer(level.getMap(), Main.METERS_PER_PIXEL);
        
        //Objects
        p1 = new Player(0, 7);
        p1.actualLevel = level;
        
        s1 = new Snake(16, 7);
        s1.actualLevel = level;
        level.snakes.add(s1);
    }

    @Override
    public void render(float f) {
        float dt = Gdx.graphics.getDeltaTime();
        
        updateThings(dt);
        renderGraphics();
    }
    
    private void updateThings(float dt) {
        
        //Objects
        p1.update(dt);
        s1.update(dt);
        
        //Level
        mapRenderer.setView(camera);
    }
    
    
    private void renderGraphics() {
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClearColor(0.69f, 0.47f, 0.21f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        mapRenderer.render();
        
        main.batch.setProjectionMatrix(camera.combined);
        main.batch.begin();
        p1.render(main.batch);
        s1.render(main.batch);
        main.batch.end();
        
        renderDebug();
        
        main.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }
    
    private void renderDebug() {
        
        //Player Parts
        Color[] playerColors = new Color[] {
            Color.RED, Color.BLUE,
            Color.GREEN, Color.YELLOW, Color.YELLOW
        };
        
        int i = 0;
        for (Rectangle part : p1.parts) {
            if (i != 0 && i != 1 && i != 2) {
                /*
                debugRenderer.setProjectionMatrix(camera.combined);
                debugRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugRenderer.setColor(playerColors[i]);
                debugRenderer.rect(part.x, part.y, part.width, part.height);
                debugRenderer.end();
                */
            }
            i++;
        }
        
        /*
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.circle(p1.position.x, p1.position.y, 1/8f);
        debugRenderer.end();
        */
        
        //Snake Parts
        /*
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(s1.body.x, s1.body.y, s1.body.width, s1.body.height);
        debugRenderer.end();
        
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.GREEN);
        debugRenderer.rect(s1.spriteArea.x, s1.spriteArea.y, s1.spriteArea.width, s1.spriteArea.height);
        debugRenderer.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.circle(s1.position.x, s1.position.y, 1/8f);
        debugRenderer.end();
        */
        
    }

    @Override
    public void dispose() {
        
        //Graphic
        hud.dispose();
        
        //Objects
        p1.dispose();
        s1.dispose();
        
        //Level
        level.dispose();
        mapRenderer.dispose();
    }

    
    @Override
    public void resize(int i, int i1) {
        viewport.update(i, i1, true);
    }
    
    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
