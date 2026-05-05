public class StrayAnimal extends Animal {
	private String foundLocation;
	private String foundDate;

	public StrayAnimal() {

	}

	public StrayAnimal(String name, int age, String residence, String diet, String foundLocation, String foundDate) {
		super(name, age, residence, diet);
		this.foundLocation = foundLocation;
		this.foundDate = foundDate;
	}

	public void beWary() {
        System.out.println(getName() + "이/가 경계하고 있습니다.");
	}

}
