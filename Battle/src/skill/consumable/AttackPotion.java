package skill.consumable;

import entity.Steve;

public class AttackPotion extends ConsumableSkill {
	
	public AttackPotion() {
		super("공격 포션", "이번 턴 공격력 2배", 0);
	}
	
	// TODO: Steve.setAttackPower() 추가 필요
	// TODO: 턴 종료 시 공격력 원래대로 복구 로직 필요 (전투 담당 팀원에게 요청)
	@Override
	public void use(Steve steve) {
		if (hasStock()) {
			steve.setAttackPower(steve.getAttackPower() * 2);
			quantity--;
			System.out.println("공격 포션 사용! 이번 턴 공격력 2배!");
		}
	}

}
