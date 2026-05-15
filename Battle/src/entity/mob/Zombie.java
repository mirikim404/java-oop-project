package entity.mob;

import ability.Heal;
import entity.Entity;

public class Zombie extends Mob {

	private final int DEFAULT_MAX_HEALTH = 10;
    private final int DEFAULT_ATTACK_POWER = 1;  
    private final int DEFAULT_DEFENCE_POWER = 1;
    private final int DROP_EXP = 5; 
    private final int DROP_COIN = 5;
	
	public Zombie() {}
	
	public Zombie (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
		setAbility(null); // 좀비는 능력 없음
	}
	
	@Override
	public void attack(Entity target) {
		target.takeDamage(getAttackPower());
		System.out.println("으으으으... 으으으으..." + target.getName() + "...");
		
	}

	@Override
	public void block() {
		
		
	}




}
