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

	public Entity(String name, int maxHealth, int attackPower, int defencePower) { //생성자
		System.out.println("Entity 초기화");
		this.name = name;
		this.health = maxHealth;
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

public abstract void attack();


public abstract void block();


public abstract void takeDamage();
}