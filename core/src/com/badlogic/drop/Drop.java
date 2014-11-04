package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {

    public static final String DROPLET_PNG = "droplet.png";
    public static final String BUCKET_PNG = "bucket.png";
    public static final String DROP_WAV = "drop.wav";
    public static final String RAIN_MP3 = "rain.mp3";

    public static final int WIDTH_BUCKET = 64;
    public static final int WIDTH_RAIN = 32;

    public static final int SPEED_MOVE = 1000;
    public static final int SPEED_RAIN = 200;


    /**
     * 屏幕宽度
     */
    public static final int SCREEN_WIDTH = 800;
    /**
     * 屏幕高度
     */
    public static final int SCREEN_HEIGHT = 480;

    /**
     * 雨点图片texture
     */
    private Texture mDropImage;
    /**
     * 桶 图片texture
     */
    private Texture mBucketImage;

    /**
     * 水滴声
     */
    private Sound mDropSound;
    /**
     * 背景音乐：水声
     */
    private Music mRainMusic;

    private SpriteBatch mBatch;
    private OrthographicCamera mCamera;

    /**
     * 木桶的坐标方位，用矩形表示，用于碰撞检测
     */
    private Rectangle mBucket;
    /**
     * 水滴的集合
     */
    private Array<Rectangle> mRaindrops;

    private long mLastDropTime;

    /**
     * 资源初始化
     */
    @Override
    public void create() {
        // load the images for the droplet and the mBucket, 64x64 pixels each
        mDropImage = new Texture(Gdx.files.internal(DROPLET_PNG));
        mBucketImage = new Texture(Gdx.files.internal(BUCKET_PNG));

        // 加载水滴声效和背景下雨声
        // load the drop sound effect and the rain background "music"
        mDropSound = Gdx.audio.newSound(Gdx.files.internal(DROP_WAV));
        mRainMusic = Gdx.audio.newMusic(Gdx.files.internal(RAIN_MP3));

        //设置背景音乐一直循环
        // start the playback of the background music immediately
        mRainMusic.setLooping(false);
        mRainMusic.play();

        // create the mCamera and the SpriteBatch
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        mBatch = new SpriteBatch();

        //初始化木桶的坐标
        // create a Rectangle to logically represent the mBucket
        mBucket = new Rectangle();
        mBucket.x = SCREEN_WIDTH / 2 - WIDTH_BUCKET / 2; // center the mBucket horizontally
        mBucket.y = 20; // bottom left corner of the mBucket is 20 pixels above the bottom screen edge
        mBucket.width = WIDTH_BUCKET;
        mBucket.height = WIDTH_BUCKET;

        //初始化雨滴的坐标
        // create the mRaindrops array and spawn the first raindrop
        mRaindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        //初始化雨滴坐标
        Rectangle raindrop = new Rectangle();
        //随机初始化木桶的x位置
        raindrop.x = MathUtils.random(0, SCREEN_WIDTH - WIDTH_RAIN);
        raindrop.y = SCREEN_HEIGHT;
        raindrop.width = WIDTH_RAIN;
        raindrop.height = WIDTH_RAIN;
        //把这个雨滴添加到雨滴结合里面
        mRaindrops.add(raindrop);

        mLastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the mCamera to update its matrices.
        mCamera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the mCamera.
        mBatch.setProjectionMatrix(mCamera.combined);

        //————————————————————绘制逻辑开始————————————————————
        // begin a new batch and draw the bucket and
        // all drops
        //————场景开始————
        mBatch.begin();
        mBatch.draw(mBucketImage, mBucket.x, mBucket.y);//绘制木桶到屏幕上
        for (Rectangle raindrop : mRaindrops) {//遍历所有雨滴图片
            //绘制雨滴
            mBatch.draw(mDropImage, raindrop.x, raindrop.y);
        }
        //————场景结束————
        mBatch.end();
        //————————————————————绘制逻辑结束——————————————————

        //————————————————————控制逻辑开始——————————————————
        //判断玩家是触摸屏幕
        // process user input
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            mCamera.unproject(touchPos);
            //设置木桶坐标到玩家点击位置
            mBucket.x = touchPos.x - WIDTH_BUCKET / 2;
        }

//        //判断左键是否按下
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mBucket.x -= SPEED_MOVE * Gdx.graphics.getDeltaTime();
        }
        //判断右键是否按下
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mBucket.x += SPEED_MOVE * Gdx.graphics.getDeltaTime();
        }

        // 确保木桶始终在屏幕之内
        // make sure the mBucket stays within the screen bounds
        if (mBucket.x < 0) {
            mBucket.x = 0;
        }

        if (mBucket.x > SCREEN_WIDTH - WIDTH_BUCKET) {
            mBucket.x = SCREEN_WIDTH - WIDTH_BUCKET;
        }

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - mLastDropTime > 1000000000) {
            //创建一个雨滴
            spawnRaindrop();
        }
        //————————————————————控制逻辑结束——————————————————

        //————————————————回收资源——————————回收掉不需要的雨滴————————————
        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the mBucket. In the later case we play back
        // a sound effect as well.
        Iterator<Rectangle> iter = mRaindrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();
            //计算这个雨滴下次的y坐标
            raindrop.y -= SPEED_RAIN * Gdx.graphics.getDeltaTime();
            //雨滴消失
            if (raindrop.y + WIDTH_RAIN < 0) {
                iter.remove();//释放雨滴
            }
            //雨滴碰撞到木桶
            if (raindrop.overlaps(mBucket)) {
                mDropSound.play();
                iter.remove();//释放雨滴
            }
        }
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        mDropImage.dispose();
        mBucketImage.dispose();
        mDropSound.dispose();
        mRainMusic.dispose();
        mBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
