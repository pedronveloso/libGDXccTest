package com.pedronveloso.libgdx1;

import com.badlogic.gdx.math.Rectangle;

/**
 * User: Pedro Veloso
 * Date: 2/16/13
 * Time: 7:58 PM
 */
public class AnimatedShip {

    private Rectangle tieRect;
    private final float SPEED = 6f;

    public AnimatedShip(final int windowWidth, final int startY) {
        tieRect = new Rectangle();
        tieRect.x = windowWidth;
        tieRect.y = startY;
        tieRect.width = 121;
        tieRect.height = 96;
    }

    public void move() {
        tieRect.x -= SPEED;
    }

    public float getX() {
        return tieRect.x;
    }

    public float getY() {
        return tieRect.y;
    }
}
