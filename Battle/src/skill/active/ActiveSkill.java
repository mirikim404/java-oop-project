package skill.active;

import entity.Steve;
import entity.mob.Mob;
import skill.Skill;

public abstract class ActiveSkill extends Skill {
    protected int cooldown;
    protected int currentCooldown = 0;
    
    // 기본 생성자
    public ActiveSkill() {}
    
    // 생성자
    public ActiveSkill(String name, String description, int cooldown) {
    	super(name, description);
    	this.cooldown = cooldown;
    }

    // 메소드
    public boolean isReady() {  // 스킬 사용 가능한지 체크하기
    	return currentCooldown == 0;
    }

    public void triggerCooldown(){  // 스킬 쓰면 쿨타임을 default로 세팅
    	currentCooldown = cooldown;
    }
    
    public void decrementCooldown(){  // 매 턴 끝날 때 쿨타임 1 감소
    	if (currentCooldown > 0) currentCooldown--;
    }
    
    public abstract void use(Steve steve, Mob target);

}
