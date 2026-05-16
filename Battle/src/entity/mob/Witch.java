package entity.mob;

import ability.Mobability;
import entity.Entity;
import entity.Steve;
import ability.Heal;
public class Witch extends Mob implements Mobability{
	
	private static final int DEFAULT_MAX_HEALTH = 50;
    private static final int DEFAULT_ATTACK_POWER = 18; 
    private static final int DEFAULT_DEFENCE_POWER = 7;
    private static final int DROP_EXP = 65; 
    private static final int DROP_COIN = 55;
	private static final int HEAL_AMOUNT = 8; // 고유 능력 회복량
	private static final double HEAL_PERCENT = 0.5; // 회복 확률
	
	public Witch() {
		super("마녀", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP ,DROP_COIN);
		//마녀 능력 적용
		setAbility(new Heal(HEAL_AMOUNT, HEAL_PERCENT));
	}
	
//	public Witch(String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
//		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
//		//마녀 능력 적용
//		setAbility(new Heal(HEAL_AMOUNT));
//	}
//	
//	
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

	@Override
	public void use(Mob attacker, Steve player) {
		// TODO Auto-generated method stub
		
	}

	

}
