package monkeyLearn;

import com.google.gson.Gson;
import utils.ObjectParse.monkeyLearn.Classifications;
import utils.ObjectParse.monkeyLearn.DESC;
import utils.ObjectParse.monkeyLearn.Details;
import utils.ObjectParse.monkeyLearn.Requirement;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassificationRequest {
    private static final String SCENARIO = "scenario";
    private static Gson gson = new Gson();

    public static Map<String, List<Requirement>> response(String[] textList) {
        String json = MonkeyLearnRequest.createRequest(textList);
        Details[] classifications = gson.fromJson(json, Details[].class);
        Map<String, List<Requirement>> result = process(classifications);
        return result;
    }

    private static Map<String, List<Requirement>> process(Details[] details) {
        Map<String, List<Requirement>> result = new TreeMap<>();
        for (Details d : details) {
            for (Classifications c : d.getClassifications()) {
                String key = c.getTag_name();
                if (!result.containsKey(key)) {
                    List<Requirement> list = new LinkedList<>();
                    list.add(createScenario(d.getText()));
                    result.put(key, list);
                } else {
                    result.get(key).add(createScenario(d.getText()));
                }
            }
        }
        return result;
    }


    private static Requirement createScenario(String text) {
        Map<String, List<String>> scenarios = null;
        String[] split = text.split("  ");
        String id = "";
        String base = "";
        for (int i = 0; i < split.length; i++) {
            if (stringContainsID(split[i].toLowerCase())) {
                id = split[i];
                for (int j = i + 1; j < split.length; j++, i++) {
                    if (split[j].toLowerCase().contains(SCENARIO)) {
                        break;
                    } else {
                        base += split[j] + "\n";
                    }
                }
            } else if (split[i].toLowerCase().contains(SCENARIO)) {
                String key = split[i].substring(10, split[i].length());
                List<String> steps = new LinkedList<>();
                for (int j = i + 1; j < split.length; j++, i++) {
                    if (split[j].toLowerCase().contains(SCENARIO)) {
                        break;
                    } else {
                        steps.add(split[j]);
                    }
                }
                if (scenarios == null)
                    scenarios = new HashMap<>();
                scenarios.put(key, steps);
            } else {
                base += split[i] + "\n";
            }
        }

        DESC desc = new DESC(base, scenarios);
        Requirement req = new Requirement(id, desc);
        return req;
    }

    private static boolean stringContainsID(String s) {
        Pattern p = Pattern.compile("\\bid\\b");
        Matcher m = p.matcher(s);

        return m.find();
    }
}

