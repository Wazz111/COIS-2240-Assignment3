public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { 
        Available, 
        Held, 
        Rented, 
        UnderMaintenance, 
        OutOfService 
    }

    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }
    
    private String capitalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.substring(0, 1).toUpperCase() + 
               trimmed.substring(1).toLowerCase();
    }

    public void setLicensePlate(String plate) {
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate format. Must be 3 letters followed by 3 numbers.");
        }
        this.licensePlate = plate.toUpperCase();
    }

    private boolean isValidPlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return false;
        }
        // Format: exactly 3 letters followed by exactly 3 numbers
        return plate.toUpperCase().matches("^[A-Z]{3}[0-9]{3}$");
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() { 
        return licensePlate; 
    }

    public String getMake() { 
        return make; 
    }

    public String getModel() { 
        return model;
    }

    public int getYear() { 
        return year; 
    }

    public VehicleStatus getStatus() { 
        return status; 
    }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [Plate: " + licensePlate + 
               ", Make: " + make + ", Model: " + model + 
               ", Year: " + year + ", Status: " + status + "]";
    }
}