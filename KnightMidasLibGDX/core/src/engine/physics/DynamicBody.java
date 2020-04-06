
package engine.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DynamicBody extends PhysicsBody {
    
    public Vector2 velocity, acceleration;

    public DynamicBody(float x, float y, float width, float height) {
        super(x, y, width, height, BodyType.DYNAMIC);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
    }
    
    public DynamicBody(Rectangle rect) {
        super(rect, BodyType.DYNAMIC);
    }
    
    
    protected void beginCollision() {}
    
    protected void endCollision() {}
}
