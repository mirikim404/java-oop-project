public class Animal {
	private String name;
	private int age;
	private String residence;
	private String diet;

	public Animal() {

	}

	public Animal(String name, int age, String residence, String diet) {
		this.name = name;
		this.age = age;
		this.residence = residence;
		this.diet = diet;

		System.out.println("이름: " + name + ", 나이: " + age + ", 거주지: " + residence + ", 식성: " + diet + "인 동물이 생성되었습니다.");
	}

	public String getName() {
		return name;
	}

	public String getDiet() {
		return diet;
	}

	public void eat() {
		System.out.println(name + "이/가 먹이를 먹습니다.");
	}

	public void sleep() {
		System.out.println(name + "이/가 잠을 잡니다.");
	}

	public void play() {
		System.out.println(name + "이/가 놉니다.");
	}

	@Override
	public String toString() {
		return "이름: " + name + ", 나이: " + age + ", 거주지: " + residence + ", 식성: " + diet;
	}

}
