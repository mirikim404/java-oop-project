package weapon;
import skill.weapon.AoeSlash;

public abstract class Sword extends Weapon {
    protected int aoeDamageBonus;
    protected AoeSlash aoeSlash;

    public Sword() {}

    public Sword(int attackBonus, int aoeDamageBonus) {
    	super(attackBonus);
    	this.aoeDamageBonus = aoeDamageBonus;
    	this.aoeSlash = new AoeSlash();
    }

    public Sword(String name, String description, int tier, int attackBonus, int aoeDamageBonus) {
    	super(name, description, tier, attackBonus);
    	this.aoeDamageBonus = aoeDamageBonus;
    	this.aoeSlash = new AoeSlash();
    }

    @Override
    public int getAoeDamageBonus() {
    	return aoeDamageBonus;
    }

    @Override
    public AoeSlash getAoeSlash() {
    	return aoeSlash;
    }

}
