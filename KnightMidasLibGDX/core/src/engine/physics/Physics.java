
package engine.physics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Physics {
    
    private ArrayList<DynamicBody> dynamicBodies;
    private ArrayList<StaticBody> staticBodies;
    private Vector2 positionTemp;

    public Physics() {
        dynamicBodies = new ArrayList<>();
        staticBodies = new ArrayList<>();
    }
    
    public void update(float dt) {
        bodies(dt);
        collisions(dt);
    }
    
    private void bodies(float dt) {
        
        for (DynamicBody body : dynamicBodies) {
            
            body.velocity.x += body.acceleration.x * dt;
            body.velocity.y += body.acceleration.y * dt;
            
            positionTemp = new Vector2(body.getX() + body.velocity.x * dt,
                    body.getY() + body.velocity.y * dt);
            
            body.setPosition(positionTemp);
        }
    }
    
    private void collisions(float dt) {
        for (int i = 0; i < dynamicBodies.size(); i++) {
            for (int j = i+1; j < dynamicBodies.size(); j++) {
                if (dynamicBodies.get(i).isActive || dynamicBodies.get(j).isActive) {
                    checkDynamicCollision(dynamicBodies.get(i), dynamicBodies.get(j));
                }
            }
        }
    }
    
    private void checkDynamicCollision(DynamicBody bodyA, DynamicBody bodyB) {
        if (bodyA.bounds.contains(bodyB.bounds)) {
            
        }
    }
    
    public void addDynamicBody(DynamicBody body) {
        this.dynamicBodies.add(body);
    }
    
    public void addStaticBody(StaticBody body) {
        this.staticBodies.add(body);
    }
}