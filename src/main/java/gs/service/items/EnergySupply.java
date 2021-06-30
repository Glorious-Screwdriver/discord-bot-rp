package gs.service.items;

import gs.service.Player;

public class EnergySupply extends Item {
    private final int providedEnergy;

    public EnergySupply(String type, String name, int price, int requiredLevel, int providedEnergy) {
        super(type, name, "Restores your energy by " + providedEnergy, price, requiredLevel);
        this.providedEnergy = providedEnergy;
    }

    public int getProvidedEnergy() {
        return providedEnergy;
    }

    @Override
    public String use(Player player) {
        switch (type) {
            case "coffee":
                player.updateEnergy(1);
                player.statistics.updateCoffeeConsumed(1);

                return String.format(
                        "You have drank a cup of coffee! Energy: %d/%d",
                        player.getEnergy(),
                        player.getMaxEnergy()
                );
            case "energy_drink":
                player.updateEnergy(2);
                player.statistics.updateEnergyDrinksConsumed(1);

                return String.format(
                        "You have consumed a can of energy drink! Energy: %d/%d",
                        player.getEnergy(),
                        player.getMaxEnergy()
                );
        }

        throw new IllegalStateException("Item use has failed");
    }
}
