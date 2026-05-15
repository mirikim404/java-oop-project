package entity.mob;

import ability.Shoot;
import entity.Entity;

public class Skeleton extends Mob {

	private final int DEFAULT_MAX_HEALTH = 15;
    private final int DEFAULT_ATTACK_POWER = 2;  
    private final int DEFAULT_DEFENCE_POWER = 3;
    private final int DROP_EXP = 10; 
    private final int DROP_COIN = 10;
    private final int EXTRA_DAMAGE = 1; // 스킬 발동 시 추가 데미지
	public Skeleton() {}
	
	public Skeleton (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
		setAbility(new Shoot(EXTRA_DAMAGE));
	}
	
	@Override
	public void attack(Entity target) {
		target.takeDamage(getAttackPower());
		System.out.println(getName() + "이" + target.getName()+ "에게 활을 쏩니다");

	}
	@Override
	public void block() {
		// TODO Auto-generated method stub
		
	}

}
