
package game;

import game.animations.HeroAnim;

public class HeroStateMachine {
    
    public Hero h;

    public HeroStateMachine(Hero hero) {
        this.h = hero;
    }
    
    
    protected void state() {
        
        switch (h.currentState) {
            
            case SPAWN:
                spawn();
                break;
                
            case IDLE:
                idle();
                break;
                
            case WALK:
                walk();
                break;
                
            case RUN:
                run();
                break;
                
            case JUMP:
                jump();
                break;
                
            case FALL:
                fall();
                break;
                
            case STAB:
                stab();
                break;
                
            case SPIN:
                spin();
                break;
                
            case HURT:
                hurt();
                break;
                
            case DEAD:
                dead();
                break;
        }
    }
    
    //States
    private void spawn() {
        h.currentAnim = HeroAnim.SPAWN;
                
        if (h.animations.get(h.currentAnim).isAnimationFinished(h.animationTimer.time))
            transitionToState(HeroState.SPAWN, HeroState.IDLE);
    }
    
    private void idle() {
        h.currentAnim = HeroAnim.IDLE;
                
        if (h.jump)
            transitionToState(HeroState.IDLE, HeroState.JUMP);
        else if (h.right || h.left) {
            h.walkTimer.start();
            transitionToState(HeroState.IDLE, HeroState.WALK);
        }
    }
    
    private void walk() {
        h.currentAnim = HeroAnim.WALK;
                
        if (h.jump)
            transitionToState(HeroState.WALK, HeroState.JUMP);
        else if (!h.right && !h.left) {
            h.walkTimer.reset();
            transitionToState(HeroState.WALK, HeroState.IDLE);
        }
        else if (h.walkTimer.time >= h.timeToRunSpeed) {
            transitionToState(HeroState.WALK, HeroState.RUN);
        }
    }
    
    private void run() {
        h.currentAnim = HeroAnim.RUN;
        
        if (h.jump)
            transitionToState(HeroState.RUN, HeroState.JUMP);
        else if (!h.right && !h.left) {
            h.walkTimer.reset();
            transitionToState(HeroState.WALK, HeroState.IDLE);
        }
    }
    
    private void jump() {
        h.currentAnim = HeroAnim.JUMP;
        
        if (h.velocity.y <= 0)
            transitionToState(HeroState.JUMP, HeroState.FALL);
    }
    
    private void fall() {
        h.currentAnim = HeroAnim.FALL;
        
        if (h.feetBottomCollided) {
            transitionToState(HeroState.FALL, HeroState.IDLE);
        }
    }
    
    private void stab() {
        h.currentAnim = HeroAnim.STAB;
    }
    
    private void spin() {
        h.currentAnim = HeroAnim.SPIN;
    }
    
    private void hurt() {
        h.currentAnim = HeroAnim.HURT;
    }
    
    private void dead() {
        h.currentAnim = HeroAnim.DEAD;
    }
    
    
    //Transition
    protected void transitionToState(HeroState oldState, HeroState newState) {
        h.previousState = oldState;
        h.currentState = newState;
    }
}
