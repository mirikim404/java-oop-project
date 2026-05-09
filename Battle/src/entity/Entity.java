package entity;
import interfaces.Attackable;
import interfaces.Blockable;

public class Entity implements Blockable, Attackable {
    private String name;
    private int health;
    private int attackPower;
    private int defencePower;

    public void attack(){
    }

    public void block(){
    }

    public void takeDamage(){
    }

}
