package utils.ObjectParse.monkeyLearn;

import utils.ObjectParse.monkeyLearn.Classifications;

/**
 * @author Pedro Feiteira, n48119, NB24217
 * This class is used as GSON conversion
 * watch -> https://app.monkeylearn.com/main/classifiers/cl_4yVFrrYo/tab/run/
 */
public class Details {

    private String text;
    private String external_id;
    private boolean error;
    private Classifications[] classifications;
    private String error_detail;

    public Details(String text, String external_id, boolean error, String error_detail, Classifications[] classifications){
        this.text = text;
        this.external_id = external_id;
        this.error = error;
        this.error_detail = error_detail;
        this.classifications = classifications;
    }

    public String getText() {
        return text;
    }

    public String getExternal_id() {
        return external_id;
    }

    public boolean isError() {
        return error;
    }

    public Classifications[] getClassifications() {
        return classifications;
    }

    public String getError_detail() {
        return error_detail;
    }
}
