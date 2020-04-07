
package game;

public abstract class UnitHelper {
    
    //Pixel - Meters
    public static float pixelsToMeters(float pixels) {
        return pixels / Main.PPM;
    }
    
    public static float metersToPixels(float meters) {
        return meters * Main.PPM;
    }
}
