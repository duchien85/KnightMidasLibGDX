
package game.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class AnimationHelper {
    
    public static Array<TextureRegion> getTextureRegions(Array<CustomAnimationFrame> frames, Texture texture) {
        Array<TextureRegion> result = new Array();
        for (CustomAnimationFrame f : frames) {
            result.add(convertFrameToRegion(f, texture));
        }
        return result;
    }
    
    public static TextureRegion convertFrameToRegion(CustomAnimationFrame f, Texture texture) {
        return new TextureRegion(texture, f.x, f.y, f.width, f.height);
    }
}
