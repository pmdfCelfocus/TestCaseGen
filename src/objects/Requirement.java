package objects;

import java.util.List;

public class Requirement {

    private String description;
    private List<String> dependency;
    private String rationals;
    private List<Scenario> scenarios;


    public Requirement(String description, List<String> dependency, String rationals, List<Scenario> scenarios){
        this.description = description;
        this.dependency = dependency;
        this.rationals = rationals;
        this.scenarios = scenarios;
    }

    public List<String> getDependency() {
        return dependency;
    }

    public String getRationals() {
        return rationals;
    }

    public String getDescription() {
        return description;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }
}
