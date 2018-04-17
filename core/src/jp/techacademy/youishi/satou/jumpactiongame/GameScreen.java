package jp.techacademy.youishi.satou.jumpactiongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Fujino_ya on 2018/04/07.
 */

public class GameScreen extends ScreenAdapter {
    static final float CAMERA_WIDTH = 10;
    static final float CAMERA_HEIGHT = 15;
    static final float WORLD_WIDTH = 10;
    static final float WORLD_HEIGHT = 15 * 5; // ２０画面分登れば終了
    static final float GUI_WIDTH = 320;
    static final float GUI_HEIGHT = 480;

    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;

    // 重力
    static final float GRAVITY = -12;

    private JumpActionGame mGame;

    Sprite mBg;
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewPort;

    Random mRandom;
    List<Step> mSteps;
    List<Star> mStars;
    Ufo mUfo;
    Player mPlayer;
    List<Enemy> mEnemy; // 課題

    float mHeightSoFar;
    int mGameState;
    Vector3 mTouchPoint;
    BitmapFont mFont;
    int mScore;
    int mHighScore;

    int meteoHeight = 20; // 課題
    int gameOverType = 0;   // 課題
    int[] soundTrack;   // 課題
    int textureFlag = 0;

    Long soundId;   // 課題
    Long interludeId;   // 課題

    float meteoX;   // 課題
    float stepX;    // 課題

    Sound mSoundEnemy;  // 課題
    Sound mSoundFall;   // 課題
    Sound mSoundMeteorite;  // 課題
    Sound mSoundGets;   // 課題
    Sound mSoundEnding; // 課題

    Texture meteoriteTexture;
    Texture meteoriteTexture1;
    Texture meteoriteTexture2;

    Music mBGM; // 課題

    Preferences mPrefs;

    public GameScreen(JumpActionGame game) {
        mGame = game;

        // 背景の準備
        Texture bgTexture = new Texture("back.png");
        // TextureRegionで切り出す時の原点は左上
        mBg = new Sprite( new TextureRegion(bgTexture, 0, 0, 540, 810));
        mBg.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        mBg.setPosition(0, 0);

        // カメラ、ViewPortを生成、設定する
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera);

        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);

        // メンバ変数の初期化
        mRandom = new Random();
        mSteps = new ArrayList<Step>();
        mStars = new ArrayList<Star>();
        mEnemy = new ArrayList<Enemy>(); // 課題
        mGameState = GAME_STATE_READY;
        mTouchPoint = new Vector3();
        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
        mFont.getData().setScale(0.8f);
        mScore = 0;
        mSoundEnemy = Gdx.audio.newSound(Gdx.files.internal("enemyCollisionSound.mp3")); // 課題
        mSoundFall = Gdx.audio.newSound(Gdx.files.internal("Fall.mp3"));    // 課題
        mSoundMeteorite = Gdx.audio.newSound(Gdx.files.internal("meteoriteSound.mp3")); // 課題
        mSoundGets = Gdx.audio.newSound(Gdx.files.internal("Gets.mp3"));    // 課題
        mSoundEnding = Gdx.audio.newSound(Gdx.files.internal("Ending.mp3"));    // 課題

        mBGM = Gdx.audio.newMusic(Gdx.files.internal("Bgm.mp3"));   // 課題

        // ハイスコアをPreferencesから取得する
        mPrefs = Gdx.app.getPreferences("jp.techacademy.youichi.satou.jumpactiongame");
        mHighScore = mPrefs.getInteger("HIGHSCORE", 0);

        createStage();
    }

    @Override
    public void render(float delta) {
        // それぞれの状態をアップデートする
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // カメラの中心を超えたらカメラを上に移動させる　つまりキャラが画面の上半分には絶対に行かない
        if (mPlayer.getY() > mCamera.position.y) {
            mCamera.position.y = mPlayer.getY();
        }

        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);

        mGame.batch.begin();

        // 背景
        // 原点は左下
        mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2);
        mBg.draw(mGame.batch);

        // Step
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).draw(mGame.batch);
        }

        // Star
        for (int i = 0; i < mStars.size(); i ++) {
            mStars.get(i).draw(mGame.batch);
        }

        // Enemy（課題）
        for (int i = 0; i < mEnemy.size(); i++) {
            mEnemy.get(i).draw(mGame.batch);
        }

        // UFO
        mUfo.draw(mGame.batch);

        // Player
        mPlayer.draw(mGame.batch);

        mGame.batch.end();

        // スコア表示
        mGuiCamera.update();
        mGame.batch.setProjectionMatrix(mGuiCamera.combined);
        mGame.batch.begin();
        mFont.draw(mGame.batch, "HighScore:" + mHighScore, 16, GUI_HEIGHT - 15);
        mFont.draw(mGame.batch, "Score:" + mScore, 16, GUI_HEIGHT - 35);
        mGame.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        mViewPort.update(width, height);
        mGuiViewPort.update(width, height);
    }

    // ステージを作成する
    private void createStage() {

        // テクスチャの準備
        Texture stepTexture = new Texture("step.png");
        Texture starTexture = new Texture("star.png");
        Texture playerTexture = new Texture("uma.png");
        Texture ufoTexture = new Texture("ufo.png");
        meteoriteTexture = new Texture("meteorite4_1.png"); // 課題
        meteoriteTexture1 = new Texture("meteorite4_2.png");
        meteoriteTexture2 = new Texture("meteorite4_3.png");

        // StepとStarをゴールの高さまで配置していく
        float y = 0;

        float maxJumpHeight = Player.PLAYER_JUMP_VELOCITY * Player.PLAYER_JUMP_VELOCITY / (2 * -GRAVITY);
        while (y < WORLD_HEIGHT - 5) {
            int type = mRandom.nextFloat() > 0.8f ? Step.STEP_TYPE_MOVING : Step.STEP_TYPE_STATIC;
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Step.STEP_WIDTH);

            Step step = new Step(type, stepTexture, 0, 0, 144, 36);
            step.setPosition(x, y);
            mSteps.add(step);

            if (mRandom.nextFloat() > 0.6f) {
                Star star = new Star(starTexture,0,0,72,72);
                star.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Star.STAR_HEIGHT + mRandom.nextFloat() * 3);
                mStars.add(star);
            }

            stepX = x;

            // 隕石を配置する（課題）
            if (y > meteoHeight) {
                Enemy enemy = new Enemy(meteoriteTexture, 0, 0, 30, 106);
                if (stepX < WORLD_WIDTH / 2) {
                    meteoX = (WORLD_WIDTH / 2) + ((WORLD_WIDTH / 2) * mRandom.nextFloat() - Enemy.ENEMY_WIDTH);
                } else {
                    meteoX = (WORLD_WIDTH / 2) * mRandom.nextFloat();
                }
                enemy.setPosition(meteoX, y);
                mEnemy.add(enemy);
                meteoHeight += 11;
            }

            y += (maxJumpHeight - 0.5f);
            y -= mRandom.nextFloat() * (maxJumpHeight / 3);

        }

        // 音楽トラックの初期値を設定する（課題）
        soundTrack = new int[mEnemy.size()];
        for (int i = 0; i > soundTrack.length; i++) {
            soundTrack[i] = 0;
        }

        // Playerを配置
        mPlayer = new Player(playerTexture,0,0,72,72);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, Step.STEP_HEIGHT);

        // コールのUFOを配置
        mUfo = new Ufo(ufoTexture, 0,0,120,74);
        mUfo.setPosition(WORLD_WIDTH / 2 - Ufo.UFO_WIDTH / 2 , y);

        // 音楽を再生する（課題）
        mBGM.setLooping(true);
        mBGM.play();
    }

    // それぞれのオブジェクトの状態をアップデートする
    private void update(float delta) {
        switch (mGameState) {
            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                mBGM.dispose(); // 課題
                updateGameOver();
                break;
        }
    }

    private void updateReady() {
        if (Gdx.input.justTouched()) {
            mGameState = GAME_STATE_PLAYING;
        }
    }

    private void updatePlaying(float delta) {
        float accel = 0;
        if (Gdx.input.isTouched()) {
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle left = new Rectangle(0,0,GUI_WIDTH / 2, GUI_HEIGHT);
            Rectangle right = new Rectangle(GUI_WIDTH / 2, 0, GUI_WIDTH / 2, GUI_HEIGHT);
            if (left.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = 5.0f;
            }
            if (right.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = -5.0f;
            }
        }

        // Step
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).update(delta);
        }

        // Enemy（課題）
        for (int i = 0; i < mEnemy.size(); i++) {

            if (textureFlag > 7) {
                mEnemy.get(i).setTexture(meteoriteTexture1);
                if (textureFlag == 10) {
                    textureFlag = 0;
                } else {
                    textureFlag += 1;
                }
            } else if (textureFlag > 3) {
                mEnemy.get(i).setTexture(meteoriteTexture2);
                textureFlag += 1;
            } else if (textureFlag >= 0) {
                mEnemy.get(i).setTexture(meteoriteTexture);

                textureFlag += 1;
            }

            if ((mEnemy.get(i).getY() - mPlayer.getY()) < 12.0f) {
                mEnemy.get(i).update(delta);
                meteoSoundPlay(i, mEnemy.get(i).getY());
            }
        }

        // Player
        if (mPlayer.getY() <= 0.5f) {
            mPlayer.hitStep();
        }
        mPlayer.update(delta, accel);
        mHeightSoFar = Math.max(mPlayer.getY(), mHeightSoFar);

        checkCollision();

        checkGameOver();
    }

    private void updateGameOver() {
        if (Gdx.input.justTouched()) {
            mSoundEnding.stop();
            mSoundEnding.dispose();
            mGame.setScreen(new ResultScreen(mGame, mScore));
        }
    }

    private void checkCollision() {
        // UFO(ゴールとの当たり判定)
        if (mPlayer.getBoundingRectangle().overlaps(mUfo.getBoundingRectangle())) {
            mSoundEnding.play(1.0f);    // 課題
            mGameState = GAME_STATE_GAMEOVER;
            return;
        }

        // Starとの当たり判定
        for (int i = 0; i < mStars.size(); i++) {
            Star star = mStars.get(i);

            if (star.mState == Star.STAR_NONE) {
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(star.getBoundingRectangle())) {
                star.get();
                mSoundGets.play(1.0f);  // 課題
                mScore++;
                if (mScore > mHighScore) {
                    mHighScore = mScore;
                    // ハイスコアをPreferencesに保存する
                    mPrefs.putInteger("HIGHSCORE", mHighScore);
                    mPrefs.flush();
                }
                break;
            }
        }


        // Enemyとの当たり判定（課題）
        for (int i = 0; i < mEnemy.size(); i++) {
            Enemy enemy = mEnemy.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                mSoundEnemy.play(2.0f);
                gameOverType = 1;   // GameOverの状態を判定する（0:落下 / 1:隕石と衝突）
                mGameState = GAME_STATE_GAMEOVER;
            }
        }

        // Stepとの当たり判定
        // 上昇中はStepとの当たり判定を確認しない
        if (mPlayer.velocity.y > 0) {
            return;
        }

        for (int i = 0; i < mSteps.size(); i++) {
            Step step = mSteps.get(i);

            if (step.mState == Step.STEP_STATE_VANISH) {
                continue;
            }

            if (mPlayer.getY() > step.getY()) {
                if (mPlayer.getBoundingRectangle().overlaps(step.getBoundingRectangle())) {
                    mPlayer.hitStep();

                    if (mRandom.nextFloat() > 0.5f) {
                        step.vanish();
                    }
                    break;
                }
            }
        }

    }

    private void checkGameOver() {
        if (mHeightSoFar - CAMERA_HEIGHT / 2 > mPlayer.getY()) {
            Gdx.app.log("JumpActionGame", "GAMEOVER");

            // 落下した時の専用 Sound を再生させる（課題）
            if (gameOverType != 1) {
                mSoundFall.play(2.0f);
            }

            mGameState = GAME_STATE_GAMEOVER;
        }
    }

    // 隕石の落下音を再生させる（課題）
    private void meteoSoundPlay(int i, float y) {
        if (soundTrack[i] == 0) {
            if (y - mPlayer.getY() < 8.0f) {
                soundId = mSoundMeteorite.play(1.0f);
                soundTrack[i] = 1;
            }
        }
    }

}
