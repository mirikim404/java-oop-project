package effect;

import entity.Entity;

public class Stun implements StatusEffect {
	private int remainingTurns;
	
    public Stun() {
        this(1);
    }

    public Stun(int turns) {
        this.remainingTurns = turns;
    }

 // Stun.java
    @Override
    public void activate(Entity target) {
        if (remainingTurns <= 0) return;
        target.setStunned(true);
        System.out.println(target.getName() + "이/가 스턴 상태가 되었습니다.");
        remainingTurns--;
    }

	@Override
	public boolean isExpired() {
		// TODO Auto-generated method stub
		return remainingTurns <= 0;
	}
    
    
}