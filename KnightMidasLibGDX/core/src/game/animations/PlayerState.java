
package game.animations;

public enum PlayerState {
    
    IDLE("heroIdle"),
    WALK("heroWalk"),
    STAB("heroStab"),
    HURT("heroHurt"),
    JUMP("heroJump"),
    DUCK("heroDuck"),
    SPAWN("heroSpawn");
    
    private final String stateName;

    private PlayerState(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
