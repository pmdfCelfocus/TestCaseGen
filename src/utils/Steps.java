package utils;

public class Steps {

        private String key;
        private String name;
        private String parent;
        private String __gohashid;

        public Steps(String key, String name, String parent, String __gohashid){
            this.key = key;
            this.name = name;
            this.parent = parent;
            this.__gohashid = __gohashid;
        }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }
}
