
package game.animations;

public enum SnakeState {
    
    SNAKE_WALK("snakeWalk"),
    SNAKE_HURT("snakeHurt");
    
    private final String stateName;

    private SnakeState(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
