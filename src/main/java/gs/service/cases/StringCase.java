package gs.service.cases;

public class StringCase extends Case {
    private final String answer;

    public StringCase(String name, String description, int profit, String answer) {
        super(name, description, profit);
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
