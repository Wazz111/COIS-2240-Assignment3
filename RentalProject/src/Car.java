public class Car extends Vehicle {
    private int numberOfSeats;

    public Car(String make, String model, int year, int numberOfSeats) {
        super(make, model, year);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + " Seats: " + numberOfSeats;
    }
}