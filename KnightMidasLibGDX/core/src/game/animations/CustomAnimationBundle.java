
package game.animations;

import java.util.ArrayList;

public class CustomAnimationBundle {
    
    public ArrayList<CustomAnimation> animations;
        
    private CustomAnimationBundle() {}
    
    public CustomAnimation getByName(String name) {
        for (CustomAnimation anim : animations) {
            if (anim.name.equals(name)) return anim;
        }
        return null;
    }
}
