package gs.service.cases;

import java.util.List;

public class OptionCase extends Case {
    private final List<String> options;
    private final int answer;

    public OptionCase(String name, String description, int profit, List<String> options, int answer) {
        super(name, description, profit);
        this.options = options;
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean solve(int answer) {
        if (answer == this.answer) {
            finish();
            return true;
        } else {
            return false;
        }
    }
}
