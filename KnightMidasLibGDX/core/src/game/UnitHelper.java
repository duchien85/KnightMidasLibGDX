
package game;

public abstract class UnitHelper {
    
    //Pixel - Meters
    public static float pixelsToMeters(float pixels) {
        return pixels / Main.PPM;
    }
    
    public static float metersToPixels(float meters) {
        return meters * Main.PPM;
    }
    
    
    //Round meters
    public static float roundMeters(float meters) {
        float pixels = metersToPixels(meters);
        float roundedPixels = Math.round(pixels);
        
        return pixelsToMeters(roundedPixels);
    }
}
