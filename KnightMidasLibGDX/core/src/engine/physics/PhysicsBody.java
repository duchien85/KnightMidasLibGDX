
package engine.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class PhysicsBody {
    
    protected Rectangle bounds;
    protected BodyType type;
    protected boolean isActive;

    
    protected PhysicsBody(float x, float y, float width, float height, BodyType type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;
        this.isActive = true;
    }
    
    protected PhysicsBody(Rectangle rect, BodyType type) {
        this.type = type;
    }
    
    
    public float getX() {
        return this.bounds.getX();
    }
    
    public float getY() {
        return this.bounds.getY();
    }
    
    public float getWidth() {
        return this.bounds.getWidth();
    }
    
    public float getHeight() {
        return this.bounds.getHeight();
    }
    
    public Vector2 getPosition() {
        return new Vector2(this.bounds.x, this.bounds.y);
    }
    
    public void setPosition(Vector2 position) {
        this.bounds.setPosition(position);
    }
}

enum BodyType {
    DYNAMIC, STATIC;
}
