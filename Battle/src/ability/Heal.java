package ability;


import entity.Steve;
import entity.mob.Mob;
import entity.mob.Witch;

public class Heal implements Mobability {
	private int healAmount;
	
	public Heal() {
		System.out.println("인자에 회복량을 입력하세요");
	}
	public Heal(int healAmount) {
		this.healAmount = healAmount;
	}
	public void use(Mob attacker, Steve player) {
		//Steve player 사용 안함
		// 현재 체력 + 회복량만큼 회복
		if (attacker instanceof Witch w) {
			System.out.println(attacker.getName() + "이 스스로 자신을 치료합니다");
			attacker.heal(healAmount);
		}
	}
	
}
