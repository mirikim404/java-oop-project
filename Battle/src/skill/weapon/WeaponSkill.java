package skill.weapon;

import skill.Skill;
import weapon.Sword;
import entity.Steve;
import entity.mob.Mob;
import java.util.List;

public abstract class WeaponSkill extends Skill {

	// 기본 생성자
	public WeaponSkill() {}
	
	// 생성자
	public WeaponSkill(String name, String description) {
		super(name, description);
	}
	
	// 추상 메서드
	public abstract void use(Steve steve, List<Mob> mobs, Sword sword);
}
