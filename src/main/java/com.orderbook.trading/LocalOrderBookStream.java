package com.orderbook.trading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class LocalOrderBookStream implements Runnable {
    private static final String WS_ENDPOINT = "wss://stream.binance.com:9443/ws/";
    static final Map<String, Map<String, JsonObject>> streamEventMap = new ConcurrentHashMap<>();
    private final String symbol;

    public LocalOrderBookStream(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void run() {
        startWebSocket();
    }

    private void startWebSocket() {
        try {
            String wsUrl = WS_ENDPOINT + this.symbol + "@depth";
            WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("Connected to WebSocket");
                }

                @Override
                public void onMessage(String message) {
                    processMessage(message);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("Closed WebSocket connection");
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private synchronized void processMessage(String message) {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        if (!json.get("s").getAsString().equalsIgnoreCase(this.symbol)) {
            // Ignore messages for other symbols
            return;
        }
        String key = json.get("U").getAsString() + "-" + json.get("u").getAsString();

        // Process bid list
        JsonArray bidArray = json.getAsJsonArray("b");
        JsonArray processedBidArray = processPriceEventArray(bidArray);

        // Process ask list
        JsonArray askArray = json.getAsJsonArray("a");
        JsonArray processedAskArray = processPriceEventArray(askArray);

        // Update json object with processed bid and ask lists
        json.add("b", processedBidArray);
        json.add("a", processedAskArray);

        // Put the updated json into streamEventMap
        if (!streamEventMap.keySet().contains(this.symbol)) {
            streamEventMap.put(this.symbol, new ConcurrentHashMap<>());
        }
        streamEventMap.get(this.symbol).put(key, json);
    }

    private JsonArray processPriceEventArray(JsonArray priceArray) {
        JsonArray processedPriceArray = new JsonArray();
        for (JsonElement price : priceArray) {
            JsonArray priceEntry = price.getAsJsonArray();
            if (!(priceEntry.get(1).getAsDouble() == 0)) {
                processedPriceArray.add(price);
            }
        }
        return processedPriceArray;
    }
}
