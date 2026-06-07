package entity.mob;

import entity.Entity;
import ability.Heal; 

public class Witch extends Mob { 
	
	private static final int DEFAULT_MAX_HEALTH = 50;
	private static final int DEFAULT_ATTACK_POWER = 18; 
	private static final int DEFAULT_DEFENCE_POWER = 7;
	private static final int DROP_EXP = 35; 
	private static final int DROP_COIN = 30;
	private static final int HEAL_AMOUNT = 8; 
	private static final double HEAL_PERCENT = 0.5; 
	
	public Witch() {
		super("마녀", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP ,DROP_COIN);
		setAbility(new Heal(HEAL_AMOUNT, HEAL_PERCENT));
	}
	
	@Override
	public void attack(Entity target) {
		super.attack(target);
		// TODO Auto-generated method stub
	}

	@Override
	public void block() {
		super.block();
	}
	
	public int getHEAL_AMOUNT() {
		return HEAL_AMOUNT;
	}
 
}