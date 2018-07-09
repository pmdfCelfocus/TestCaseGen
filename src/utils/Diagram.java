package utils;

public class Diagram {

    private String name;
    private Node[] nodes;

    public Diagram(String name, Node[] nodes){
        this.name = name;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public Node[] getNodes() {
        return nodes;
    }
}
