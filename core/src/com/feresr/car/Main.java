package com.feresr.car;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Car car;
    private float ds = 0.2f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        car = new Car(80);
        car.setX(130);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        car.move(1f, ds);
        car.draw(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
