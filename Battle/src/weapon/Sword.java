package weapon;
import skill.weapon.AoeSlash;

public abstract class Sword extends Weapon {
    protected int aoeDamageBonus;
    protected AoeSlash aoeSlash;
    
    //기본 생성자
    public Sword() {}
    
    // 생성자
    public Sword(int attackBonus, int aoeDamageBonus) {
    	super(attackBonus);
    	this.aoeDamageBonus = aoeDamageBonus;
    	this.aoeSlash = new AoeSlash(); // 검 교체시 새로 생성
    }
    
    //getter
    public int getAoeDamageBonus() {
    	return aoeDamageBonus;
    }
    
    //getter
    public AoeSlash getAoeSlash() {
    	return aoeSlash;
    }
    

}
