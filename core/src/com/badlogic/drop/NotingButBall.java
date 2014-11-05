package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by tangsong on 11/5/14.
 */
public class NotingButBall extends ApplicationAdapter {

    public static final String BUCKET_PNG = "bucket.png";
    public static final String DROP_WAV = "drop.wav";
    public static final String RAIN_MP3 = "rain.mp3";

    public static final int WIDTH_BUCKET = 64;
    public static final int WIDTH_RAIN = 32;

    public static final int SPEED_MOVE = 500;
    public static final int SPEED_RAIN = 200;
    /**
     * 屏幕宽度
     */
    public static final int SCREEN_WIDTH = 800;
    /**
     * 屏幕高度
     */
    public static final int SCREEN_HEIGHT = 480;
    public boolean mDirection = true;
    private Texture mBall;
    private OrthographicCamera mCamera;
    private SpriteBatch mBatch;
    private Rectangle mBucket;

    @Override
    public void create() {
        mBall = new Texture(Gdx.files.internal(BUCKET_PNG));

        // create the mCamera and the SpriteBatch
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        mBatch = new SpriteBatch();

        mBucket = new Rectangle();
        mBucket.y = SCREEN_HEIGHT / 2 - WIDTH_BUCKET / 2; // center the mBucket horizontally
        mBucket.x = 20; // bottom left corner of the mBucket is 20 pixels above the bottom screen edge
        mBucket.width = WIDTH_BUCKET;
        mBucket.height = WIDTH_BUCKET;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the mCamera to update its matrices.
        mCamera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the mCamera.
        mBatch.setProjectionMatrix(mCamera.combined);

        mBatch.begin();
        mBatch.draw(mBall, mBucket.x, mBucket.y);//绘制木桶到屏幕上
        //————场景结束————
        mBatch.end();


        if (mDirection) {
            mBucket.x += SPEED_MOVE * Gdx.graphics.getDeltaTime();
        } else {
            mBucket.x -= SPEED_MOVE * Gdx.graphics.getDeltaTime();
        }

        // make sure the mBucket stays within the screen bounds
        if (mBucket.x < 0) {
            mBucket.x = 0;
            mDirection = true;
        } else if (mBucket.x > SCREEN_WIDTH - WIDTH_BUCKET) {
            mBucket.x = SCREEN_WIDTH - WIDTH_BUCKET;
            mDirection = false;
        }

    }

    @Override
    public void dispose() {
        mBall.dispose();
        super.dispose();
    }
}
