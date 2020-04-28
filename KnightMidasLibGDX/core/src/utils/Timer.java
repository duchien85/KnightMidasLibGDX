
package utils;

public class Timer {
    
    public boolean active;
    public float time;

    public Timer() {
        active = false;
        time = 0f;
    }
    
    
    public void start() {
        active = true;
    }
    
    public void update(float dt) {
        if (active)
            time += dt;
    }
    
    public void pause() {
        active = false;
    }
    
    public void clear() {
        time = 0;
    }
    
    public void reset() {
        clear();
        pause();
    }
}
