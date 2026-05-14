package skill.active;

import effect.Stun;
import entity.Steve;
import entity.mob.Mob;

public class SnowBall extends ActiveSkill {

    public SnowBall() {
        super("눈덩이", "단일 몹을 1턴 스턴시킨다.", 3);
    }

    @Override
    public void use(Steve steve, Mob target) {
        target.getEffects().add(new Stun());
        triggerCooldown();
        System.out.println("눈덩이를 던졌다! " + target.getName() + "이(가) 스턴됐다!");
    }
}