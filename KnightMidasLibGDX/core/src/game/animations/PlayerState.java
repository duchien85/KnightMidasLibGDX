
package game.animations;

public enum PlayerState {
    
    HERO_IDLE("heroIdle"),
    HERO_WALK("heroWalk"),
    HERO_STAB("heroStab"),
    HERO_HURT("heroHurt"),
    HERO_JUMP("heroJump"),
    HERO_DUCK("heroDuck"),
    HERO_SPAWN("heroSpawn");
    
    private final String stateName;

    private PlayerState(String name) {
        this.stateName = name;
    }

    public String getStateName() {
        return stateName;
    }
}
