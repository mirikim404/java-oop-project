package entity.mob;

public class ZombieTest extends Mob {

    public ZombieTest() {
        super("좀비(테스트)", 30, 3, 1, 10, 5);
    }

    @Override
    public void attack(entity.Entity target) {
        System.out.println(getName() + "이/가 " + target.getName() + "을/를 공격합니다.");
        target.takeDamage(getAttackPower());
    }
}