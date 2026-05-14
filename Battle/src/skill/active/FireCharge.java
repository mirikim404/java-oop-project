package skill.active;

import effect.Burn;
import entity.Steve;
import entity.mob.Mob;

public class FireCharge extends ActiveSkill {
    private static final int DAMAGE = 5;
    private static final int BURN_TURNS = 2;
    private static final int BURN_DAMAGE = 3;

    public FireCharge() {
        super("화염구", "소량 데미지 + 2턴 화상을 입힌다.", 3);
    }

    @Override
    public void use(Steve steve, Mob target) {
        target.takeDamage(DAMAGE);
        target.getEffects().add(new Burn(BURN_TURNS, BURN_DAMAGE));
        triggerCooldown();
        System.out.println("화염구 발사! " + target.getName() + "이(가) 화상을 입었다!");
    }
}