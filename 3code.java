import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class TravelPlanner {
    private static final String GOOGLE_MAPS_API_KEY = "YOUR_GOOGLE_MAPS_API_KEY";
    private static final String OPENWEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Travel Itinerary Planner!");
        double totalBudget = getBudget(scanner);
        int numDestinations = getNumDestinations(scanner);
        String[] destinations = getDestinations(scanner, numDestinations);
        String[] dates = getDates(scanner, numDestinations);

        // Print itinerary
        System.out.println("\nYour Travel Itinerary:");
        double totalExpense = 0.0;
        for (int i = 0; i < numDestinations; i++) {
            System.out.println("Destination " + (i + 1) + ": " + destinations[i]);
            System.out.println("Date: " + dates[i]);
            displayLocation(destinations[i]);
            double weather = displayWeather(destinations[i]);
            totalExpense += estimateExpense(weather);
            System.out.println("--------------------------------------");
        }
        
        System.out.println("\nTotal estimated expenses: $" + totalExpense);
        if(totalExpense > totalBudget) {
            System.out.println("Warning: Your expenses exceed your budget!");
        }

        scanner.close();
    }

    private static double getBudget(Scanner scanner) {
        System.out.print("Enter your total budget: ");
        return scanner.nextDouble();
    }

    private static int getNumDestinations(Scanner scanner) {
        System.out.print("Enter the number of destinations: ");
        return scanner.nextInt();
    }

    private static String[] getDestinations(Scanner scanner, int numDestinations) {
        String[] destinations = new String[numDestinations];
        for (int i = 0; i < numDestinations; i++) {
            System.out.print("Enter destination " + (i + 1) + ": ");
            destinations[i] = scanner.next();
        }
        return destinations;
    }

    private static String[] getDates(Scanner scanner, int numDestinations) {
        String[] dates = new String[numDestinations];
        for (int i = 0; i < numDestinations; i++) {
            System.out.print("Enter date for destination " + (i + 1) + " (MM/DD/YYYY): ");
            dates[i] = scanner.next();
        }
        return dates;
    }

    private static void displayLocation(String destination) {
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + destination + "&key=" + GOOGLE_MAPS_API_KEY);
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject location = jsonObject.getJSONArray("results")
                                              .getJSONObject(0)
                                              .getJSONObject("geometry")
                                              .getJSONObject("location");
            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");
            System.out.println("Location: Latitude - " + latitude + ", Longitude - " + longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double displayWeather(String destination) {
        double temperature = 0.0;
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + destination + "&appid=" + OPENWEATHER_API_KEY);
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject main = jsonObject.getJSONObject("main");
            temperature = main.getDouble("temp") - 273.15; // Convert from Kelvin to Celsius
            System.out.printf("Weather: %.1f°C\n", temperature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    private static double estimateExpense(double weather) {
        // Simple expense estimation based on weather
        if (weather < 10) {
            return 100.0; // Cold weather, higher expenses
        } else if (weather >= 10 && weather < 25) {
            return 50.0; // Moderate weather, moderate expenses
        } else {
            return 25.0; // Warm weather, lower expenses
        }
    }
}
