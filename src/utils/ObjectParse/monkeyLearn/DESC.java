package utils.ObjectParse.monkeyLearn;

import java.util.*;

public class DESC {
    private String base;
    private Map<String, List<String>> scenarios;
    private boolean hasScenario;

    public DESC(String base, Map<String, List<String>> scenarios){
        this.base = base;
        this.scenarios = scenarios;
        hasScenario = scenarios != null ? true : false;
    }

    public String getBase() {
        return base;
    }

    public Map<String, List<String>> getScenarios() {
        return scenarios;
    }

    public boolean isHasScenario() {
        return hasScenario;
    }
}
