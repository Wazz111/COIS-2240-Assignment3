import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRentalTest {

    private RentalSystem rentalSystem;
    private Vehicle car;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Get the singleton instance of RentalSystem
        rentalSystem = RentalSystem.getInstance();
        
        // Clear any existing data to ensure clean tests
        // Note: This is a workaround since RentalSystem doesn't have clear methods
        // In a real test, you'd want to reset the singleton or use a fresh instance
        
        // Create sample Vehicle and Customer
        car = new Car("Toyota", "Camry", 2020, 4);
        customer = new Customer("1", "Alice");  // Changed to String ID

        // Ensure vehicle is available
        car.setStatus(Vehicle.VehicleStatus.Available);
        car.setLicensePlate("CAR123");

        // Add vehicle to system if not already added
        if (rentalSystem.findVehicleByPlate("CAR123") == null) {
            rentalSystem.addVehicle(car);
        }
        
        // Add customer to the system if not already added
        if (rentalSystem.findCustomerById("1") == null) {
            rentalSystem.addCustomer(customer);
        }
    }

    @Test
    void testLicensePlate() {
        Vehicle testCar = new Car("Test", "Model", 2020, 4);
        
        // --- Valid Plates ---
        assertDoesNotThrow(() -> testCar.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> testCar.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> testCar.setLicensePlate("ZZZ999"));

        // --- Invalid Plates ---
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("ZZZ99"));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("ABC12D"));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("123ABC"));
    }

    @Test
    void testRentAndReturnVehicle() {
        // Vehicle should initially be available
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        // Rent vehicle
        boolean rentSuccess = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertTrue(rentSuccess, "Rent should succeed");
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());

        // Attempt to rent the same vehicle again
        boolean rentFail = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 50.0);
        assertFalse(rentFail, "Renting same vehicle should fail");

        // Return vehicle
        boolean returnSuccess = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 10.0);
        assertTrue(returnSuccess, "Return should succeed");
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        // Attempt to return the same vehicle again
        boolean returnFail = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
        assertFalse(returnFail, "Returning available vehicle should fail");
    }

    @Test
    void testSingletonRentalSystem() throws Exception {
        // Validate that the constructor is private
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // Validate that getInstance returns a non-null instance
        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance);

        // Validate that multiple calls return the same instance
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance, instance2);
    }
}