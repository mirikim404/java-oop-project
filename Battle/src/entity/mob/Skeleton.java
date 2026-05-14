package entity.mob;

import entity.Entity;

public class Skeleton extends Mob {

	private final int DEFAULT_MAX_HEALTH = 15;
    private final int DEFAULT_ATTACK_POWER = 2;  
    private final int DEFAULT_DEFENCE_POWER = 3;
    private final int DROP_EXP = 10; 
    private final int DROP_COIN = 10;
	public Skeleton() {}
	
	public Skeleton (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
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
