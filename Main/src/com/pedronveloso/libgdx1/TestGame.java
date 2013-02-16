package com.pedronveloso.libgdx1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Pedro Veloso
 */
public class TestGame extends Game {

    private final static int NUM_STARS = 900;
    private final int SHOW_TIE_AT = 4; //seconds

    private Stage ui;
    private Table root;
    private TextureRegion emergeLogo;
    private OrthographicCamera camera;
    private Texture star, tieFighter;
    private SpriteBatch batch;
    private BitmapFont font;

    private ArrayList<Star> stars;
    private TieStarWars tieStarWars;

    private boolean firstTieInterAppearance = true;
    private Sound fighterSound;

    private float color = 0.01f;
    private float colorVariance = 0.001f;
    private int frameNum = 0;

    @Override
    public void create() {
        emergeLogo = new TextureRegion(new Texture(Gdx.files.internal("data/logo_simples.png")));
        star = new Texture(Gdx.files.internal("data/star.png"));
        tieFighter = new Texture(Gdx.files.internal("data/tieintercepter.png"));

        //load font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/DroidSans.ttf"));
        font = generator.generateFont(15);//new BitmapFont();
        font.setColor(1, 1, 1, 1);
        generator.dispose();

        stars = new ArrayList<Star>(NUM_STARS);
        Random rnd = new Random();
        for (int i = 0; i < NUM_STARS; i++) {
            stars.add(new Star(rnd, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        }

        //tie intercepter (aka fighter?)
        tieStarWars = new TieStarWars(Gdx.graphics.getWidth());
        fighterSound = Gdx.audio.newSound(Gdx.files.internal("data/tie.ogg"));

        ui = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(ui);

        root = new Table();
        ui.addActor(root);
        //root.debug();

        batch = new SpriteBatch();

        Image image = new Image(emergeLogo);
        image.setScaling(Scaling.fit);
        root.add(image);

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        Gdx.graphics.setVSync(true);
    }

    @Override
    public void dispose() {
        ui.dispose();
        emergeLogo.getTexture().dispose();
        star.dispose();
        tieFighter.dispose();
        fighterSound.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        //draw background
        Gdx.gl.glClearColor(color, color, color, 1);
        color += colorVariance;
        if (color > 0.1) {
            colorVariance = -0.001f;
        } else if (color < 0) {
            color = 0;
            colorVariance = 0.001f;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ui.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        ui.setCamera(camera);

        //draw stars
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Star star1 : stars) {
            //batch.draw(star, star1.getX(), star1.getY());
            batch.draw(star, star1.getX(), star1.getY(), star1.getSize(), star1.getSize(), 0, 0, 12, 12, false, false);
            star1.move();
        }
        //draw tie intercepter?
        if (frameNum > (SHOW_TIE_AT * 60) && tieStarWars.getX() > 0) {
            batch.draw(tieFighter, tieStarWars.getX(), tieStarWars.getY());
            tieStarWars.move();
            if (firstTieInterAppearance) {
                firstTieInterAppearance = false;
                fighterSound.play();
            }
        }

        // draw FPS
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 30);

        batch.end();

        //draw logo
        ui.draw();
        Table.drawDebug(ui);
        frameNum++;

    }

    @Override
    public void resize(int width, int height) {
        ui.setViewport(width, height, false);
        root.setSize(width, height);
    }

}
