package entity.mob;

import entity.Entity;
import entity.Steve;

public class EnderDragon extends Mob {
    private int phase;
    private final int BREATHDAMAGE = 15;
    private final double BLOCK_PERCENT = 0.5;
    private final int CHARGE_DAMAGE = 35;

    public EnderDragon() {
        super("엔더드래곤", 500, 35, 20, 0, 0); // 여기서 스탯 고정
        setAbility(null);
        phase = 1;
        showPhase();
    }

    public void whatPhase() {
        double hpRatio = (double) getHealth() / getMaxHealth();
        if (hpRatio > 0.667) phase = 1;
        else if (hpRatio > 0.333) phase = 2;
        else phase = 3;
    }

    public void act(Steve player) {
        whatPhase();
        switch (phase) {
            case 1:
                breathFire(player);
                break;
            case 2:
                fly();
                if (!isBlock) attack(player);
                isBlock = false;
                break;
            case 3:
                chargeAttack(player);
                break;
            default:
                attack(player);
        }
    }

    public void breathFire(Steve player) {
        System.out.println(getName() + "이 breath 공격을 합니다!");
        this.attack(player);
        player.takeDamage(BREATHDAMAGE);
    }

    public void fly() {
        System.out.println(getName() + "이(가) 날아오릅니다");
        if (Math.random() < BLOCK_PERCENT) {
            block();
        }
    }

    public void chargeAttack(Steve player) {
        System.out.println(getName() + "이(가) 돌진 공격을 사용합니다!//방어력 무시//");
        player.takeDamage(CHARGE_DAMAGE + player.getDefencePower());
        System.out.println(player.getName() + "이(가) " + CHARGE_DAMAGE + "의 고정 피해를 입었습니다.");
    }

    @Override
    public void attack(Entity target) {
        super.attack(target);
    }

    @Override
    public void block() {
        super.block();
    }

    public void showPhase() {
        System.out.println("========[" + phase + "단계]========");
        System.out.println("========[체력:" + getHealth() + "]========");
    }
}