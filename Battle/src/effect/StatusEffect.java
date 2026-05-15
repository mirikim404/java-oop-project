package effect;

import entity.mob.Mob;

public interface StatusEffect {

    void activate(Mob mob);
    boolean isExpired();
    
}
