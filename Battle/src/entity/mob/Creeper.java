package entity.mob;

import ability.Explode;
import entity.Entity;

public class Creeper extends Mob {
	
	private static final int DEFAULT_MAX_HEALTH = 35;
    private static final int DEFAULT_ATTACK_POWER = 12; //폭발 데미지 
    private static final int DEFAULT_DEFENCE_POWER = 3;
    private static final int EXPLOSION_TURNS = 2;
    private static final int DROP_EXP = 50; 
    private static final int DROP_COIN = 45;
    
	public Creeper () { // 기본 생성자
		super("크리퍼", DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, DROP_EXP, DROP_COIN);
		setAbility(new Explode(EXPLOSION_TURNS, DEFAULT_ATTACK_POWER));
	}
	
//	public Creeper (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
//		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
//		setAbility(new Explode());
//	}
	 
	@Override
	public void attack(Entity target) {
		//스킬 사용 이외에 추가 공격 방법 없음
		System.out.println("쉬시식...쉬시시식....");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void block() {
		super.block();
		
	}



}
