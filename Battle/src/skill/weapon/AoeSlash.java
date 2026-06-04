package skill.weapon;

import entity.Steve;
import entity.mob.Mob;
import weapon.Weapon;
import java.util.List;

public class AoeSlash extends WeaponSkill {
	private final int cooldown = 3;
	private int currentCooldown = 0; 
	
	public AoeSlash() {
		super("광역베기", "살아있는 전체 몹에게 데미지를 입힌다.");
	}
	
	public boolean isReady() {
		return currentCooldown == 0;
	}
	
	public void triggerCooldown() {
		currentCooldown = cooldown;
	}
	
	public void decrementCooldown() {
		if (currentCooldown > 0) currentCooldown--;
	}

	public void resetCooldown() {
		currentCooldown = 0;
	}

	public int getCooldown() {
		return cooldown;
	}

	public int getCurrentCooldown() {
		return currentCooldown;
	}
	
	@Override
	public void use(Steve steve, List<Mob> mobs, Weapon weapon) {
		for (Mob mob : mobs) {
			if (mob.isAlive()) {
				mob.takeDamage(steve.getAttackPower() + weapon.getAoeDamageBonus());
			}
		}
		triggerCooldown();
	}

}
