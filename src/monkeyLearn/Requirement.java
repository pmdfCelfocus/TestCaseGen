package monkeyLearn;

public class Requirement {
    private String id;
    private DESC desc;

    public Requirement(String id, DESC desc){
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public DESC getDesc() {
        return desc;
    }
}
