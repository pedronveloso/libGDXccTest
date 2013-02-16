package com.pedronveloso.libgdx1;

import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/**
 * User: Pedro Veloso
 * Date: 2/16/13
 * Time: 1:54 AM
 */
public class Star {

    private Rectangle starRect;
    private float speed;
    private int windowWidth, windowHeight;
    private Random rnd;
    private final static float SQUARE_SIZE = 1.5f;
    // base speed is the minimum star speed possible
    private final static float BASE_SPEED = 1f;
    // max acceleration factor that will be ADDED to @BASE_SPEED
    private final static float MAX_ACCELERATION_FACTOR = 5f;

    public Star(Random rnd, int windowWidth, int windowHeight) {
        starRect = new Rectangle();
        starRect.x = rnd.nextInt(windowWidth);
        starRect.y = rnd.nextInt(windowHeight);
        starRect.width = 12;
        starRect.height = 12;
        speed = BASE_SPEED + rnd.nextFloat() * MAX_ACCELERATION_FACTOR;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        this.rnd = rnd;
    }

    public void move() {
        starRect.x += speed;
        //if reach limit of screen spawn somewhere else at the left side with random speed
        if (starRect.x > windowWidth) {
            starRect.x = 0;
            starRect.y = rnd.nextInt(windowHeight);
            speed = BASE_SPEED + rnd.nextFloat() * MAX_ACCELERATION_FACTOR;
        }
    }

    public float getX() {
        return starRect.x;
    }

    public float getY() {
        return starRect.y;
    }


    public float getSize() {
        return SQUARE_SIZE * speed;
    }
}
