package effect;

import entity.Entity;
import entity.mob.Mob;

public interface StatusEffect {

    void activate(Entity target);
    boolean isExpired();
    
}
