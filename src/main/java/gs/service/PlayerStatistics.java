package gs.service;

public class PlayerStatistics {
    int moneySpent;
    int casesDone;
    int coffeeConsumed;
    int energyDrinksConsumed;

    public PlayerStatistics() {
        moneySpent = 0;
        casesDone = 0;
        coffeeConsumed = 0;
        energyDrinksConsumed = 0;
    }

    public PlayerStatistics(int moneySpent, int casesDone, int coffeeConsumed, int energyDrinksConsumed) {
        this.moneySpent = moneySpent;
        this.casesDone = casesDone;
        this.coffeeConsumed = coffeeConsumed;
        this.energyDrinksConsumed = energyDrinksConsumed;
    }

    public int getMoneySpent() {
        return moneySpent;
    }

    public int getCasesDone() {
        return casesDone;
    }

    public int getCoffeeConsumed() {
        return coffeeConsumed;
    }

    public int getEnergyDrinksConsumed() {
        return energyDrinksConsumed;
    }
}