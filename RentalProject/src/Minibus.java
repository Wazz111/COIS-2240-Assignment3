public class Minibus extends Vehicle {
    private boolean isAccessible;

    public Minibus(String make, String model, int year, boolean isAccessible) {
        super(make, model, year);
        this.isAccessible = isAccessible;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + " Accessible: " + (isAccessible ? "Yes" : "No");
    }
}