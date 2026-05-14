package entity.mob;

import entity.Entity;

public class Witch extends Mob {
	
	private final int DEFAULT_MAX_HEALTH = 30;
    private final int DEFAULT_ATTACK_POWER = 15; 
    private final int DEFAULT_DEFENCE_POWER = 15;
    private final int DROP_EXP = 30; 
    private final int DROP_COIN = 30;
	
	public Witch() {}
	
	public Witch(String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
	}
	
	@Override
	public void attack(Entity target) {
		target.takeDamage(getAttackPower());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void block() {
		// TODO Auto-generated method stub
		
	}


}
