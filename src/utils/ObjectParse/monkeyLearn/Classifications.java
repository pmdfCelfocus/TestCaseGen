package utils.ObjectParse.monkeyLearn;
/**
 * @author Pedro Feiteira, n48119
 * This class is used as GSON conversion
 * watch -> https://app.monkeylearn.com/main/classifiers/cl_4yVFrrYo/tab/run/
 */
public class Classifications {

    private String tag_name;
    private int tag_id;
    private double confidence;

    public Classifications(String tag_name, int tag_id, double confidence) {
        this.tag_name = tag_name;
        this.tag_id = tag_id;
        this.confidence = confidence;
    }

    public String getTag_name() {
        return tag_name;
    }

    public int getTag_id() {
        return tag_id;
    }

    public double getConfidence() {
        return confidence;
    }
}
