package weapon;

public abstract class Weapon {
    protected int attackBonus;
    
    // 기본 생성자
    public Weapon() {}
    
    //생성자
    public Weapon(int attackBonus) {
    	this.attackBonus = attackBonus;
    }
    
    //getter
    public int getAttackBonus() {
    	return attackBonus;
    }

}
