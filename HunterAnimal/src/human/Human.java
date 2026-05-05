package human;
import animal.Animal;

public class Human {
	private String name;
	private int age;
	private String residence;
	private String occupation;

	public Human() {
	}

	public Human(String name, int age, String residence, String occupation) {
		this.name = name;
		this.age = age;
		this.residence = residence;
		this.occupation = occupation;
	}

	public String getName() {
		return name;
	}

	public void feed(Animal animal) {
        System.out.println(name + "이/가 " + animal.getName() + "에게 먹이를 줍니다.");
	}

	public void sleep() {
        System.out.println(name + "이/가 잠을 잡니다.");
	}

	public void eat() {
        System.out.println(name + "이/가 식사를 합니다.");
	}

	public void getAnimalInfo(Animal animal) {
        System.out.println(name + "이/가 동물 정보를 확인합니다: " + animal);
	}

}
