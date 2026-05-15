package effect;

import entity.mob.Mob;

public class Stun implements StatusEffect {
	private int remainingTurns;
	
    public Stun() {
        this(1);
    }

    public Stun(int turns) {
        this.remainingTurns = turns;
    }

    @Override
    public void activate(Mob mob) {
        if (remainingTurns <= 0) {
            return;
        }

        mob.setStunned(true);
        System.out.println(mob.getName() + "이/가 스턴 상태가 되었습니다.");
        remainingTurns--;

    }

	@Override
	public boolean isExpired() {
		// TODO Auto-generated method stub
		return remainingTurns <= 0;
	}
    
    
}