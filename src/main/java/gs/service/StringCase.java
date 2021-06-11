package gs.service;

public class StringCase extends Case {
    private final String answer;

    public StringCase(String name, String description, int profit, String answer) {

        // those are read from file
        this.name = name;
        this.description = description;
        this.profit = profit;
        this.answer = answer;
    }

    public boolean solve(String answer) {
        if (answer.equals(this.answer)) {
            finish();
            return true;
        } else {
            return false;
        }
    }
}
