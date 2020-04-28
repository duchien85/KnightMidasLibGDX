
package game.animations;

public enum HeroAnim {
    
    IDLE("heroIdle"),
    WALK("heroWalk"),
    RUN("heroRun"),
    STAB("heroStab"),
    HURT("heroHurt"),
    JUMP("heroJump"),
    FALL("heroFall"),
    SPAWN("heroSpawn"),
    SPIN("heroSpin"),
    DEAD("heroDead");
    
    private final String stateName;

    private HeroAnim(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
