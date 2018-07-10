package utils;

public class Diagram {

    private String name;
    private Steps[] steps;

    public Diagram(String name, Steps[] steps){
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public Steps[] getNodes() {
        return steps;
    }
}
