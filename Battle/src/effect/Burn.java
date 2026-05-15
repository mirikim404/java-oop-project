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
        if (remainingTurns <= 0) {
            return;
        }
        
        System.out.println(mob.getName() + "이/가 화상 피해를 입었습니다. 데미지: " + damage);
        mob.takeDamage(damage);
        remainingTurns--;
    }

    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}