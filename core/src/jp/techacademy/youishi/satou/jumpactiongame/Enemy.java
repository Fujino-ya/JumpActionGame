package jp.techacademy.youishi.satou.jumpactiongame;

/**
 * Created by Fujino_ya on 2018/04/11.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;



public class Enemy extends GameObject {
    // 横幅、高さ
    public static final float ENEMY_WIDTH = 1.1f;
    public static final float ENEMY_HEIGHT = 2.6f;

    // 速度
    public static final float ENEMY_VELOCITY = 2.0f;


    public Enemy(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT);
        velocity.y = ENEMY_VELOCITY;
    }

    @Override
    public Rectangle getBoundingRectangle () {
        Rectangle rectangle = super.getBoundingRectangle();

        rectangle.x = rectangle.x + 0.2f;
        rectangle.width = rectangle.width - 0.5f;
        rectangle.y = rectangle.y + 0.3f;
        rectangle.height = rectangle.height / 2 - 0.3f;

        return rectangle;
    }

    // 座標を更新する
    public void update(float deltaTime) {
        setY(getY() - velocity.y * deltaTime);
    }

}
