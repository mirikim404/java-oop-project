package skill.weapon;

import skill.Skill;
import weapon.Weapon;
import entity.Steve;
import entity.mob.Mob;
import java.util.List;

public abstract class WeaponSkill extends Skill {

	public WeaponSkill() {}
	
	public WeaponSkill(String name, String description) {
		super(name, description);
	}
	
	public abstract void use(Steve steve, List<Mob> mobs, Weapon weapon);
}
