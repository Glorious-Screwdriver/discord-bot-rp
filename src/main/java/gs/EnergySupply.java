package gs;

public class EnergySupply extends Item {
    private int providedEnergy;

    public EnergySupply(String name, int price, String description, int requiredLevel, int providedEnergy) {
        this.type = 0;
        this.name = name;
        this.price = price;
        this.description = description;
        this.requiredLevel = requiredLevel;
        this.providedEnergy = providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }
}
