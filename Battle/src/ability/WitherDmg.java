package ability;

import effect.Wither;
import entity.Steve;
import entity.mob.Mob;

public class WitherDmg implements Mobability {
	//private int witherTurns; // 위더 데미지 초기 턴수
	private int remainTurns; //위더 데미지 잔여 턴수
	private int witherDmg;
	
	public WitherDmg() {}//기본 생성자
	public WitherDmg(int witherTurns, int witherDmg) {
		this.remainTurns = witherTurns;
		this.witherDmg = witherDmg;
		
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
	 	player.getEffects().add(new Wither(remainTurns, witherDmg));
			
	
		//추가 할 것 : 체력 안보이게끔
    }
	
}
