public class ShelterAnimal extends Animal {
    private String admissionDate;
    private boolean isTreated;
    
    public ShelterAnimal() {
    	
    }
    
    public ShelterAnimal(String name, int age, String residence, String diet, String admissionDate, boolean isTreated) {
        super(name, age, residence, diet);
        this.admissionDate = admissionDate;
        this.isTreated = isTreated;
    }

}
