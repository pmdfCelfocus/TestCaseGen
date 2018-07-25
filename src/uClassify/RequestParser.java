package uClassify;

import uClassify.Objects.Classification;
import uClassify.Objects.ClassifyObject;
import uClassify.Objects.ResultObject;
import utils.ObjectParse.requirement.DESC;
import utils.ObjectParse.requirement.Requirement;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pedro Feiteira, n48119
 * This class receives the requirements and send it to the uClassify server to be classified.
 * The uClassify response is transformed into a well understand json. This json is sent it as a response
 */
public class RequestParser {

    private static final String SCENARIO = "scenario";

    /**
     * Sends the text to be classified to the monkey learn server, receives the service response and returns
     * this information as a JSON after processed
     * @param textList, text that need to be classified
     * @return a map with the category as key and the list of requirements that belongs to this category as value
     */
    public static Map<String, List<Requirement>> response(String[] textList) {
        List<ResultObject> toParse = APIRequest.createRequest(textList);
        //JSON to object GSON transformation
        Map<String, List<Requirement>> result = process(toParse);
        return result;
    }

    /**
     * Process the objects and creates a better understandable structure
     * @param toParse, uClassify data as an object list
     * @return a map with the category as key and the list of requirements that belongs to this category as value
     */
    private static Map<String, List<Requirement>> process(List<ResultObject> toParse) {
        Map<String, List<Requirement>> result = new TreeMap<>();
        for (ResultObject r : toParse) {
            String key = getHigher(r.getResponse());
            if (!result.containsKey(key)) {
                List<Requirement> list = new LinkedList<>();
                list.add(createScenario(r.getText()));
                result.put(key, list);
            } else {
                result.get(key).add(createScenario(r.getText()));
            }
        }
        return result;
    }

    /**
     * Converts the scenario information into a requirement scenario
     * @param text, scenario information
     * @return a requirement object
     */
    private static Requirement createScenario(String text) {
        Map<String, List<String>> scenarios = null;
        String[] split = text.split("  ");
        String id = "";
        String base = "";
        for (int i = 0; i < split.length; i++) {
            //Get the requirement ID
            if (stringContainsID(split[i].toLowerCase())) {
                id = split[i];
                for (int j = i + 1; j < split.length; j++, i++) {
                    if (split[j].toLowerCase().contains(SCENARIO)) {
                        break;
                    } else {
                        //Get requirement base description
                        base += split[j] + "\n";
                    }
                }
            } else if (split[i].toLowerCase().contains(SCENARIO)) {
                //Get scenario title
                String key = split[i].substring(10, split[i].length());
                List<String> steps = new LinkedList<>();
                for (int j = i + 1; j < split.length; j++, i++) {
                    //When another scenario is achieved, we should break the construction of the current scenario
                    if (split[j].toLowerCase().contains(SCENARIO)) {
                        break;
                    } else {
                        steps.add(split[j]);
                    }
                }
                if (scenarios == null)
                    scenarios = new HashMap<>();
                //Add scenario into the scenarios list
                scenarios.put(key, steps);
            } else {
                base += split[i] + "\n";
            }
        }

        DESC desc = new DESC(base, scenarios);
        Requirement req = new Requirement(id, desc);
        return req;
    }

    /**
     * Checks if a string contains the id word
     * @param s, string that is needed to check
     * @return true if s has the id word, otherwise returns false
     */
    private static boolean stringContainsID(String s) {
        Pattern p = Pattern.compile("\\bid\\b");
        Matcher m = p.matcher(s);

        return m.find();
    }

    /**
     * Get the higher response confidence name
     * @param classify, Classification object
     * @return most confident category
     */
    private static String getHigher(ClassifyObject classify){
        Classification result = classify.getClassification()[0];
        for(Classification c: classify.getClassification()){
            if(result.getP() < c.getP()){
                result = c;
            }
        }
        return result.getClassName();
    }

}
