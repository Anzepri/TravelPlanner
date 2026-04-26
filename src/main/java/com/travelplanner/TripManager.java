package com.travelplanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripManager {

    public static List<Trip> trips = new ArrayList<>();
    private static final String FILE = "trips.txt";

    private static void ensureFile() {
        try {
            File file = new File(FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void loadTrips() {

        trips.clear();
        ensureFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                if (parts.length >= 5) {

                    Trip trip = new Trip(
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4]
                    );

                    trip.setOwnerEmail(parts[0]);

                    
                    if (parts.length == 6) {
                        String[] items = parts[5].split(";");

                        for (String item : items) {

                            String[] i = item.split(",");

                            if (i.length == 4) {
                                trip.getItinerary().add(
                                        new ItineraryItem(
                                                i[2], // title
                                                i[0], // date
                                                i[1], // time
                                                i[3]  // location
                                        )
                                );
                            }
                        }
                    }

                    trips.add(trip);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void saveTrips() {

        try (FileWriter writer = new FileWriter(FILE)) {

            for (Trip t : trips) {

                StringBuilder line = new StringBuilder();

                line.append(t.getOwnerEmail()).append("|")
                    .append(t.getName()).append("|")
                    .append(t.getDestination()).append("|")
                    .append(t.getStartDate()).append("|")
                    .append(t.getEndDate()).append("|");

                
                List<ItineraryItem> items = t.getItinerary();

                for (int i = 0; i < items.size(); i++) {

                    ItineraryItem item = items.get(i);

                    line.append(item.getDate()).append(",")
                        .append(item.getTime()).append(",")
                        .append(item.getTitle()).append(",")
                        .append(item.getLocation());

                    if (i < items.size() - 1) {
                        line.append(";");
                    }
                }

                writer.write(line.toString());
                writer.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
