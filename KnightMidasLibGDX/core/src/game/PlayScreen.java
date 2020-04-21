
package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {
    
    protected Main main;
    protected Player p1;
    
    //Graphics
    private final OrthographicCamera camera;
    private final float cameraWidth = 30, cameraHeight = 30;
    private final Viewport viewport;
    private ShapeRenderer debugRenderer;
    
    //Hud
    private Hud hud;
    private boolean debug1 = false;
    private final int debugKey1 = Input.Keys.NUM_1;
    private boolean debug2 = false;
    private final int debugKey2 = Input.Keys.NUM_2;
    private boolean debug3 = false;
    private final int debugKey3 = Input.Keys.NUM_3;
    private boolean debug4 = false;
    private final int debugKey4 = Input.Keys.NUM_4;
    private boolean debug9 = false;
    private final int debugKey9 = Input.Keys.NUMPAD_9;
    
    //Level
    private Level level;
    private final OrthogonalTiledMapRenderer mapRenderer;

    
    public PlayScreen(Main main) {
        
        this.main = main;
        
        //Graphics - Render
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.DISPLAY_WIDTH, Main.DISPLAY_HEIGHT, camera);
        camera.position.set(15, 15, 0);
        debugRenderer = new ShapeRenderer();
        hud = new Hud(main.batch);
        
        //Level
        level = new Level(StringPaths.tiled_ExampleLevel);
        mapRenderer = new OrthogonalTiledMapRenderer(level.getMap(), Main.METERS_PER_PIXEL);
        
        //Objects
        p1 = new Player(level, 2, 4.1875f);
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
        for (Snake snake : level.snakes)
            snake.update(dt);
        
        //Camera
        camera.position.x = MathUtils.clamp(p1.body.x + p1.body.width / 2,
                15, Main.WORLD_WIDTH - 15);
        camera.update();
        
        //Hud
        if (Gdx.input.isKeyJustPressed(debugKey1)) debug1 = !debug1;
        if (Gdx.input.isKeyJustPressed(debugKey2)) debug2 = !debug2;
        if (Gdx.input.isKeyJustPressed(debugKey3)) debug3 = !debug3;
        if (Gdx.input.isKeyJustPressed(debugKey4)) debug4 = !debug4;
        if (Gdx.input.isKeyJustPressed(debugKey9)) debug9 = !debug9;
        hud.update(this);
        
        //Level
        mapRenderer.setView(camera);
    }
    
    
    private void renderGraphics() {
        
        //Gdx.gl.glClearColor(0.69f, 0.47f, 0.21f, 1);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        mapRenderer.render();
        
        main.batch.setProjectionMatrix(camera.combined);
        main.batch.begin();
        p1.render(main.batch);
        for (Snake snake : level.snakes)
            snake.render(main.batch);
        main.batch.end();
        
        renderDebug();
        
        if (debug9) {
            main.batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud.stage.draw();
        }
    }
    
    private void renderDebug() {
        
        
        //Player Physics
        if (debug1) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.RED);
            debugRenderer.rect(p1.body.x, p1.body.y, p1.body.width, p1.body.height);
            debugRenderer.end();
            
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.BLUE);
            debugRenderer.rect(p1.feet.x, p1.feet.y, p1.feet.width, p1.feet.height);
            debugRenderer.end();
        }
        
        
        //Player Sprite/Hitboxes/Pos
        if (debug2) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.YELLOW);
            debugRenderer.rect(p1.swordHitbox.x, p1.swordHitbox.y,
                    p1.swordHitbox.width, p1.swordHitbox.height);
            debugRenderer.end();
            
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.YELLOW);
            debugRenderer.rect(p1.mainHurtbox.x, p1.mainHurtbox.y,
                    p1.mainHurtbox.width, p1.mainHurtbox.height);
            debugRenderer.end();
            
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.GREEN);
            debugRenderer.rect(p1.spriteArea.x, p1.spriteArea.y,
                    p1.spriteArea.width, p1.spriteArea.height);
            debugRenderer.end();
            
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
            debugRenderer.setColor(Color.WHITE);
            debugRenderer.circle(p1.position.x, p1.position.y, 1/8f);
            debugRenderer.end();
        }
        
        
        //Snakes
        if (debug3) {
            for (Snake s : level.snakes) {
                debugRenderer.setProjectionMatrix(camera.combined);
                debugRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugRenderer.setColor(Color.RED);
                debugRenderer.rect(s.body.x, s.body.y, s.body.width, s.body.height);
                debugRenderer.end();

                debugRenderer.setProjectionMatrix(camera.combined);
                debugRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugRenderer.setColor(Color.GREEN);
                debugRenderer.rect(s.spriteArea.x, s.spriteArea.y,
                        s.spriteArea.width, s.spriteArea.height);
                debugRenderer.end();

                debugRenderer.setProjectionMatrix(camera.combined);
                debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
                debugRenderer.setColor(Color.WHITE);
                debugRenderer.circle(s.position.x, s.position.y, 1/8f);
                debugRenderer.end();
            }
        }
        
        
        //Walls
        if (debug4) {
            for (Rectangle wall : level.walls) {
                debugRenderer.setProjectionMatrix(camera.combined);
                debugRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugRenderer.setColor(Color.YELLOW);
                debugRenderer.rect(wall.x, wall.y, wall.width, wall.height);
                debugRenderer.end();
            }
        }
    }

    @Override
    public void dispose() {
        
        //Graphic
        hud.dispose();
        
        //Objects
        p1.dispose();
        for (Snake snake : level.snakes)
            snake.dispose();
        
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
