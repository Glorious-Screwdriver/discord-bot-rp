package gs.service.items;

public class EnergySupply extends Item {
    private final int providedEnergy;

    public EnergySupply(String type, String name, int price, int requiredLevel, int providedEnergy) {
        this.type = type;
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.providedEnergy = providedEnergy;
        this.description = "Restores your energy by " + providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }
}
