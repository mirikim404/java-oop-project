package ability;

import entity.Steve;
import entity.mob.Mob;

public class WitherDmg implements Mobability {
	private int witherTurns; // 위더 데미지 초기 턴수
	private int remainTurns; //위더 데미지 잔여 턴수
	private int witherDmg;
	
	public WitherDmg() {}//기본 생성자
	public WitherDmg(int witherTurns, int witherDmg) {
		this.witherTurns = witherTurns;
		this.witherDmg = witherDmg;
		remainTurns = witherTurns;
	}
	
	public int getRemainTurns() {
		return remainTurns;
	}
	public void setRemainTurns(int remainTurns) {
		this.remainTurns = remainTurns;
	}

	
	@Override
    public void use(Mob attacker, Steve player) {
	 	System.out.println(attacker.getName() + "이"+ remainTurns + "턴간 위더 효과를 겁니다. " );
	 	
		if(remainTurns > 0) {
			player.takeDamage(witherDmg);
			System.out.println("남은 턴 수 : " + remainTurns );
		}
		System.out.println("위더 효과 종료");
		//추가 할 것 : 체력 안보이게끔
    }
	
}
