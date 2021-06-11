package gs.service;

public class PlayerStatistics {
    int acquiredMoney;
    int casesDone;
    int coffeeConsumed;
    int energyDrinksConsumed;

    public PlayerStatistics() {
        acquiredMoney = 0;
        casesDone = 0;
        coffeeConsumed = 0;
        energyDrinksConsumed = 0;
    }

    public PlayerStatistics(int acquiredMoney, int casesDone, int coffeeConsumed, int energyDrinksConsumed) {
        this.acquiredMoney = acquiredMoney;
        this.casesDone = casesDone;
        this.coffeeConsumed = coffeeConsumed;
        this.energyDrinksConsumed = energyDrinksConsumed;
    }

    public int getAcquiredMoney() {
        return acquiredMoney;
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