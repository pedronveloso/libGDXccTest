package com.pedronveloso.libgdx1;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Pedro Veloso
 */
public class TestGame extends Game {

    private final static int NUM_STARS = 700;
    private final int SHOW_TIE_AT = 4; //seconds

    private Stage ui;
    private TextureRegion emergeLogo;
    private OrthographicCamera camera;
    private Texture star, tieFighter, companyLogo;
    private SpriteBatch batch;
    private BitmapFont font;

    //ship animation
    private static final int FRAME_COLS = 2;         // #1
    private static final int FRAME_ROWS = 1;         // #2

    //game logic? :P
    private int mCurrentKilledEnemies = 0;
    private int mCurrentRecord = 0;

    //animation related
    private Animation shipAnimation;
    private float stateTime;

    private ArrayList<Star> stars;
    private TieStarWars tieStarWars;

    private ArrayList<AnimatedShip> animatedShips = null;

    private boolean firstTieInterAppearance = true;

    //sounds
    private Sound fighterSound;
    private Music backgroundMusic;

    private float color = 0.01f;
    private float colorVariance = 0.001f;
    private int frameNum = 0;

    @Override
    public void create() {
        emergeLogo = new TextureRegion(new Texture(Gdx.files.internal("data/logo_simples.png")));
        star = new Texture(Gdx.files.internal("data/star.png"));
        tieFighter = new Texture(Gdx.files.internal("data/tieintercepter.png"));
        companyLogo = new Texture(Gdx.files.internal("data/logo_simples.png"));

        //load background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("data/background_music.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();


        //load font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/DroidSans.ttf"));
        font = generator.generateFont(15);
        font.setColor(1, 1, 1, 1);
        generator.dispose();

        //load animation
        Texture animatedShip = new Texture(Gdx.files.internal("data/ship_anim_1.png"));
        TextureRegion[][] tmp = TextureRegion.split(animatedShip, animatedShip.getWidth() /
                FRAME_COLS, animatedShip.getHeight());
        TextureRegion[] shipFrames = new TextureRegion[FRAME_COLS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                shipFrames[index++] = tmp[i][j];
            }
        }
        shipAnimation = new Animation(0.1f, shipFrames);

        stateTime = 0f;

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


        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        Gdx.graphics.setVSync(true);

        //logging
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        //listen for user input
        MyInputProcessor inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void dispose() {
        ui.dispose();
        emergeLogo.getTexture().dispose();
        star.dispose();
        tieFighter.dispose();
        fighterSound.dispose();
        companyLogo.dispose();
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

        //draw animated ships
        if (animatedShips != null && animatedShips.size() > 0) {
            stateTime += Gdx.graphics.getDeltaTime();
            for (AnimatedShip animatedShip : animatedShips) {
                batch.draw(shipAnimation.getKeyFrame(stateTime, true), animatedShip.getX(), animatedShip.getY());
                animatedShip.move();
            }

            removeAnimatedShips();
        }

        // draw FPS
        font.setScale(1);
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 30);
        if (animatedShips != null && animatedShips.size() > 0) {
            font.setScale(1.5f);
            font.setColor(1, 0, 0, 1);
            font.draw(batch, "#enemy ships: " + animatedShips.size(), 20, 60);
        }

        //draw logo
        batch.draw(companyLogo, 10, Gdx.graphics.getHeight() - (companyLogo.getHeight() + 10));

        batch.end();

        ui.draw();
        frameNum++;

    }

    /**
     * Remove animated ships that have existed left side of the screen
     * or are said to be dead
     */
    private void removeAnimatedShips() {
        for (int i = 0; i < animatedShips.size(); i++) {
            if (animatedShips.get(i).getX() < 0) {
                animatedShips.remove(i);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        ui.setViewport(width, height, false);
    }


    /**
     * Handling input events
     */
    public class MyInputProcessor implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            //Gdx.app.log(Constants.LOG_TAG, "touch up at X: "+x+", Y: "+y);
            if (animatedShips == null) {
                animatedShips = new ArrayList<AnimatedShip>();
            }
            animatedShips.add(new AnimatedShip(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - y));
            return false;
        }

        @Override
        public boolean touchDragged(int x, int y, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int i, int i2) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

}
