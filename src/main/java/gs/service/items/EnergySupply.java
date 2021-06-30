package gs.service.items;

public class EnergySupply extends Item {
    private final int providedEnergy;

    public EnergySupply(String type, String name, int price, int requiredLevel, int providedEnergy) {
        super(type, name, "Restores your energy by " + providedEnergy, price, requiredLevel);
        this.providedEnergy = providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }
}
