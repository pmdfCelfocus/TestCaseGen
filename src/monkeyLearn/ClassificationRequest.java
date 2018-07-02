package monkeyLearn;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassificationRequest {

    public static Map<String, List<String>> response(String[] textList) {
        //String json = MonkeyLearnRequest.createRequest(textList);
        String json = "{\"text\": \"ID: FR22  Feature: Create an account  In order to create an account  A restaurant owner  Should register on the web-portal  Scenario: Required information for registration  Given the restaurant owner wants to create an account  And the restaurant owner does not have an account  When the restaurant owner registers on the web-portal by providing user-name  And password  And address  And e-mail address  And phone number  Then the restaurant owner should be able to apply for verification     Scenario: Full information for registration   Given the restaurant owner wants to create an account  And the restaurant owner does not have an account  When the restaurant owner registers on the web-portal by providing user name  And password  And address  And e-mail address  And phone number  And mobile number  Then the restaurant owner should be able to apply for verification  Scenario: Confirmed registration  Given the restaurant owner has applied for verification  And has not received a confirmation e-mail after registration  When the restaurant owner receives a confirmation e-mail  Then the restaurant owner should be able to log in  \", " +
        "\"external_id\": null, \"error\": false, \"classifications\": [{\"tag_name\": \"Functional Requirements\",\"tag_id\": 55180139,\"confidence\": 0.227}]}";
        //String json = "{\"text\": \"ID: FR9  TITLE: Mobile application - Navigation to restaurant  DESC: A user should be able to select a pin on a map or an element on a list. When a selection is made,  the location of the restaurant should be sent to the mobile phone\\u2019s GPS-navigation program. The user  should then be navigated to the destination. When the destination is reached, a user should be able to go  back to the search page on the mobile application.  RAT: To navigate a user to a chosen restaurant.  DEP: FR7, FR8  \"," +
          //      "\"external_id\": null, \"error\": false,\"classifications\": [ {\"tag_name\": \"Functional Requirements\",\"tag_id\": 55180139,\"confidence\": 0.217}]}";
        Gson gson = new Gson();
        //Details[] classifications = gson.fromJson(json,Details[].class);
        Details classifications = gson.fromJson(json, Details.class);
        Map<String, List<String>> result = process(classifications);
        return result;
    }

    private static Map<String, List<String>> process(Details details) {
        Map<String, List<String>> result = null;
        String text = details.getText();
        String[] split = text.split("  ");
        String id = "";
        String base = "";
        for (int i = 0; i < split.length; i++) {
            if (split[i].toLowerCase().contains("id")) {
                id = split[i];
                for (int j = i + 1; j < split.length; j++, i++) {
                    if (split[j].toLowerCase().contains("scenario")) {
                        break;
                    } else {
                        base += split[j] + "\n";
                    }
                }
            } else if (split[i].toLowerCase().contains("scenario")) {
                String key = split[i].substring(10, split[i].length());
                List<String> steps = new LinkedList<>();
                for (int j = i + 1; j < split.length; j++, i++) {
                    if (split[j].toLowerCase().contains("scenario")) {
                        break;
                    } else {
                        steps.add(split[j]);
                    }
                }
                if (result == null)
                    result = new HashMap<>();
                result.put(key, steps);
            } else {
                base += split[i] + "\n";
            }
        }

        DESC desc = new DESC(base, result);
        Requirement req = new Requirement(id, desc);
        System.out.println("->" + req.getId() + "\n");
        System.out.println(req.getDesc().getBase());
        System.out.println(req.getDesc().isHasScenario());
        if(req.getDesc().isHasScenario())
        for (String s : req.getDesc().getScenarios().keySet()) {
            System.out.println();
            System.out.println("Scenario -> " + s);
            req.getDesc().getScenarios().get(s).forEach(value -> {
                System.out.println(value);
            });
        }
        return null;
    }

    public static void main(String[] args) {
        response(null);
    }

    /**private static Map<String, List<String>> process(Details[] details){
     Map<String,List<String>> result = new TreeMap<>();
     for(Details d : details){
     for(Classifications c: d.getClassifications()){
     String key = c.getTag_name();
     if(!result.containsKey(key)){
     List<String> list = new LinkedList<>();
     list.add(d.getText());
     result.put(key, list);
     }else{
     result.get(key).add(d.getText());
     }
     }
     }
     return result;
     }
     **/

}
