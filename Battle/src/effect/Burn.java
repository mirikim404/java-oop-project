package effect;

import entity.mob.Mob;

public class Burn implements StatusEffect {
    private int remainingTurns;
    private int damage;

    public Burn(int turns, int damage) {
        this.remainingTurns = turns;
        this.damage = damage;
    }

    @Override
    public void activate(Mob mob) {
        mob.takeDamage(damage);
        remainingTurns--;
    }

    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}