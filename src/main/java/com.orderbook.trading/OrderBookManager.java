package com.orderbook.trading;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderBookManager {
    public static void main(String[] args) {
        // Define symbols to monitor
        List<String> symbols = Arrays.asList("btcusdt", "ethusdt");

        // Create a fixed thread pool with multiple processors
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessors);


        // Instantiate and start threads for each symbol
        for (String symbol : symbols) {
            executorService.submit(new LocalOrderBookStream(symbol.toLowerCase()));
            executorService.submit(new DepthSnapshotManager(symbol.toLowerCase()));
        }
    }
}