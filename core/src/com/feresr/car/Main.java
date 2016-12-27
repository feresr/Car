package com.feresr.car;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Car car;
    private float P = 0.0289f;
    private float D = 2.8612f;

    private float I = 0.0f;
    private float ctesum = 0f;
    private float oldcte = 0f;

    private float cte = 0f;

    int u = 0;
    private float[][] path = new float[8][2];

    @Override
    public void create() {
        batch = new SpriteBatch();
        car = new Car(80);
        car.setX(100);
        car.setY(100);
        car.setVelocity(3f);

        path[0][0] = 100;
        path[0][1] = 100;

        path[1][0] = 200;
        path[1][1] = 80;

        path[2][0] = 400f;
        path[2][1] = 100f;

        path[3][0] = 480f;
        path[3][1] = 200f;

        path[4][0] = 400f;
        path[4][1] = 400f;

        path[5][0] = 200f;
        path[5][1] = 480f;

        path[6][0] = 100f;
        path[6][1] = 400f;

        path[7][0] = 80f;
        path[7][1] = 200f;

        path = smoothPathCircular(path, .5f, .1f, .000001f);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        float dx = path[(u + 1) % path.length][0] - path[u % path.length][0];
        float dy = path[(u + 1) % path.length][1] - path[u % path.length][1];
        float rx = car.getX() - path[u % path.length][0];
        float ry = car.getY() - path[u % path.length][1];

        oldcte = cte;
        cte = (float) ((ry * dx - rx * dy) / Math.sqrt(dx * dx + dy * dy));
        ctesum += cte;
        System.out.println(u);
        float s = -(P * cte) - (D * (cte - oldcte)) - (I * ctesum);
        if (Math.abs((rx * dx + ry * dy) / (dx * dx + dy * dy)) > 1.0) {
            System.out.println(s);
            u++;
        }
        car.setSteering(s);
        car.move();

        car.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    private float run(float[] params) {
        Car car = new Car(80);
        car.setX(0);
        car.setY(80);
        car.setVelocity(1f);

        float crosstrack_error = car.getY();
        int N = 100;
        float diff_crosstrack_error = 0.0f;
        float int_crosstrack_error = 0.0f;
        float steer = 0.0f;
        float err = 0.0f;
        for (int i = 0; i < N * N; i++) {
            diff_crosstrack_error = car.getY() - crosstrack_error;
            crosstrack_error = car.getY();
            int_crosstrack_error += crosstrack_error;

            steer = -params[0] * crosstrack_error
                    - params[1] * diff_crosstrack_error
                    - int_crosstrack_error * params[2];
            car.setSteering(steer);
            car.move();
            if (i >= N) {
                err += (crosstrack_error * crosstrack_error);
            }
        }

        return err / N;
    }

    private float[][] smoothPath(float[][] path, float weightData, float weightSmooth, float tolerance) {
        float[][] smoothPath = new float[path.length][path[0].length];

        for (int i = 0; i < path.length; i++) {
            System.arraycopy(path[i], 0, smoothPath[i], 0, path[0].length);
        }

        float change = tolerance;
        float aux;
        while (change >= tolerance) {
            change = 0;
            for (int i = 1; i < path.length - 1; i++) {
                for (int j = 0; j < path[0].length; j++) {
                    aux = smoothPath[i][j];
                    smoothPath[i][j] += weightData * (path[i][j] - smoothPath[i][j])
                            + weightSmooth * (smoothPath[i - 1][j] + smoothPath[i + 1][j] - (2.0 * smoothPath[i][j]));
                    change += Math.abs(aux - smoothPath[i][j]);
                }
            }
        }
        return smoothPath;
    }

    private float[][] smoothPathCircular(float[][] path, float weightData, float weightSmooth, float tolerance) {
        float[][] smoothPath = new float[path.length][path[0].length];

        for (int i = 0; i < path.length; i++) {
            System.arraycopy(path[i], 0, smoothPath[i], 0, path[0].length);
        }

        float change = tolerance;
        float aux;
        int index = 1;
        while (change >= tolerance) {
            change = 0;
            for (int j = 0; j < path[0].length; j++) {
                aux = smoothPath[index % smoothPath.length][j];
                smoothPath[index % path.length][j] += weightData * (path[index % path.length][j] - smoothPath[index % smoothPath.length][j])
                        + weightSmooth * (smoothPath[(index + 1) % smoothPath.length][j] + smoothPath[(index - 1) % smoothPath.length][j] - (2.0f * smoothPath[index % smoothPath.length][j]));
                change += Math.abs(aux - smoothPath[index % smoothPath.length][j]);
            }
            index++;
        }
        return smoothPath;
    }

    private float twiddle(float tolerance) {
        float[] p = new float[]{0.0f, 0.0f, 0.0f};
        float[] dp = new float[]{1.0f, 1.0f, 1.0f};

        float best_error = run(p);
        float err = 0.0f;
        while (sum(dp) > tolerance) {
            for (int i = 0; i < p.length; i++) {
                p[i] = p[i] + dp[i];
                err = run(p);
                if (err < best_error) {
                    best_error = err;
                    dp[i] *= 1.1;
                } else {
                    p[i] -= 2 * dp[i];
                    err = run(p);
                    if (err < best_error) {
                        best_error = err;
                        dp[i] *= 1.1;
                    } else {
                        p[i] = p[i] + dp[i];
                        dp[i] *= .9;
                    }
                }
            }
            System.out.println(p[0]);
            System.out.println(p[1]);
            System.out.println(p[2]);
            System.out.println("---");
        }
        return run(p);
    }

    private float sum(float[] array) {
        float sum = 0.0f;
        for (float anArray : array) {
            sum += anArray;
        }
        return sum;
    }
}
