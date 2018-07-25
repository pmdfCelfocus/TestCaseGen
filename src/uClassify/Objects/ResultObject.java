package uClassify.Objects;

/**
 * @author Pedro Feiteira, n48119
 * This class is used as GSON conversion
 * watch -> https://uclassify.com/docs/restapi
 */
public class ResultObject {

    private String text;
    private ClassifyObject response;

    public ResultObject(String text, ClassifyObject response){
        this.text = text;
        this.response = response;
    }

    public String getText() {
        return text;
    }

    public ClassifyObject getResponse() {
        return response;
    }
}
