package utils;

public class Diagram {

    private String name;
    private String desc;
    private Steps[] steps;

    public Diagram(String name, String desc, Steps[] steps){
        this.name = name;
        this.desc = desc;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Steps[] getNodes() {
        return steps;
    }
}
