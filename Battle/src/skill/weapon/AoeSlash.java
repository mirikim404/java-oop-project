package skill.weapon;

import entity.Steve;
import entity.mob.Mob;
import skill.active.ActiveSkill;
import weapon.Weapon;

public class AoeSlash extends ActiveSkill {
	
	public AoeSlash() { 
		super("광역베기", "살아있는 대상에게 데미지를 입힌다.", 3);
	}

	@Override
	public void use(Steve steve, Mob target) {
		Weapon weapon = steve.getWeapon();
		int damage = 0;
		
		if (weapon != null) {
			damage = (int)((steve.getAttackPower() + weapon.getAoeDamageBonus()) * 1.3);
		} else {
			damage = (int)(steve.getAttackPower()); 
		}
		
		System.out.println("[시스템] " + target.getName() + "에게 들어갈 계산된 광역 데미지: " + damage);

		if (target.isAlive()) {
			target.takeDamage(damage);
		}
		
		triggerCooldown(); 
	}
}