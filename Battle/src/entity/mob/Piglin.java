package entity.mob;

public class Piglin extends Mob {
	private final int DEFAULT_MAX_HEALTH = 50;
    private final int DEFAULT_ATTACK_POWER = 25;  
    private final int DEFAULT_DEFENCE_POWER = 40;
    private final int DROP_EXP = 50; 
    private final int DROP_COIN = 50;
	public Piglin () {} // 기본 생성자
	
	public Piglin (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
	}
	
	
	@Override
	public void attack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void block() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeDamage() {
		// TODO Auto-generated method stub
		
	}

}
