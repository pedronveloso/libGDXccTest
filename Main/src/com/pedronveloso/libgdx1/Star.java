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

    public Star(Random rnd, int windowWidth, int windowHeight) {
        starRect = new Rectangle();
        starRect.x = rnd.nextInt(windowWidth);
        starRect.y = rnd.nextInt(windowHeight);
        starRect.width = 12;
        starRect.height = 12;
        speed = 2 + rnd.nextFloat() * 5;
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
            speed = 2 + rnd.nextFloat() * 5;
        }
    }

    public float getX() {
        return starRect.x;
    }

    public float getY() {
        return starRect.y;
    }
}
