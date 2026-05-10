package entity;

import interfaces.Attackable;
import interfaces.Blockable;

public abstract class Entity implements Blockable, Attackable {
	private String name;
	private int health;
	private int attackPower;
	private int defencePower;
	private int maxHealth;

	public Entity() {} // 기본 생성자

	public Entity(String name, int maxHealth, int attackPower, int defencePower) { // 생성자
		System.out.println(name + "Entity 초기화");
		this.name = name;
		this.health = maxHealth;
		this.maxHealth = maxHealth;
		this.attackPower = attackPower;
		this.defencePower = defencePower;
	}

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
		this.health = health;
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
		setHealth(health + amount);
		System.out.println(name + "의 체력이 " + amount + " 회복되었습니다. 현재 체력: " + health + "/" + maxHealth);
	}

	@Override
	public void block() {
		System.out.println(name + "이/가 방어합니다.");
	}

	@Override
	public void takeDamage() {
		takeDamage(0);
	}

	public void takeDamage(int damage) {
		int finalDamage = damage - defencePower;

		if (finalDamage < 0) {
			finalDamage = 0;
		}

		setHealth(health - finalDamage);

		System.out.println(name + "이/가 " + finalDamage + "의 피해를 입었습니다. 현재 체력: " + health + "/" + maxHealth);
	}

	public abstract void attack();

	//public abstract void block();

	//public abstract void takeDamage();
}