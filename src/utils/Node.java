package utils;

public class Node {

        private int key;
        private String name;
        private int parent;
        private int __gohashid;

        public Node(int key, String name, int parent, int __gohashid){
            this.key = key;
            this.name = name;
            this.parent = parent;
            this.__gohashid = __gohashid;
        }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getParent() {
        return parent;
    }
}
