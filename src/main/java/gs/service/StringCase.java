package gs.service;

public class StringCase extends Case {
    private final String answer;

    public StringCase(String name, String description, int profit, int exp, String answer) {

        // those are read from file
        this.name = name;
        this.description = description;
        this.profit = profit;
        this.exp = exp;
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
