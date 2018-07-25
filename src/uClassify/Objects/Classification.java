package uClassify.Objects;

/**
 * @author Pedro Feiteira, n48119
 * This class is used as GSON conversion
 * watch -> https://uclassify.com/docs/restapi
 */
public class Classification {

    private String className;
    private float p;

    public Classification(String className, float p){
        this.className = className;
        this.p = p;
    }

    public String getClassName() {
        return className;
    }

    public float getP() {
        return p;
    }
}
