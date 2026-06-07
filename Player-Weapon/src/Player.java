

public class Player implements Attackable {
    private int hp;
    private int power;
    private String name;
    private Weapon weapon;
    private String filename;

    
    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void attack(){
    	System.out.println("플레이어가 공격합니다.");
    	System.out.println("각 하위 플레이어의 공격동작을 구현합니다.");
    }
    
    // 구현해봤자 하위클래스가 오버라이딩
    //공통동작: 상대방의 hp를 나의 power만큼 깎는다!
    public void attack(Player target) {
    	System.out.println(this.getName() + "이/가" + target.getName() + "을 공격합니다.");
    	// target.hp -= this.power; -- 가능하지만 좋지 않은 코드
    	target.setHp(target.getHp()-this.getPower());
    	
    }

    public void attack(Player target, Weapon weapon) {
    	System.out.println("플레이어가 상대방을 무기로 공격합니다.");
    	
    }
    
    //한명의 플레이어 정보 보기
    public void showstatus() {
    	System.out.printf("%s (power: %2d)", this.getName(), this.getPower());
    	for (int i=0; i<this.getPower()/10; i++) {
    		System.out.print("♡");
    	}
    	System.out.println();
    }
    
    //여러명의 플레이어 정보 보기 
    //공유를 하려면? -> static 사용하기 
    public static void showStatus(Player [] ps, int count) {
    	for (int i=0; i<count; i++) {
    		System.out.print("Player" + (i+1) + " : ");
    		ps[i].showstatus();
    	}
    }
    
    public Player(String name, int power, int hp) {
    	System.out.println("플레이어 초기화시켜 생성!");
    	this.name = name;
    	this.hp = hp;
    	this.power = power;
    }
    
    public Player(String name, int power, int hp, String file) {
    	this(name, power, hp);
    	this.filename= file;
    }

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	

}
