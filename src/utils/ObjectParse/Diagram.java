package utils.ObjectParse;

/**
 * @author Pedro Feiteira, n48119, NB24217
 * This class is used as GSON conversion
 * watch -> https://gojs.net/latest/samples/flowchart.html (json format)
 */
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
