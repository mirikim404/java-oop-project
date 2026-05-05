package human;
import animal.AdoptedAnimal;
import animal.Animal;

public class Owner extends Human {
	private String housingType;
	private Animal[] pets;

	public Owner() {
	}

	public Owner(String name, int age, String residence, String occupation, String housingType) {
		super(name, age, residence, occupation);
		this.housingType = housingType;
		this.pets = new Animal[10];
	}

	public void adopt(Animal animal) {
	    System.out.println(getName() + " 보호자가 " + animal.getName() + "을/를 입양합니다.");
	    if (animal instanceof AdoptedAnimal) {
	        System.out.println("입양 날짜: " + ((AdoptedAnimal) animal).getAdoptionDate());
	    }
	}

}
