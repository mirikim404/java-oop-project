package ability;

import entity.Steve;
import entity.mob.Mob;
import java.util.Random;

public class DoubleAttack implements Mobability {

    private static final int TRIGGER_CHANCE = 30; 
    private Random random = new Random();

    @Override
    public void use(Mob attacker, Steve player) {
        if (new Random().nextInt(100) < 30) {
            System.out.println(attacker.getName()+ "이(가) 분노하며 빠르게 두 번 공격합니다!");
            attacker.attack(player);
            attacker.attack(player);
        } else {
            attacker.attack(player);
        }
    }
}