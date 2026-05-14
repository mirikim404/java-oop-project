package ability;

import entity.Steve;
import entity.mob.Mob;

public class Explode implements Mobability {
	
	
	@Override
	public void use(Mob attacker, Steve player) {
		
		attacker.attack(player); // 공격
		attacker.setHealth(0); // 자폭(사망처리)
		
	}



}
