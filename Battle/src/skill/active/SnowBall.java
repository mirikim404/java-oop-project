package skill.active;

import entity.Steve;
import entity.mob.Mob;

public class SnowBall extends ActiveSkill {
	
	// 생성자(기본)
	public SnowBall() {
		super("눈덩이", "단일 몹을 1턴 스턴시킨다.", 3);
	}
	
	// TODO: Mob.setStunned() 추가 필요
	// TODO: Entity.getName() 추가 필요
	@Override
	public void use(Steve steve, Mob target) {
		target.setStunned(true);
		triggerCooldown();
		System.out.println("눈동이를 던졌다!" + target.getName() + "이(가) 스턴됐다!");
	}

}
