package com.badlogic.drop.adaper;

import com.badlogic.drop.R;
import com.badlogic.drop.UtilMath;
import com.badlogic.drop.model.TapBallManagerModel;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by tangsong on 11/5/14.
 */
public class NotingButBall extends ApplicationAdapter {

    public static final int WIDTH_BUCKET = 64;
    public static final int WIDTH_RAIN = 32;
    public static final int SPEED_MOVE = 100;
    private int mSpeed = SPEED_MOVE;
    public static final int SPEED_RAIN = 200;
    public static final int SPEED_ADD = 1;
    /**
     * 屏幕宽度
     */
    public static final int SCREEN_WIDTH = 800;
    /**
     * 屏幕高度
     */
    public static final int SCREEN_HEIGHT = 480;
    public boolean mDirection = true;
    boolean mIsStart = false;
    private Texture mBall;
    private OrthographicCamera mCamera;
    private SpriteBatch mBatch;
    private Rectangle mBucketRect;
    private Sound mClickSound;
    private Sound mBallHitSound;
    private long mCreateTime;
    private TapBallManagerModel mTapModel;
    private Sound mSoundFirstBlood;
    private Sound mSound2;
    private Sound mSound3;
    private Sound mSound4;
    private Sound mSound5;
    private Sound mSoundShutDown;

    @Override
    public void create() {
        mTapModel = new TapBallManagerModel();
        final ArrayList<Sound> list = new ArrayList<Sound>();

        mTapModel.setContinuousClickListener(new TapBallManagerModel.ContinuousClickListener() {
            @Override
            public void onContinuousClick(int count) {

            }

            @Override
            public void onMultClick(int count) {
                if (count - 1 < list.size()) {
                    list.get(count - 1).play();
                }
            }

            @Override
            public void onFirstBlood() {
                mSoundFirstBlood.play();
            }

            @Override
            public void onShutDown() {
                mSoundShutDown.play();
            }
        });

        mBall = new Texture(Gdx.files.internal(R.BUCKET_PNG));
        mClickSound = Gdx.audio.newSound(Gdx.files.internal(R.DROP_WAV));
        mBallHitSound = Gdx.audio.newSound(Gdx.files.internal(R.WAV_BALL_HIT));

        mSoundFirstBlood = Gdx.audio.newSound(Gdx.files.internal("kill_1.mp3"));
        mSound2 = Gdx.audio.newSound(Gdx.files.internal("kill_2.mp3"));
        mSound3 = Gdx.audio.newSound(Gdx.files.internal("kill_3.mp3"));
        mSound4 = Gdx.audio.newSound(Gdx.files.internal("kill_4.mp3"));
        mSound5 = Gdx.audio.newSound(Gdx.files.internal("kill_5.mp3"));
        mSoundShutDown = Gdx.audio.newSound(Gdx.files.internal("shut_down.mp3"));
        list.add(mSound2);
        list.add(mSound3);
        list.add(mSound4);
        list.add(mSound5);


        // create the mCamera and the SpriteBatch
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        mBatch = new SpriteBatch();

        mBucketRect = new Rectangle();
        mBucketRect.y = SCREEN_HEIGHT / 2 - WIDTH_BUCKET / 2; // center the mBucketRect horizontally
        mBucketRect.x = 20; // bottom left corner of the mBucketRect is 20 pixels above the bottom screen edge
        mBucketRect.width = WIDTH_BUCKET;
        mBucketRect.height = WIDTH_BUCKET;

        Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener()));

        mCreateTime = TimeUtils.millis();
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
        mBatch.draw(mBall, mBucketRect.x, mBucketRect.y);//绘制木桶到屏幕上
        //————场景结束————
        mBatch.end();


        if (TimeUtils.millis() - mCreateTime < 2000) {
            return;
        }

        if (!mIsStart) {
            mIsStart = true;
            mTapModel.start();
        }

        if (mDirection) {
            mBucketRect.x += mSpeed * Gdx.graphics.getDeltaTime();
        } else {
            mBucketRect.x -= mSpeed * Gdx.graphics.getDeltaTime();
        }

        // make sure the mBucketRect stays within the screen bounds
        if (mBucketRect.x < 0) {
            mBucketRect.x = 0;
            mDirection = true;
            mBallHitSound.play();
        } else if (mBucketRect.x > SCREEN_WIDTH - WIDTH_BUCKET) {
            mBucketRect.x = SCREEN_WIDTH - WIDTH_BUCKET;
            mDirection = false;
            mBallHitSound.play();
        }

    }

    @Override
    public void dispose() {
        mBall.dispose();
        super.dispose();
    }

    public class MyGestureListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            if (mBucketRect.contains(UtilMath.getVirtue(Gdx.graphics.getWidth(), SCREEN_WIDTH, Gdx.input.getX()), UtilMath.getVirtue(Gdx.graphics.getWidth(), SCREEN_WIDTH, Gdx.input.getY()))) {
                mSpeed += SPEED_ADD;
                mTapModel.success();
                mClickSound.play();
                return true;
            } else {
                mTapModel.failed();
            }
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {

            return false;
        }

        @Override
        public boolean longPress(float x, float y) {

            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {

            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {

            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean zoom(float originalDistance, float currentDistance) {

            return false;
        }

        @Override
        public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {

            return false;
        }
    }
}
