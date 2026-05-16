package entity;

import interfaces.Attackable;
import interfaces.Blockable;

public abstract class Entity implements Blockable, Attackable {
	private String name;
	private int health;
	private int attackPower;
	private int defencePower;
	private int maxHealth;
	
	protected boolean isStunned = false;

	public Entity() {
	} // 기본 생성자

	public Entity(String name, int maxHealth, int attackPower, int defencePower) { // 생성자
		System.out.println(name + "Entity 초기화");
		this.name = name;
		this.health = maxHealth;
		this.maxHealth = maxHealth;
		this.attackPower = attackPower;
		this.defencePower = defencePower;
	}
	
	public boolean isStunned() { return isStunned; }
	public void setStunned(boolean stunned) { this.isStunned = stunned; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {	
		// hp 0 미만, maxHealth 초과 방지
        if (health < 0) {
            this.health = 0;
        } else if (health > maxHealth) {
            this.health = maxHealth;
        } else {
            this.health = health;
        }
	}

	public int getAttackPower() {
		return attackPower;
	}

	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}

	public int getDefencePower() {
		return defencePower;
	}

	public void setDefencePower(int defencePower) {
		this.defencePower = defencePower;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;

		if (health > maxHealth) { // 현재체력 > 최대체력 방지
			health = maxHealth;
		}
	}

	public boolean isAlive() {
		return health > 0;
	}

	public void heal(int amount) {
		if(amount <= 0) {
			System.out.println("회복량이 없습니다.");
			return;
		}
		int beforeHealth = health;
		setHealth(health + amount);
		int actualHealed = health - beforeHealth;
		
		System.out.println(name + "의 체력이 " + actualHealed + " 회복되었습니다. 현재 체력: " + health + "/" + maxHealth);
	}


	public void takeDamage(int damage) {
		int actual = Math.max(1, damage - getDefencePower()); // 최소 1은 들어가도록
	    this.setHealth(getHealth() - actual);
	    System.out.println("현재 체력: " + getHealth());
	}
	
	public abstract void attack(Entity target);

	public abstract void block();

}