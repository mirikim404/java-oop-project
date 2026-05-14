package effect;

import entity.mob.Mob;

public class Stun implements StatusEffect {

    @Override
    public void activate(Mob mob) {
        mob.setStunned(true);
    }
}