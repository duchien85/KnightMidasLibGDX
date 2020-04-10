
package game.animations;

import com.badlogic.gdx.utils.Array;

public class CustomAnimation {
    
    protected String name;
    public float time;
    public int framesLength;
    public int playMode;
    public Array<CustomAnimationFrame> frames;
}

class CustomAnimationFrame {
    protected int x, y, width, height;
}