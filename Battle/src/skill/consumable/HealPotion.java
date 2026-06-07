package skill.consumable;

import entity.Steve;

public class HealPotion extends ConsumableSkill {
	
	private static final int HEAL_AMOUNT = 20;
	
	// 생성자(기본)
	public HealPotion() {
		super("회복 포션", "체력을 20 회복한다.", 0);
	}
	
	// TODO: Steve.getHealth(), getMaxHealth(), setHealth() 추가 필요
	@Override
	public void use(Steve steve) {
		if (hasStock()) {
			int healed = Math.min(steve.getMaxHealth() - steve.getHealth(), HEAL_AMOUNT);
			steve.heal(HEAL_AMOUNT);
			quantity--;
			System.out.println("회복 포션 사용! 체력이 " + healed + "회복됐다!");
		}
	}

}
