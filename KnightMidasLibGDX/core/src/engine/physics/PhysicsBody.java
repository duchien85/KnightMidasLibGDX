
package engine.physics;

public class PhysicsBody {
    
    private Shape bounds;

    public PhysicsBody(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    protected PhysicsBody(float x, float y, float radius) {
        this.bounds = new Circle(x, y, radius);
    }
    
    public float getX() {
        return this.bounds.x;
    }
    
    public float getY() {
        return this.bounds.y;
    }
    
    public Shape getShape() {
        return this.bounds;
    }
}
