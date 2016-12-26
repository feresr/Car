package com.feresr.car;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by feresr on 24/12/16.
 */
public class Car extends Sprite {
    private final float LENGTH;
    private float steering = 0.0f;
    private float velocity = 1.0f;

    private final float TURNING_TOLERANCE = .00001f;
    private final double MAX_STEERING_ANGLE = Math.PI / 4;

    public Car(float length) {
        super(new Texture("redcar.png"));
        this.LENGTH = length;
        this.setOrigin(length/6, this.getHeight()/2);
    }

    public void setSteering(float steering) {
        this.steering = steering;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    void move() {
        if (velocity < 0.0) {
            throw new IllegalArgumentException("Moving backwards is not supported");
        }

        if (Math.abs(steering) > MAX_STEERING_ANGLE) {
            if (steering > 0) {
                steering = (float) MAX_STEERING_ANGLE;
            } else {
                steering = (float) -MAX_STEERING_ANGLE;
            }
        }

        float turningAngle = (float) ((velocity / LENGTH) * Math.tan(steering));

        if (Math.abs(turningAngle) > TURNING_TOLERANCE) {
            float turningRadius = velocity / turningAngle;

            float cx = (float) (this.getX() - Math.sin(this.getRotationInRad()) * turningRadius);
            float cy = (float) (this.getY() + Math.cos(this.getRotationInRad()) * turningRadius);

            this.setX((float) (cx + Math.sin(turningAngle + this.getRotationInRad()) * turningRadius));
            this.setY((float) (cy - Math.cos(turningAngle + this.getRotationInRad()) * turningRadius));
        } else {
            //assume straight motion
            this.setX((float) (this.getX() + (velocity * Math.cos(this.getRotationInRad()))));
            this.setY((float) (this.getY() + (velocity * Math.sin(this.getRotationInRad()))));
        }

        this.setRotation((float) Math.toDegrees(((turningAngle + this.getRotationInRad()) % (2.0 * Math.PI))));
    }


    private float getRotationInRad() {
        return (float) Math.toRadians(super.getRotation());
    }
}
