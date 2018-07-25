package uClassify.Objects;

/**
 * @author Pedro Feiteira, n48119
 * This class is used as GSON conversion
 * watch -> https://uclassify.com/docs/restapi
 */
public class ClassifyObject {

    private float textCoverage;
    private Classification[] classification;

    public ClassifyObject(float textCoverage, Classification[] classification){
        this.textCoverage = textCoverage;
        this.classification = classification;
    }

    public float getTextCoverage() {
        return textCoverage;
    }

    public Classification[] getClassification() {
        return classification;
    }
}
