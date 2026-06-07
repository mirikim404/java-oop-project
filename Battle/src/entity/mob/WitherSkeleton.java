package entity.mob;

import entity.Entity;
import ability.WitherDmg; // 

public class WitherSkeleton extends Mob { 

	private static final int DEFAULT_MAX_HEALTH = 68;
	private static final int DEFAULT_ATTACK_POWER = 24; 
	private static final int DEFAULT_DEFENCE_POWER = 10;
	private static final int DROP_EXP = 45; 
	private static final int DROP_COIN = 40; 
	private static final int WITHERTURNS = 3; 
	private static final int WITHERDAMAGE = 7;
    
	public WitherSkeleton() {
		super("위더스켈레톤", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP, DROP_COIN);
		setAbility(new WitherDmg(WITHERTURNS, WITHERDAMAGE));
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

}