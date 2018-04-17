package jp.techacademy.youishi.satou.jumpactiongame;

/**
 * Created by Fujino_ya on 2018/04/08.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.audio.Sound;


public class Player extends GameObject {
    // 横幅、高さ
    public static final float PLAYER_WIDTH = 1.0f;
    public static final float PLAYER_HEIGHT = 1.0f;

    // 状態（ジャンプ中、落ちている最中）
    public static final int PLAYER_STATE_JUMP = 0;
    public static final int PLAYER_STATE_FALL = 1;

    // 速度
    public static final float PLAYER_JUMP_VELOCITY = 11.0f;
    public static final float PLAYER_MOVE_VELOCITY = 20.0f;

    int mState;

    Sound mSoundJump = Gdx.audio.newSound(Gdx.files.internal("Jump.mp3"));    // 課題

    public Player(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        mState = PLAYER_STATE_FALL;
    }

    public void update(float delta, float accelX) {

        // 重力をプレイヤーの速度に加算し、速度から位置を計算する
        velocity.add(0, GameScreen.GRAVITY * delta);
        velocity.x = -accelX / 10 * PLAYER_MOVE_VELOCITY;
        setPosition(getX() + velocity.x * delta, getY() + velocity.y * delta);

        // y方向の速度が正（＝上方向）の時にSTATEがPLAYER_STATE_JUMPでなければPLAYER_STATE_JUMPにする
        if (velocity.y > 0) {
            if (mState != PLAYER_STATE_JUMP) {
                mState = PLAYER_STATE_JUMP;
            }
        }

        // y方向の速度が負（＝下方向）の時にSTATEがPLAYER_STATE_FALLでなければPLAYER_STATE_FALLにする
        if (velocity.y < 0) {
            if (mState != PLAYER_STATE_FALL) {
                mState = PLAYER_STATE_FALL;
            }
        }

        // 画面の端まで来たら反対側に移動させる
        if (getX() + PLAYER_WIDTH / 2 < 0) {
            setX(GameScreen.WORLD_WIDTH - PLAYER_WIDTH / 2);
        } else if (getX() + PLAYER_WIDTH / 2 > GameScreen.WORLD_WIDTH) {
            setX(0);
        }
    }

    public void hitStep() {
        velocity.y = PLAYER_JUMP_VELOCITY;
        mSoundJump.play(0.5f);  // 課題
        mState = PLAYER_STATE_JUMP;
    }
}
