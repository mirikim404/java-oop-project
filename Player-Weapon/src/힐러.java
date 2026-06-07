
public class 힐러 extends Player{

    public 힐러(String name, int hp, int power, 물약 weapon) {
		super(name, power, hp);
		this.setWeapon(weapon);
	}
    
    public 힐러(String name, int hp, int power, String file) {
		super(name, power, hp, file);
		
	}

	public void 치유하기(){
    }
    
    public void attack(Player target) {
    	System.out.printf("%s가 %s를 치유합니다.", this.getName(), target.getName());
    	System.out.println();
    	target.setHp(target.getHp()+this.getPower());
    }
    
    public void attack(Player target, Weapon weapon) {
        System.out.println(this.getName() + "이/가 " + target.getName() + "을 물약으로 치유합니다.");
        target.setHp(target.getHp() + (this.getPower() + weapon.getPower()));
    }
    
    public void heal(Player [] team, int count) {
    	Player weakest = team[0];
    	for (int i=1; i<count; i++) {
    		if (team[i].getHp() < weakest.getHp()) {
    			weakest = team[i];
    		}
    	}
    	System.out.printf("%s가 %s를 치유합니다. (+%d)%n",
    	        this.getName(), weakest.getName(), this.getPower());
    	    weakest.setHp(weakest.getHp() + this.getPower());
    }

}
