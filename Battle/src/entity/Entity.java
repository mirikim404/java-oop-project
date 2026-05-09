package entity;
import interfaces.Attackable;
import interfaces.Blockable;

public abstract class Entity implements Blockable, Attackable {
    private String name;
    private int health;
    private int attackPower;
    private int defencePower;

    public Entity() {} // 기본 생성자
    
    public Entity(String name, int health, int attackPower, int defencePower) { //생성자
    	System.out.println("Entity 초기화");
		this.name = name;
		this.health = health;
		this.attackPower = attackPower;
		this.defencePower = defencePower;
    }
    public abstract void attack();
    

    public abstract void block();
    

    public abstract void takeDamage();
}