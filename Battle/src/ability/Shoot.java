package ability;

import entity.Steve;
import entity.mob.Mob;

public class Shoot implements Mobability {
	private int extraDamage;
	
	public Shoot() {}
	
	public Shoot(int extraDamage) {
		this.extraDamage = extraDamage;
	}
    public void use(Mob attacker, Steve player) {
        System.out.println("화살이" + player.getName()+"을/를 관통하여"+ extraDamage + "만큼의 추가 피해를 입혔습니다");
    		attacker.attack(player);
    		player.takeDamage(extraDamage);
    		
    }

}
