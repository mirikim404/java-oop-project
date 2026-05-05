public class ZooManager extends Human {
	private String cureArea;

	public ZooManager() {
	}

	public ZooManager(String name, int age, String residence, String occupation, String cureArea) {
		super(name, age, residence, occupation);
		this.cureArea = cureArea;
	}

	public void monitor() {
		System.out.println(" 동물원관리자가 탈출하려는 동물들을 감시합니다.");
	}

	public void care(Animal animal) {
		System.out.println(" 동물원관리자가 " + animal.getName() + "을/를 보살핍니다.");
	}

}
