import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;
import java.time.format.DateTimeParseException;

public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private static RentalSystem instance;
    
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }
    
    private RentalSystem() {
        loadData();
    }

    public void addVehicle(Vehicle vehicle) {
        // Check for duplicate license plate
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Error: Vehicle with license plate " + 
                              vehicle.getLicensePlate() + " already exists.");
            return;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle added successfully!");
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer added successfully.");
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
        }
    }

    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            "Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
        
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) vehicleType = "Car";
                else if (vehicle instanceof Minibus) vehicleType = "Minibus";
                else if (vehicle instanceof PickupTruck) vehicleType = "Pickup Truck";
                else vehicleType = "Unknown";

                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                    vehicleType,
                    vehicle.getLicensePlate(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getStatus().toString()
                );
            }
        }
        if (!found) {
            if (status == null) System.out.println("  No Vehicles found.");
            else System.out.println("  No vehicles with Status: " + status);
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        if (customers.isEmpty()) {
            System.out.println("  No customers found.");
        } else {
            for (Customer c : customers) {
                System.out.println("  " + c.toString());
            }
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                "Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");

            for (RentalRecord record : rentalHistory.getRentalHistory()) {
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n",
                    record.getRecordType(),
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) return v;
        }
        return null;
    }

    public Customer findCustomerById(String id) {  // Changed to String
        for (Customer c : customers) {
            if (c.getCustomerId().equals(id)) return c;  // Changed to equals for String
        }
        return null;
    }

    // File Storage Methods
    private void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out = new PrintWriter(new FileWriter("vehicles.txt", true))) {
            String type;
            String extraInfo = "";
            
            if (vehicle instanceof Car) {
                type = "Car";
                Car car = (Car) vehicle;
                extraInfo = "," + car.getNumberOfSeats();
            } else if (vehicle instanceof Minibus) {
                type = "Minibus";
                Minibus bus = (Minibus) vehicle;
                extraInfo = "," + bus.isAccessible();
            } else if (vehicle instanceof PickupTruck) {
                type = "PickupTruck";
                PickupTruck truck = (PickupTruck) vehicle;
                extraInfo = "," + truck.getCargoSize() + "," + truck.hasTrailer();
            } else {
                type = "Unknown";
            }
            
            out.println(vehicle.getLicensePlate() + "," + 
                       vehicle.getMake() + "," +
                       vehicle.getModel() + "," + 
                       vehicle.getYear() + "," +
                       type + extraInfo);
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new FileWriter("customers.txt", true))) {
            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new FileWriter("rental_records.txt", true))) {
            out.println(record.getRecordType() + "," +
                       record.getVehicle().getLicensePlate() + "," +
                       record.getCustomer().getCustomerId() + "," +
                       record.getRecordDate() + "," +
                       record.getTotalAmount());
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    // Data Loading Methods
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
        System.out.println("Data loaded successfully.");
    }

    private void loadVehicles() {
        File file = new File("vehicles.txt");
        if (!file.exists()) {
            System.out.println("No vehicle data found. Starting with empty vehicle list.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String plate = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    String type = parts[4];
                    
                    Vehicle vehicle = null;
                    
                    switch (type) {
                        case "Car":
                            if (parts.length >= 6) {
                                int seats = Integer.parseInt(parts[5]);
                                vehicle = new Car(make, model, year, seats);
                            }
                            break;
                        case "Minibus":
                            if (parts.length >= 6) {
                                boolean accessible = Boolean.parseBoolean(parts[5]);
                                vehicle = new Minibus(make, model, year, accessible);
                            }
                            break;
                        case "PickupTruck":
                            if (parts.length >= 7) {
                                double cargoSize = Double.parseDouble(parts[5]);
                                boolean hasTrailer = Boolean.parseBoolean(parts[6]);
                                vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                            }
                            break;
                    }
                    
                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        // Check for duplicates before adding
                        if (findVehicleByPlate(plate) == null) {
                            vehicles.add(vehicle);
                            count++;
                        }
                    }
                }
            }
            System.out.println("Loaded " + count + " vehicles from file.");
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing vehicle data: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        File file = new File("customers.txt");
        if (!file.exists()) {
            System.out.println("No customer data found. Starting with empty customer list.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String customerId = parts[0];
                    String customerName = parts[1];
                    
                    // Check for duplicates before adding
                    if (findCustomerById(customerId) == null) {
                        customers.add(new Customer(customerId, customerName));
                        count++;
                    }
                }
            }
            System.out.println("Loaded " + count + " customers from file.");
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRentalRecords() {
        File file = new File("rental_records.txt");
        if (!file.exists()) {
            System.out.println("No rental record data found. Starting with empty rental history.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String recordType = parts[0];
                    String licensePlate = parts[1];
                    String customerId = parts[2];
                    LocalDate recordDate = LocalDate.parse(parts[3]);
                    double totalAmount = Double.parseDouble(parts[4]);
                    
                    Vehicle vehicle = findVehicleByPlate(licensePlate);
                    Customer customer = findCustomerById(customerId);
                    
                    if (vehicle != null && customer != null) {
                        rentalHistory.addRecord(new RentalRecord(vehicle, customer, recordDate, totalAmount, recordType));
                        count++;
                    }
                }
            }
            System.out.println("Loaded " + count + " rental records from file.");
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Error parsing rental record data: " + e.getMessage());
        }
    }
}