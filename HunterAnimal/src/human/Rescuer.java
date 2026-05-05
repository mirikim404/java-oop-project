package human;
import animal.Animal;
import animal.StrayAnimal;
import common.ISendAnimalInfo;

public class Rescuer extends Human implements ISendAnimalInfo {
	private String organization;
	private Animal[] rescuedAnimals;

	public Rescuer() {
	}

	public Rescuer(String name, int age, String residence, String occupation, String organization) {
		super(name, age, residence, occupation);
		this.organization = organization;
		this.rescuedAnimals = new Animal[10];
	}
	
	public void sendAnimalInfo(Animal animal) {
        System.out.println(getName() + " 구조대원이 보호소관리자에게 동물 정보를 전달합니다.");
        System.out.println("전달 정보: " + animal);
	}

	public void rescue(StrayAnimal animal) {
	    System.out.println(getName() + " 구조대원이 길거리 동물을 구조합니다: " + animal.getName());
	    System.out.println("발견 장소: " + animal.getFoundLocation() + ", 발견 날짜: " + animal.getFoundDate());
	}

}
