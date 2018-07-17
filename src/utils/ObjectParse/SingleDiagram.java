package utils.ObjectParse;

/**
 * @author Pedro Feiteira, n48119, NB24217
 * This class is used as GSON conversion
 * watch -> https://gojs.net/latest/samples/flowchart.html (json format)
 */
public class SingleDiagram {

    private Diagram diagram;

    public SingleDiagram(Diagram diagram){
        this.diagram = diagram;
    }

    public Diagram getDiagram() {
        return diagram;
    }
}
