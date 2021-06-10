package gs;

public class EnergySupply extends Item {
    private final int providedEnergy;

    public EnergySupply(String name, int providedEnergy, int price, int requiredLevel) {
        this.name = name;
        this.providedEnergy = providedEnergy;
        this.price = price;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public String getDescription() {
        return "Restores your energy by " + providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }
}
