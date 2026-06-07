package skill.consumable;

import entity.Steve;

public class AttackPotion extends ConsumableSkill {
	
	public AttackPotion() {
		super("공격 포션", "이번 턴 공격력 2배", 0);
	}
	
	@Override
	public void use(Steve steve) {
		if (hasStock()) {
			steve.setAttackPower(steve.getAttackPower() * 2);
			quantity--;
			System.out.println("공격 포션 사용! 이번 턴 공격력 2배!");
		}
	}

}
