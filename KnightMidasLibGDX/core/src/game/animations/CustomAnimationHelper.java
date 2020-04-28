
package game.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class CustomAnimationHelper {
    
    public static Array<TextureRegion> getTextureRegions(Array<CustomAnimationFrame> frames, Texture texture) {
        Array<TextureRegion> result = new Array();
        for (CustomAnimationFrame f : frames) {
            result.add(convertFrameToRegion(f, texture));
        }
        return result;
    }
    
    public static Animation.PlayMode getPlayMode(int index) {
        switch (index) {
            case 0:
                return Animation.PlayMode.NORMAL;
            case 1:
                return Animation.PlayMode.REVERSED;
            case 2:
                return Animation.PlayMode.LOOP;
            case 3:
                return Animation.PlayMode.LOOP_REVERSED;
            case 4:
                return Animation.PlayMode.LOOP_PINGPONG;
            case 5:
                return Animation.PlayMode.LOOP_RANDOM;
            default:
                return null;
        }
    }
    
    public static TextureRegion convertFrameToRegion(CustomAnimationFrame f, Texture texture) {
        return new TextureRegion(texture, f.x, f.y, f.width, f.height);
    }
}
