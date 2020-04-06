
package engine.physics;

import com.badlogic.gdx.math.Rectangle;

public class StaticBody extends PhysicsBody {
    
    public StaticBody(float x, float y, float width, float height) {
        super(x, y, width, height, BodyType.STATIC);
    }
    
    public StaticBody(Rectangle rect) {
        super(rect, BodyType.STATIC);
    }
}
