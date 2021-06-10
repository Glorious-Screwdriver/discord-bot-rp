package gs;

public class EnergySupply extends Item {
    private int providedEnergy;

    public EnergySupply(String name, int providedEnergy) {
        this.name = name;
        this.providedEnergy = providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }
}
