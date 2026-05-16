package entity.mob;


import entity.Entity;
import entity.Steve;
import ability.Mobability;
import ability.WitherDmg;

public class WitherSkeleton extends Mob implements Mobability {

	private static final int DEFAULT_MAX_HEALTH = 65;
    private static final int DEFAULT_ATTACK_POWER = 20; 
    private static final int DEFAULT_DEFENCE_POWER = 15;
    private static final int DROP_EXP = 40; 
    private static final int DROP_COIN = 35; 
    private static final int WITHERTURNS = 3; // 위더 효과 발현 턴수
    private static  final int WITHERDAMAGE = 7;
    
	public WitherSkeleton() {
		super("위더스켈레톤", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP, DROP_COIN);
		setAbility(new WitherDmg(WITHERTURNS, WITHERDAMAGE));
	}
//	public WitherSkeleton (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
//		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
//		setAbility(new WitherDmg(WITHERTURNS, WITHERDAMAGE));
//	}
	
	@Override
	public void attack(Entity target) {
		super.attack(target);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void block() {
		super.block();
		
	}
	@Override
	public void use(Mob attacker, Steve player) {
		// TODO Auto-generated method stub
		
	}



}
