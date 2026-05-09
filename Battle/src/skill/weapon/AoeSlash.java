package skill.weapon;

import entity.Steve;
import entity.mob.Mob;
import weapon.Sword;
import java.util.List;

public class AoeSlash extends WeaponSkill {
	private final int cooldown = 3;
	private int currentCooldown = 0; 
	
	// 생성자(기본)
	public AoeSlash() {
		super("광역베기", "살아있는 전체 몹에게 데미지를 입힌다.");
	}
	
	
	// 메소드
	public boolean isReady() {  // 스킬 사용 가능한지 체크하기
		return currentCooldown == 0;
	}
	
	public void triggerCooldown() {  // 스킬 쓰면 쿨타임을 3으로 세팅하기
		currentCooldown = cooldown;
	}
	
	public void decrementCooldown() {  // 매 턴 끝날 때 쿨타임 1 감소
		if (currentCooldown > 0) currentCooldown--;
	}
	
	// TODO: Entity.health를 protected로 변경 필요 (담당 팀원에게 요청)
	// TODO: Mob.isAlive() 메서드 추가 필요
	// TODO: Steve.getAttackPower() 메서드 추가 필요
	@Override
	public void use(Steve steve, List<Mob> mobs, Sword sword) {
		for (Mob mob : mobs) {
			if (mob.isAlive()) {  // 살아있는 몹에게만 데미지 주기 (광역베기이므로 살아있는 모든 몹)
				mob.takeDamage(steve.getAttackPower() + sword.getAoeDamageBonus());
			}
		}
		triggerCooldown();  // 스킬 다 쓰고 나서 쿨타임 3으로 세팅
	}
	
	
	

}
