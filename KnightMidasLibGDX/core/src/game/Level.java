
package game;

import utils.Units;
import utils.StringPaths;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;

public class Level implements Disposable {
    
    protected final TmxMapLoader mapLoader;
    protected final TiledMap map;
    
    public ArrayList<Rectangle> walls;
    protected ArrayList<Snake> snakes;
    protected Rectangle key;
    protected Rectangle chest;

    public Level(String mapPath) {
        this.mapLoader = new TmxMapLoader();
        this.map = this.mapLoader.load(mapPath);
        
        createWalls();
        createSnakes();
        createKey();
        createChest();
    }
    
    private void createWalls() {
        
        Array<RectangleMapObject> list = map.getLayers().get(StringPaths.tiled_WallsLayer)
                .getObjects().getByType(RectangleMapObject.class);
        walls = new ArrayList<>();
        
        for (MapObject object : list) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            walls.add( new Rectangle(
                    Units.pixelsMeters(rect.x),
                    Units.pixelsMeters(rect.y),
                    Units.pixelsMeters(rect.width),
                    Units.pixelsMeters(rect.height)));
        }
    }
    
    private void createSnakes() {
        
        Array<RectangleMapObject> list = map.getLayers().get(StringPaths.tiled_SnakesLayer)
                .getObjects().getByType(RectangleMapObject.class);
        snakes = new ArrayList<>();
        
        for (MapObject object : list) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            snakes.add( new Snake(this,
                    Units.pixelsMeters(rect.x),
                    Units.pixelsMeters(rect.y)));
        }
    }
    
    private void createKey() {
        
        Array<RectangleMapObject> list = map.getLayers().get(StringPaths.tiled_KeyLayer)
                .getObjects().getByType(RectangleMapObject.class);
        
        for (MapObject object : list) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            if (list.size == 1) {
                key = new Rectangle(
                    Units.pixelsMeters(rect.x),
                    Units.pixelsMeters(rect.y),
                    Units.pixelsMeters(rect.width),
                    Units.pixelsMeters(rect.height));
            }
        }
    }
    
    private void createChest() {
        
        Array<RectangleMapObject> list = map.getLayers().get(StringPaths.tiled_ChestLayer)
                .getObjects().getByType(RectangleMapObject.class);
        
        for (MapObject object : list) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            if (list.size == 1) {
                chest = new Rectangle(
                    Units.pixelsMeters(rect.x),
                    Units.pixelsMeters(rect.y),
                    Units.pixelsMeters(rect.width),
                    Units.pixelsMeters(rect.height));
            }
        }
    }
    

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
