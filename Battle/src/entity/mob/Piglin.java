package entity.mob;

import ability.DoubleAttack;
import entity.Entity;

public class Piglin extends Mob {
	private static final int DEFAULT_MAX_HEALTH = 82;
    private static final int DEFAULT_ATTACK_POWER = 28;  
    private static final int DEFAULT_DEFENCE_POWER = 14;
    private static final int DROP_EXP = 110; 
    private static final int DROP_COIN = 95;
	public Piglin () {
		super("피글린", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP,DROP_COIN);
		setAbility(new DoubleAttack());
	} // 기본 생성자
	
//	public Piglin (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
//		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
//		setAbility(new DoubleAttack());
//	}
	
	
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
