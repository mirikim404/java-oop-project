package entity.mob;
public class EnderDragon extends Mob {
    
    private int phase;
    private final int DEFAULT_MAX_HEALTH = 300;
    private final int DEFAULT_ATTACK_POWER = 50;
    private final int DEFAULT_DEFENCE_POWER = 50;
//    private final int DROP_EXP = 10; 사실상 불필요 
    
    public EnderDragon() {} // 기본 생성자
    
	public EnderDragon (String name, int DEFAULT_MAX_HEALTH, int DEFAULT_ATTACK_POWER, int DEFAULT_DEFENCE_POWER) { //생성자
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
	}
	
    public void breathFire(){
    }

    public void fly(){
    }

    public void chargeAttack(){
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
