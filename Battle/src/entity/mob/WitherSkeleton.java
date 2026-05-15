package entity.mob;


import entity.Entity;
import entity.Steve;
import ability.Mobability;
import ability.WitherDmg;

public class WitherSkeleton extends Mob implements Mobability {

	private final int DEFAULT_MAX_HEALTH = 30;
    private final int DEFAULT_ATTACK_POWER = 15; 
    private final int DEFAULT_DEFENCE_POWER = 15;
    private final int DROP_EXP = 30; 
    private final int DROP_COIN = 30; 
    private final int WITHERTURNS = 2; // 위더 효과 발현 턴수
    private final int WITHERDAMAGE = 5;
	public WitherSkeleton() {}
	public WitherSkeleton (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
		setAbility(new WitherDmg(WITHERTURNS, WITHERDAMAGE));
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
	@Override
	public void use(Mob attacker, Steve player) {
		// TODO Auto-generated method stub
		
	}



}
