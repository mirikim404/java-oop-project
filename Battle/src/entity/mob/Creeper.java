package entity.mob;

import entity.Entity;

public class Creeper extends Mob {
	
	private final int DEFAULT_MAX_HEALTH = 20;
    private final int DEFAULT_ATTACK_POWER = 5; //폭발 데미지 
    private final int DEFAULT_DEFENCE_POWER = 5;
    private final int DROP_EXP = 15; 
    private final int DROP_COIN = 15;
    
	public Creeper () { // 기본 생성자
		
	}
	public Creeper (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
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
