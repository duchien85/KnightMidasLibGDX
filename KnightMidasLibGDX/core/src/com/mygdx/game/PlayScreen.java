
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import engine.physics.Physics;

public class PlayScreen implements Screen {
    
    protected Main main;
    Physics physics;
    Player p1;

    public PlayScreen(Main main) {
        this.main = main;
        
        physics = new Physics();
        
        p1 = new Player();
        p1.createBody(physics, 0, 0, 21, 25);
        
        p1.body.velocity.x = 0;
    }

    @Override
    public void render(float f) {
        float dt = Gdx.graphics.getDeltaTime();
        
        update(dt);
        render();
    }
    
    private void update(float dt) {
        
        p1.update(dt);
        physics.update(dt);
    }
    
    private void render() {
        
        Gdx.gl.glClearColor(0.69f, 0.47f, 0.21f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        main.batch.begin();
        p1.render(main.batch);
        main.batch.end();
    }

    @Override
    public void dispose() {
        p1.dispose();
    }

    
    @Override
    public void show() {
    }

    @Override
    public void resize(int i, int i1) {
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
