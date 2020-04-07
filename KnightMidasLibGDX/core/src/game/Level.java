
package game;

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
    
    private ArrayList<GameObject> entities;
    public ArrayList<Rectangle> walls;

    public Level(String mapPath) {
        this.mapLoader = new TmxMapLoader();
        this.map = this.mapLoader.load(mapPath);
        
        entities = new ArrayList<>();
        createWalls();
    }
    
    private void createWalls() {
        
        Array<RectangleMapObject> list = map.getLayers().get(StringPaths.tiled_WallsLayer)
                .getObjects().getByType(RectangleMapObject.class);
        walls = new ArrayList<>();
        
        for (MapObject object : list) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            walls.add( new Rectangle(
                    UnitHelper.pixelsToMeters(rect.x),
                    UnitHelper.pixelsToMeters(rect.y),
                    UnitHelper.pixelsToMeters(rect.width),
                    UnitHelper.pixelsToMeters(rect.height)));
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
