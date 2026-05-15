package effect;

import entity.mob.Mob;

public class Wither implements StatusEffect {
    
    @Override
    public void activate(Mob mob) {
    	// TODO : 위더 효과 구현
    }

	@Override
	public boolean isExpired() {
        // TODO: 위더 효과 구현 시 만료 조건 추가
        return true;
	}
    
    
}