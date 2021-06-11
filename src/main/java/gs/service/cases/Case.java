package gs.service.cases;

import gs.service.Player;

public abstract class Case {
    protected String name;
    protected String description;
    protected int profit;
//    protected int requiredLevel;

    protected Player player;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getProfit() {
        return profit;
    }

    public void assignToPlayer(Player player) {
        this.player = player;
        boolean assigned = player.setActiveCase(this);
        if (!assigned) {
            throw new IllegalStateException("Player already has active case");
        }
    }

    public void finish() {
        player.updateMoney(profit);
        player.updateLevel(1);
        player.clearCase();
    }
}
