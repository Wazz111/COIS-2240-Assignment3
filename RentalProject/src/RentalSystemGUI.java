import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class RentalSystemGUI extends JFrame {
    private RentalSystem rentalSystem;
    private JTextArea outputArea;
    
    public RentalSystemGUI() {
        rentalSystem = RentalSystem.getInstance();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Vehicle Rental System - Java GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Vehicle menu
        JMenu vehicleMenu = new JMenu("Vehicles");
        JMenuItem addVehicleItem = new JMenuItem("Add Vehicle");
        JMenuItem viewVehiclesItem = new JMenuItem("View Available Vehicles");
        
        addVehicleItem.addActionListener(e -> showAddVehicleDialog());
        viewVehiclesItem.addActionListener(e -> viewAvailableVehicles());
        
        vehicleMenu.add(addVehicleItem);
        vehicleMenu.add(viewVehiclesItem);
        
        // Customer menu
        JMenu customerMenu = new JMenu("Customers");
        JMenuItem addCustomerItem = new JMenuItem("Add Customer");
        JMenuItem viewCustomersItem = new JMenuItem("View Customers");
        
        addCustomerItem.addActionListener(e -> showAddCustomerDialog());
        viewCustomersItem.addActionListener(e -> viewCustomers());
        
        customerMenu.add(addCustomerItem);
        customerMenu.add(viewCustomersItem);
        
        // Rental menu
        JMenu rentalMenu = new JMenu("Rentals");
        JMenuItem rentVehicleItem = new JMenuItem("Rent Vehicle");
        JMenuItem returnVehicleItem = new JMenuItem("Return Vehicle");
        JMenuItem viewHistoryItem = new JMenuItem("View Rental History");
        
        rentVehicleItem.addActionListener(e -> showRentDialog());
        returnVehicleItem.addActionListener(e -> showReturnDialog());
        viewHistoryItem.addActionListener(e -> viewRentalHistory());
        
        rentalMenu.add(rentVehicleItem);
        rentalMenu.add(returnVehicleItem);
        rentalMenu.add(viewHistoryItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(vehicleMenu);
        menuBar.add(customerMenu);
        menuBar.add(rentalMenu);
        
        setJMenuBar(menuBar);
        
        // Create main panel with buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] buttonLabels = {
            "Add Vehicle", "Add Customer", "Rent Vehicle",
            "Return Vehicle", "View Available", "View History"
        };
        
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }
        
        // Output area
        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        // Main layout
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton)e.getSource()).getText();
            
            switch (command) {
                case "Add Vehicle":
                    showAddVehicleDialog();
                    break;
                case "Add Customer":
                    showAddCustomerDialog();
                    break;
                case "Rent Vehicle":
                    showRentDialog();
                    break;
                case "Return Vehicle":
                    showReturnDialog();
                    break;
                case "View Available":
                    viewAvailableVehicles();
                    break;
                case "View History":
                    viewRentalHistory();
                    break;
            }
        }
    }
    
    private void showAddVehicleDialog() {
        JDialog dialog = new JDialog(this, "Add Vehicle", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        
        JTextField plateField = new JTextField();
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Minibus", "Pickup Truck"});
        JTextField seatsField = new JTextField("5");
        
        dialog.add(new JLabel("License Plate:"));
        dialog.add(plateField);
        dialog.add(new JLabel("Make:"));
        dialog.add(makeField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Seats (for Car):"));
        dialog.add(seatsField);
        
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                String plate = plateField.getText();
                String make = makeField.getText();
                String model = modelField.getText();
                int year = Integer.parseInt(yearField.getText());
                String type = (String) typeCombo.getSelectedItem();
                
                Vehicle vehicle;
                switch (type) {
                    case "Car":
                        int seats = Integer.parseInt(seatsField.getText());
                        vehicle = new Car(make, model, year, seats);
                        break;
                    case "Minibus":
                        vehicle = new Minibus(make, model, year, true);
                        break;
                    case "Pickup Truck":
                        vehicle = new PickupTruck(make, model, year, 5.0, false);
                        break;
                    default:
                        vehicle = new Car(make, model, year, 5);
                }
                
                vehicle.setLicensePlate(plate);
                rentalSystem.addVehicle(vehicle);
                outputArea.append("Vehicle added: " + plate + "\n");
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(new JLabel()); // empty cell
        dialog.add(buttonPanel);
        
        dialog.setVisible(true);
    }
    
    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add Customer", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(300, 150);
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        
        dialog.add(new JLabel("Customer ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            
            if (!id.isEmpty() && !name.isEmpty()) {
                Customer customer = new Customer(id, name);
                boolean added = rentalSystem.addCustomer(customer);
                
                if (added) {
                    outputArea.append("Customer added: " + name + " (ID: " + id + ")\n");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Duplicate customer ID: " + id);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(new JLabel()); // empty cell
        dialog.add(buttonPanel);
        
        dialog.setVisible(true);
    }
    
    private void showRentDialog() {
        outputArea.append("=== Rent Vehicle ===\n");
        outputArea.append("(For full rental functionality, use the console application)\n");
        outputArea.append("Menu: 3. Rent Vehicle\n\n");
    }
    
    private void showReturnDialog() {
        outputArea.append("=== Return Vehicle ===\n");
        outputArea.append("(For full return functionality, use the console application)\n");
        outputArea.append("Menu: 4. Return Vehicle\n\n");
    }
    
    private void viewAvailableVehicles() {
        outputArea.append("\n=== Available Vehicles ===\n");
        List<Vehicle> available = rentalSystem.getAvailableVehicles();
        
        if (available.isEmpty()) {
            outputArea.append("No vehicles available.\n");
        } else {
            for (Vehicle v : available) {
                outputArea.append(v.toString() + "\n");
            }
        }
        outputArea.append("\n");
    }
    
    private void viewCustomers() {
        outputArea.append("\n=== Customers ===\n");
        rentalSystem.displayAllCustomers();
        outputArea.append("(Customers displayed in console above)\n\n");
    }
    
    private void viewRentalHistory() {
        outputArea.append("\n=== Rental History ===\n");
        rentalSystem.displayRentalHistory();
        outputArea.append("(Rental history displayed in console above)\n\n");
    }
    
    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new RentalSystemGUI());
    }
}