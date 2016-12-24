package com.feresr.car;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Car car;
    private float ds = 4.932f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        car = new Car(216);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        car.move(2f, ds);
        car.draw(batch);
        //todo draw
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
