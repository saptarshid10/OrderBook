package com.orderbook.trading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DepthSnapshotManager implements Runnable {
    private static final String API_ENDPOINT = "https://api.binance.com/api/v3/";
    private final String symbol;
    private final Map<String, Double> previousVolume = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    public DepthSnapshotManager(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000); // Fetch snapshot every 10 seconds
                JsonObject snapshot = fetchSnapshot();
                if (snapshot != null) {
                    compareSnapshot(snapshot);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private JsonObject fetchSnapshot() {
        try {
            String snapshotUrl = API_ENDPOINT + "depth?symbol=" + this.symbol.toUpperCase() + "&limit=50";
            String snapshotJson = sendGetRequest(snapshotUrl);
            return JsonParser.parseString(snapshotJson).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void compareSnapshot(JsonObject snapshot) throws InterruptedException {
        long lastUpdateId = snapshot.get("lastUpdateId").getAsLong();
        Thread.sleep(1000);

        if (!LocalOrderBookStream.streamEventMap.keySet().contains(this.symbol)) {
            return;
        }
        // Iterate over the streamEventMap
        Iterator<Map.Entry<String, JsonObject>> iterator = LocalOrderBookStream.streamEventMap.get(this.symbol).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonObject> entry = iterator.next();
            String key = String.valueOf(entry.getKey());
            String[] parts = key.split("-");
            long eventFirstUpdateId = Long.parseLong(parts[0]);
            long eventFinalUpdateId = Long.parseLong(parts[1]);
            if (eventFinalUpdateId <= lastUpdateId) {
                LocalOrderBookStream.streamEventMap.get(this.symbol).remove(key);
            }
            if (eventFirstUpdateId <= lastUpdateId + 1 && eventFinalUpdateId >= lastUpdateId + 1) {
                // Print the first processed event that meets the conditions
                JsonObject orderBook = entry.getValue();
                if (!previousVolume.containsKey(this.symbol)) {
                    previousVolume.put(this.symbol, 0.0);
                }

                if (previousVolume.get(this.symbol) == 0.0) {
                    previousVolume.put(this.symbol, calculateTotalVolume(orderBook));
                    return; // Skip calculation for the first snapshot
                }
                double currentVolume = calculateTotalVolume(orderBook);
                double volumeChange = currentVolume - previousVolume.get(this.symbol);
                synchronized (lock) {
                    System.out.println("Order Book: " + this.symbol.toUpperCase());

                    System.out.println("Previous volume: " + previousVolume.get(this.symbol) + " USDT");
                    System.out.println("Current Change: " + currentVolume + " USDT");
                    System.out.println("Volume Change: " + volumeChange + " USDT");
                    previousVolume.put(this.symbol, currentVolume);

                    System.out.println("Price\t\t\t| Quantity");
                    System.out.println("------------------------------");
                    JsonArray bids = orderBook.getAsJsonArray("b");
                    JsonArray asks = orderBook.getAsJsonArray("a");

                    // Print bids
                    System.out.println("Bids:");
                    printOrder(bids);
                    System.out.println("------------------------------");

                    // Print asks
                    System.out.println("Asks:");
                    printOrder(asks);
                    System.out.println("============================\n");
                    break;
                }
            }
        }
    }

    private void printOrder(JsonArray events) {
        int eventsCount = 0;
        for (JsonElement bid : events) {
            if (eventsCount >= 50) {
                break;
            }
            JsonArray eventArray = bid.getAsJsonArray();
            double price = eventArray.get(0).getAsDouble();
            double quantity = eventArray.get(1).getAsDouble();
            System.out.printf("%.8f\t| %.8f\n", price, quantity);
            eventsCount++;
        }
    }

    private double calculateTotalVolume(JsonObject snapshot) {
        JsonArray bids = snapshot.getAsJsonArray("b");
        JsonArray asks = snapshot.getAsJsonArray("a");

        double totalVolume = 0.0;

        // Calculate volume for bids
        totalVolume = getVolume(bids, totalVolume);

        // Calculate volume for asks
        totalVolume = getVolume(asks, totalVolume);

        return totalVolume;
    }

    private double getVolume(JsonArray priceEvents, double totalVolume) {
        for (JsonElement event : priceEvents) {
            JsonArray eventArray = event.getAsJsonArray();
            double price = eventArray.get(0).getAsDouble();
            double quantity = eventArray.get(1).getAsDouble();
            totalVolume += price * quantity;
        }
        return totalVolume;
    }

    private String sendGetRequest(String url) throws IOException {
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");
        int responseCode = httpClient.getResponseCode();
        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
        }
        return response.toString();
    }
}
