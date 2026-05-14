package skill.consumable;

import entity.Steve;
import skill.Skill;

public abstract class ConsumableSkill extends Skill {

    protected int quantity;

    // 생성자
    public ConsumableSkill() {}

    public ConsumableSkill(String name, String description, int quantity) {
        super(name, description);
        this.quantity = quantity;
    }

    // 메서드
    public boolean hasStock() {
        return quantity > 0;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    // getter, setter
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // 추상 메서드
    public abstract void use(Steve steve);
}