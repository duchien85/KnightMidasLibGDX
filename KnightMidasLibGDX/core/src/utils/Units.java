
package utils;

import game.Main;

public abstract class Units {
    
    //Convert pixels to meters
    public static float pixelsMeters(float pixels) {
        return pixels / Main.PPM;
    }
    
    //Convert meters to pixels
    public static float metersPixels(float meters) {
        return meters * Main.PPM;
    }
    
    
    //Round meters
    public static float roundMeters(float meters) {
        float pixels = metersPixels(meters);
        float roundedPixels = Math.round(pixels);
        
        return pixelsMeters(roundedPixels);
    }
}
