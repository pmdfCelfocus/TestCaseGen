import java.util.List;

public class Scenario {

    private String title;
    private List<String> steps;

    public Scenario(String title, List<String> steps){
        this.title = title;
        this.steps = steps;
    }

    public List<String> getSteps() {
        return steps;
    }

    public String getTitle() {
        return title;
    }
}
