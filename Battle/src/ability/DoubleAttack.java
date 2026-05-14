package ability;


import entity.Steve;
import entity.mob.Mob;

public class DoubleAttack implements Mobability {
	
	@Override
    public void use(Mob attacker, Steve player) {
		System.out.println(attacker.getName()+ "이 빠르게 두번 공격합니다");
		attacker.attack(player);
		attacker.attack(player);
    }


}
