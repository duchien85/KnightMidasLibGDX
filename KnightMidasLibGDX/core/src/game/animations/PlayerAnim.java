
package game.animations;

public enum PlayerAnim {
    
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

    private PlayerAnim(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
