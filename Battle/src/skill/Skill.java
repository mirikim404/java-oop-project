package skill;

public abstract class Skill {
    protected String name;
    protected String description;
    
    //기본 생성자
    public Skill() {}
    
    //생성자
    public Skill(String name, String description) {
    	this.name = name;
    	this.description = description;
    }
    
    // getter
    public String getName() {return name;}
    public String getDescription() {return description;}

}
