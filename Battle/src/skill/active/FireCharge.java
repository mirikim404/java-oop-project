package skill.active;

import entity.Steve;
import entity.mob.Mob;

public class FireCharge extends ActiveSkill {
	private static final int DAMAGE = 5;
	private static final int BURN_TURNS = 2;
	
	// 생성자(기본)
	public FireCharge() {
		super("화염구", "소량 데미지 + 2턴 화상을 입힌다.", 3);
	}
	
	// TODO: Mob.takeDamage() 구현 확인 필요
	// TODO: Mob.setBurnTurns() 추가 필요
	// TODO: Entity.getName() 추가 필요
	@Override
	public void use(Steve steve, Mob target) {
		target.takeDamage(DAMAGE);
		target.setBurnTurns(BURN_TURNS);
		triggerCooldown();
		System.out.println("화염구 발사!" + target.getName() + "이(가) 화상을 입었다!");
	}

}
