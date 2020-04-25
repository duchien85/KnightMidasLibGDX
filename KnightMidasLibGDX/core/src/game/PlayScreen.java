
package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    
    //Audio
    private Music music1;
    private final int volumeUp = Input.Keys.W, volumeDown = Input.Keys.Q;
    private float volume = 1f;
    
    //Hud
    private Hud hud;
    
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
        
        //Music
        music1 = Gdx.audio.newMusic(
                Gdx.files.internal(StringPaths.music_TutorialLevel));
        music1.setLooping(true);
        music1.setVolume(1f);
        music1.play();
        
        //Hud
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
        
        //Music
        if (Gdx.input.isKeyJustPressed(volumeUp)) volume += 0.2f;
        if (Gdx.input.isKeyJustPressed(volumeDown)) volume -= 0.2f;
        music1.setVolume(volume);
        
        //Hud
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
        
        renderHudAndDebug();
    }
    
    private void renderHudAndDebug() {
        
        //Hud
        if (hud.showHud) {
            main.batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud.stage.draw();
        }
        
        //Player Physics
        if (hud.debug[0]) {
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
            
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.BLUE);
            debugRenderer.rect(p1.head.x, p1.head.y, p1.head.width, p1.head.height);
            debugRenderer.end();
        }
        
        //Player Sprite/Hitboxes/Pos
        if (hud.debug[1]) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor((p1.isAttacking && !p1.finishedAttack)
                    ? Color.RED : Color.YELLOW);
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
            debugRenderer.setColor(Color.YELLOW);
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
        if (hud.debug[2]) {
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
        if (hud.debug[3]) {
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
        
        //Music
        music1.dispose();
        
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
