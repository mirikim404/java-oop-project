package ability;

import entity.Steve;
import entity.mob.Mob;

public class Explode implements Mobability {
	private int beforeExplosionTurns; //폭발 전까지 남은 턴 수
	private int explosionDamage;
	
	public Explode() {} 
	
	public Explode(int beforeExplosionTurns, int explosionDamage) {
		this.beforeExplosionTurns = beforeExplosionTurns;
		this.explosionDamage = explosionDamage;
		
	}
	
	//getter setter 
	public int getBeforeExplosionTurns() {
		return beforeExplosionTurns;
	}

	public void setBeforeExplosionTurns(int beforeExplosionTurns) {
		this.beforeExplosionTurns = beforeExplosionTurns;
	}

	@Override
	public void use(Mob attacker, Steve player) {
		
		if(beforeExplosionTurns == 0) { 
			
			System.out.println("쉬시시시식...펑!!!");
			player.takeDamage(explosionDamage + player.getDefencePower());  // 방어력만큼 더해서 상쇄
			attacker.setHealth(0); // 자폭(사망처리) //플레이어 막기() 무시
			System.out.println(attacker.getName()+"가 폭발하여" + player.getName()+"에게"+ explosionDamage+"만큼의 피해를 입혔습니다.");
		}
			
		
	}



}
