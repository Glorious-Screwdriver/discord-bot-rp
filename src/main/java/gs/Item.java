package gs;

public class Item implements Comparable {
    protected int type;
    protected String name;
    protected int price = 0;
    protected String description = null;
    protected int requiredLevel = 0;
    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    @Override
    public int compareTo(Object o) {
        if (o.getClass() == Item.class) {
            return name.compareTo(((Item) o).getName());
        }

        throw new IllegalArgumentException("Comparing with non-Item object");
    }
}
