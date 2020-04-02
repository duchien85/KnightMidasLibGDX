
package engine.physics;

public abstract class Shape {
    
    protected float x, y;
    protected final ShapeType type;

    protected Shape(float x, float y, ShapeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}

enum ShapeType {
    RECTANGLE, CIRCLE;
}

class Rectangle extends Shape {
    private float width, height;

    public Rectangle(float x, float y, float width, float height) {
        super(x, y, ShapeType.RECTANGLE);
        this.width = width;
        this.height = height;
    }
}

class Circle extends Shape {
    private float radius;
    
    public Circle(float x, float y, float radius) {
        super(x, y, ShapeType.CIRCLE);
        this.radius = radius;
    }
}
