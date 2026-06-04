package skill.active;

import entity.Steve;
import entity.mob.Mob;
import skill.Skill;

public abstract class ActiveSkill extends Skill {
    protected int cooldown;
    protected int currentCooldown = 0;

    public ActiveSkill() {}

    public ActiveSkill(String name, String description, int cooldown) {
    	super(name, description);
    	this.cooldown = cooldown;
    }

    public boolean isReady() {
    	return currentCooldown == 0;
    }

    public void triggerCooldown(){
    	currentCooldown = cooldown;
    }

    public void decrementCooldown(){
    	if (currentCooldown > 0) currentCooldown--;
    }

    public boolean skipsMobTurn() {
        return false;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCurrentCooldown() {
		return currentCooldown;
	}

	public void setCurrentCooldown(int currentCooldown) {
		this.currentCooldown = currentCooldown;
	}

	public abstract void use(Steve steve, Mob target);


}
