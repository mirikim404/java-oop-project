package effect;

import entity.Entity;

public class Wither implements StatusEffect {

    private int remainingTurns;
    private int damage;

    public Wither(int turns, int damage) {
        this.remainingTurns = turns;
        this.damage = damage;
    }

    @Override
    public void activate(Entity target) {
        if (remainingTurns <= 0) return;

        System.out.println(target.getName() + "이/가 위더 피해를 입었습니다. 데미지: " + damage);
        target.takeDamage(damage);
        remainingTurns--;
        System.out.println("남은 턴 수 : " + remainingTurns );
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}