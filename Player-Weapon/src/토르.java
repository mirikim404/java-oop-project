
public class 토르 extends Player {
	
	public 토르(String name, int hp, int power, 묠니르 weapon) {
		super(name, power, hp);
		this.setWeapon(weapon);
	}
	public 토르(String name, int hp, int power, 묠니르 weapon, String file) {
		super(name, power, hp, file);
		this.setWeapon(weapon);
		
	}

    public void 맥주마시기(){
    }

    public void 날기(){
    }

    public void 던지기(){
    }
   
    // 오버라이딩
    // 토르의 무기가 없는 경우
    public void attack(Player target) {
    	target.setHp(target.getHp()-this.getPower()*3);
    }
    // 토르의 무기가 있는 경우
    public void attack(Player target, Weapon weapon) {
    	System.out.println("묠니르로 공격하기");
    	target.setHp(target.getHp() - (this.getPower() + weapon.getPower()));
    	
    }

}
