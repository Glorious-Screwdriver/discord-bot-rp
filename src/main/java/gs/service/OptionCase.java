package gs.service;

import java.util.List;

public class OptionCase extends Case {
    private final List<String> options;
    private final int answer;

    public OptionCase(String name, String description, int profit, int exp, List<String> options, int answer) {

        // those are read from file
        this.name = name;
        this.description = description;
        this.profit = profit;
        this.exp = exp;
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
