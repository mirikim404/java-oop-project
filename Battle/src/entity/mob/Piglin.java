package entity.mob;

import ability.DoubleAttack;
import entity.Entity;

public class Piglin extends Mob {
	private static final int DEFAULT_MAX_HEALTH = 82;
    private static final int DEFAULT_ATTACK_POWER = 24;  
    private static final int DEFAULT_DEFENCE_POWER = 14;
    private static final int DROP_EXP = 60; 
    private static final int DROP_COIN = 55;
	public Piglin () {
		super("피글린", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP,DROP_COIN);
		setAbility(new DoubleAttack());
	} 
	
	
	@Override
	public void attack(Entity target) {
		super.attack(target);
		System.out.println(getName() + "이(가) 외칩니다.  '금 냄새가 난다... 내놔!'");
	
		
	}

	@Override
	public void block() {
		super.block();
		
	}



}
