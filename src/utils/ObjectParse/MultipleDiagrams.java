package utils.ObjectParse;

/**
 * @author Pedro Feiteira, n48119
 * This class is used as GSON conversion
 * watch -> https://gojs.net/latest/samples/flowchart.html (json format)
 */
public class MultipleDiagrams {

    private Diagram[] diagram;

    public MultipleDiagrams(Diagram[] diagram){
        this.diagram = diagram;
    }

    public Diagram[] getDiagrams() {
        return diagram;
    }
}
