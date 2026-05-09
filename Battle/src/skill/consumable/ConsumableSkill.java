package skill.consumable;

import entity.Steve;
import skill.Skill;

public abstract class ConsumableSkill extends Skill {
    protected int quantity;
    
    // 기본 생성자
    public ConsumableSkill() {}
    
    // 생성자
    public ConsumableSkill(String name, String description, int quantity) {
    	super(name, description);
    	this.quantity = quantity;
    }
    
    // 메서드
    public boolean hasStock() {
    	return quantity > 0;
    }
    
    // 추상 메서드
    public abstract void use(Steve steve);

}
