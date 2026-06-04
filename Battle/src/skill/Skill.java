package skill;

public abstract class Skill {
    protected String name;
    protected String description;

    public Skill() {}

    public Skill(String name, String description) {
    	this.name = name;
    	this.description = description;
    }

    public String getId() {
        return getClass().getSimpleName();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
