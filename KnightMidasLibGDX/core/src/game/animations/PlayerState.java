
package game.animations;

public enum PlayerState {
    
    IDLE("heroIdle"),
    HALF_WALK("heroHalfWalk"),
    FULL_WALK("heroFullWalk"),
    STAB("heroStab"),
    HURT("heroHurt"),
    JUMP("heroJump"),
    SPAWN("heroSpawn"),
    SPIN("heroSpin"),
    DEAD("heroDead");
    
    private final String stateName;

    private PlayerState(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
