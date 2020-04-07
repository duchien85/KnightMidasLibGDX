
package game.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class CustomAnimationJsonReader {
    
    public static CustomAnimationBundle getFrames(String jsonPath) {
        
        FileHandle file = Gdx.files.internal(jsonPath);
        String jsonText = file.readString();
        
        Json json = new Json();
        CustomAnimationBundle bundle = json.fromJson(CustomAnimationBundle.class, jsonText);
        
        return bundle;
    }
}
