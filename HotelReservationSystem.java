import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
class Room {
    private int roomNumber;
    private String category;
    private double pricePerNight;
    private boolean isAvailable;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = true;
    }
    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - $" + pricePerNight + "/night";
    }
}
class Reservation {
    private static int nextReservationId = 1;
    private int reservationId;
    private Room room;
    private String guestName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private boolean isPaid;

    public Reservation(Room room, String guestName, LocalDate checkIn, LocalDate checkOut) {
        this.reservationId = nextReservationId++;
        this.room = room;
        this.guestName = guestName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = calculateTotalAmount();
        this.isPaid = false;
        room.setAvailable(false); 
    }

    private double calculateTotalAmount() {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return room.getPricePerNight() * nights;
    }
    public int getReservationId() { return reservationId; }
    public Room getRoom() { return room; }
    public String getGuestName() { return guestName; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    @Override
    public String toString() {
        return "Reservation #" + reservationId + "\n" +
               "Guest: " + guestName + "\n" +
               "Room: " + room + "\n" +
               "Check-in: " + checkIn + "\n" +
               "Check-out: " + checkOut + "\n" +
               "Total Amount: $" + totalAmount + "\n" +
               "Status: " + (isPaid ? "Paid" : "Pending");
    }
}
class Payment {
    public static boolean process(double amount, String paymentMethod, String cardNumber, int expiryMonth, int expiryYear) {
        System.out.println("Processing " + paymentMethod + " payment of $" + amount + "...");
        System.out.println("Using card number: " + cardNumber + ", Expiry Month: " + expiryMonth + ", Expiry Year: " + expiryYear);
        if ((paymentMethod.equalsIgnoreCase("Debit Card") || paymentMethod.equalsIgnoreCase("Credit Card")) && isValidCard(cardNumber)) {
            return true;
        }
        return false; 
    }

    private static boolean isValidCard(String cardNumber) {
        return cardNumber.length() == 16 && cardNumber.matches("\\d+");
    }
}
class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;

    public Hotel() {
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        initializeRooms();
    }

    private void initializeRooms() {
        rooms.add(new Room(101, "Standard", 100.0));
        rooms.add(new Room(102, "Standard", 100.0));
        rooms.add(new Room(201, "Deluxe", 200.0));
        rooms.add(new Room(202, "Deluxe", 200.0));
        rooms.add(new Room(301, "Suite", 300.0));
    }

    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, String category) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (isRoomAvailable(room, checkIn, checkOut) && 
                (category == null || room.getCategory().equalsIgnoreCase(category))) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation reservation : reservations) {
            if (reservation.getRoom().getRoomNumber() == room.getRoomNumber()) {
                if (!(checkOut.isBefore(reservation.getCheckIn()) || 
                      checkIn.isAfter(reservation.getCheckOut()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Reservation makeReservation(Room room, String guestName, LocalDate checkIn, LocalDate checkOut) {
        if (!isRoomAvailable(room, checkIn, checkOut)) {
            throw new IllegalStateException("Room is not available for the selected dates");
        }
        Reservation reservation = new Reservation(room, guestName, checkIn, checkOut);
        reservations.add(reservation);
        return reservation;
    }

    public void processPayment(Reservation reservation, String paymentMethod, String cardNumber, int expiryMonth, int expiryYear) {
        if (reservation.isPaid()) {
            System.out.println("Reservation is already paid.");
        } else {
            boolean paymentSuccess = Payment.process(reservation.getTotalAmount(), paymentMethod, cardNumber, expiryMonth, expiryYear);
            if (paymentSuccess) {
                reservation.setPaid(true);
                System.out.println("Payment processed successfully for reservation #" + 
                                  reservation.getReservationId());
            } else {
                System.out.println("Payment failed for reservation #" + reservation.getReservationId());
            }
        }
    }

    public Reservation getReservation(int reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId() == reservationId) {
                return reservation;
            }
        }
        return null;
    }
}
public class HotelReservationSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Hotel hotel = new Hotel();

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchRooms();
                    break;
                case 2:
                    makeReservation();
                    break;
                case 3:
                    viewReservation();
                    break;
                case 4:
                    processPayment();
                    break;
                case 5:
                    System.out.println("Thank you for using our system!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Hotel Reservation System ===");
        System.out.println("1. Search Available Rooms");
        System.out.println("2. Make Reservation");
        System.out.println("3. View Reservation");
        System.out.println("4. Process Payment");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void searchRooms() {
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter room category (Standard/Deluxe/Suite) or press Enter for all: ");
        String category = scanner.nextLine();
        if (category.isEmpty()) category = null;

        List<Room> availableRooms = hotel.searchAvailableRooms(checkIn, checkOut, category);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected criteria.");
        } else {
            System.out.println("\nAvailable Rooms:");
            for (Room room : availableRooms) {
                System.out.println(room);
            }
        }
    }

    private static void makeReservation() {
        try {
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine());
            
            List<Room> availableRooms = hotel.searchAvailableRooms(checkIn, checkOut, null);
            if (availableRooms.isEmpty()) {
                System.out.println("No rooms available for the selected dates.");
                return;
            }

            System.out.println("\nAvailable Rooms:");
            for (Room room : availableRooms) {
                System.out.println(room);
            }

            System.out.print("Select room number to reserve: ");
            int roomNumber = scanner.nextInt();
            scanner.nextLine();

            Room selectedRoom = null;
            for (Room room : availableRooms) {
                if (room.getRoomNumber() == roomNumber) {
                    selectedRoom = room;
                    break;
                }
            }

            if (selectedRoom == null) {
                System.out.println("Invalid room number.");
                return;
            }

            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();
            Reservation reservation = hotel.makeReservation(selectedRoom, guestName, checkIn, checkOut);
            System.out.println("Reservation made successfully:\n" + reservation);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewReservation() {
        System.out.print("Enter reservation ID to view details: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine();

        Reservation reservation = hotel.getReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
        } else {
            System.out.println("\nReservation Details:\n" + reservation);
        }
    }

    private static void processPayment() {
        System.out.print("Enter reservation ID to process payment: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine();

        Reservation reservation = hotel.getReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
        } else {
            System.out.print("Select payment method (1. Debit Card, 2. Credit Card): ");
            int paymentChoice = scanner.nextInt();
            scanner.nextLine();
            String paymentMethod = (paymentChoice == 1) ? "Debit Card" : "Credit Card";

            System.out.print("Please enter your card number: ");
            String cardNumber = scanner.nextLine();

            System.out.print("Please enter your card's expiry month (MM): ");
            int expiryMonth = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Please enter your card's expiry year (YYYY): ");
            int expiryYear = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Confirm payment of $" + reservation.getTotalAmount() + " using " + paymentMethod + " (yes/no): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("yes")) {
                hotel.processPayment(reservation, paymentMethod, cardNumber, expiryMonth, expiryYear);
            } else {
                System.out.println("Payment cancelled.");
            }
        }
    }
}
