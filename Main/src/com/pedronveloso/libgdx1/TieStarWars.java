package com.pedronveloso.libgdx1;

import com.badlogic.gdx.math.Rectangle;

/**
 * User: Pedro Veloso
 * Date: 2/16/13
 * Time: 2:25 AM
 */
public class TieStarWars {

    private final float SPEED = 6f;
    private Rectangle tieRect;

    public TieStarWars(final int windowWidth) {
        tieRect = new Rectangle();
        tieRect.x = windowWidth;
        tieRect.y = 30;
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
