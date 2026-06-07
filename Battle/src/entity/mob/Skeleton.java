package entity.mob;

import ability.Shoot;
import entity.Entity;

public class Skeleton extends Mob {

	private static final int DEFAULT_MAX_HEALTH = 34;
    private static final int DEFAULT_ATTACK_POWER = 22;  
    private static final int DEFAULT_DEFENCE_POWER = 4;
    private static final int DROP_EXP = 25; 
    private static final int DROP_COIN = 20;
    private static final int EXTRA_DAMAGE = 3; 
	public Skeleton() {
		super("스켈레톤", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP, DROP_COIN );
		setAbility(new Shoot(EXTRA_DAMAGE));
	}
	
	@Override
	public void attack(Entity target) {
		super.attack(target);
		System.out.println(getName() + "이" + target.getName()+ "에게 활을 쏩니다");

	}
	@Override
	public void block() {
		super.block();
		
	}

}
