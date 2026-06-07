package weapon;

import skill.weapon.AoeSlash;

public abstract class Weapon {
    protected String name;
    protected String description;
    protected int tier;
    protected int attackBonus;

    public Weapon() {}

    public Weapon(int attackBonus) {
    	this.attackBonus = attackBonus;
    }

    public Weapon(String name, String description, int tier, int attackBonus) {
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.attackBonus = attackBonus;
    }

    public String getId() {
        return getClass().getSimpleName();
    }

    public String getName() {
        return name == null ? getId() : name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public int getTier() {
        return tier;
    }

    public int getAttackBonus() {
    	return attackBonus;
    }

    public int getAoeDamageBonus() {
        return 0;
    }

    public AoeSlash getAoeSlash() {
        return null;
    }

}
