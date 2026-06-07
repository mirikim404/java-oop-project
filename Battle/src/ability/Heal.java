package ability;


import entity.Steve;
import entity.mob.Mob;
import entity.mob.Witch;

public class Heal implements Mobability {
	private int healAmount;
	private double healPercent;
	
	public Heal() {
		System.out.println("인자에 회복량을 입력하세요");
	}
	public Heal(int healAmount, double healPercent) {
		this.healAmount = healAmount;
		this.healPercent = healPercent;
	}
	public void use(Mob attacker, Steve player) {
		if (Math.random() < healPercent ) { // 일정 확률로 회복 
		    
		    //Steve player 사용 안함
		    // 현재 체력 + 회복량만큼 회복
		    if (attacker instanceof Witch w) {
		    		System.out.println(attacker.getName() + "이 스스로 자신을 치료합니다");
		    		attacker.heal(healAmount);
		    		}
		}
		else attacker.attack(player); // 회복 아니면 공격 
		
	}
	
}
