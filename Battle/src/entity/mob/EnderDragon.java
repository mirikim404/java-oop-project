package entity.mob;

import entity.Entity;
import entity.Steve;

public class EnderDragon extends Mob {

	private int phase;
	private final int DEFAULT_MAX_HEALTH = 300;
	private final int DEFAULT_ATTACK_POWER = 50;
	private final int DEFAULT_DEFENCE_POWER = 50;
	private final int BREATHDAMAGE = 15;
	private final double BLOCK_PERCENT = 0.5;
	
	public EnderDragon() {} // 기본 생성자

	public EnderDragon (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER, 0 , 0);
		setAbility(null); // 능력 없음
		phase = 1;
		showPhase();
	}
	
	public void whatPhase() { 
		double hpRatio = (double)getHealth()/DEFAULT_MAX_HEALTH;
		if (hpRatio > 2/3) phase = 1;
		else if (hpRatio > 1/3) phase = 2;
		else phase = 3;
	}
	
	public void act(Steve player) { 
		
		switch(phase) {
		
		case 1 :
			breathFire(player);
			break;
		case 2 :
			fly();
			break;
		case 3:
			chargeAttack(player);
			break;
			
		default :
			attack();
		}
	}
	public void breathFire(Steve player){
		//범위 화염 — 방어로 일부 경감 가능
		System.out.println(getName() + "이 breath 공격을 합니다!");
		this.attack(player); //방어로 줄일 수 있음 
		player.takeDamage(BREATHDAMAGE);
		
	}

	public void fly(){
		System.out.println(getName() + "이(가) 날아오릅니다");
		if (Math.random() < BLOCK_PERCENT ) { // 일정 확률로 block()실행 
		    block();
		    
		}
		
		
	}

	public void chargeAttack(Steve player){
	    System.out.println(getName() + "이(가) 돌진 공격을 사용합니다!//방어력 무시//");
	    player.takeDamage(DEFAULT_ATTACK_POWER);

	    System.out.println(player.getName() + "이(가) " + DEFAULT_ATTACK_POWER + "의 고정 피해를 입었습니다.");
	}

	@Override
	public void attack(Entity target) {
		super.attack(target);
		// TODO Auto-generated method stub

	}

	@Override
	public void block() {
		super.block();
		

	}
	public void showPhase() {
		System.out.println("========["+phase+"단계]========");
		System.out.println("========[체력:"+getHealth() +"]========");
		
	}

}
