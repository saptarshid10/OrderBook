# OrderBook

### Algorithm

The code will be running in 2 different thread.

####Thread 1
1. Here we will manage local order book
2. Open a stream to `wss://stream.binance.com:9443/ws/btcusdt@depth`
3. Buffer each event received from the stream at every time interval.
4. Store each event as a row in a Map. Lets call the Map "**streamEventMap**". The key of the map is in the format "U"-"u".
5. In each event there is a list of bid("b") and ask("a"). If quantity is 0 then remove the price level.

####Thread 2
1. Every 10 second, get a depth snapshot from `https://api.binance.com/api/v3/depth?symbol=BTCUSDT&limit=50`
2. Then compare the snapshot "lastUpdateId" with the keys of "streamEventMap".
3. Remove any event from the Map where "u" is <= "lastUpdateId"
4. The first processed event should have "U" <= lastUpdateId+1 AND "u" >= lastUpdateId+1
5. Calculate the volume in USDT sum(bid quantity * bid price) + sum(ask quantity * ask price).
6. Calculate total volume change which is the difference between the values at T and T + 10s.
7. Print the orderbook in a column format (one pair of price & quantity per line) and the total volume change in USDT for each order book every 10 seconds.

### How to run

1. Checkout `https://github.com/saptarshid10/OrderBook.git`
2. Open the project in an IDE (IntelliJ)
3. Right Click on **pom.xml** Maven>Download Sources
4. Run OrderBookManager


### Sample output
```
Connected to WebSocket
Connected to WebSocket
Order Book: BTCUSDT
Previous volume: 4304673.769615002 USDT
Current Change: 2636454.559086 USDT
Volume Change: -1668219.210529002 USDT
Price			| Quantity
------------------------------
Bids:
64255.95000000	| 2.59221000
64254.35000000	| 0.01529000
64254.34000000	| 0.15544000
64252.22000000	| 0.02830000
64252.21000000	| 0.04707000
64250.98000000	| 0.18658000
64250.97000000	| 0.31089000
64246.01000000	| 0.39692000
64245.88000000	| 0.01746000
64242.75000000	| 0.00155000
64240.86000000	| 0.00009000
64240.82000000	| 0.00155000
64239.18000000	| 0.00009000
64239.01000000	| 0.01556000
64239.00000000	| 0.00778000
64236.36000000	| 0.18656000
64230.72000000	| 0.79425000
64228.56000000	| 0.54325000
64222.43000000	| 0.02000000
64202.58000000	| 0.04283000
64201.73000000	| 0.00009000
64200.90000000	| 0.00009000
64200.07000000	| 0.00009000
64199.24000000	| 0.02973000
64198.41000000	| 0.00009000
64197.58000000	| 0.00009000
64196.75000000	| 0.00009000
64188.06000000	| 0.00388000
64188.05000000	| 0.00100000
64177.57000000	| 0.00370000
64164.69000000	| 0.00011000
64132.25000000	| 0.01090000
64131.52000000	| 0.31147000
64122.21000000	| 0.00155000
64113.22000000	| 0.00149000
64101.02000000	| 0.00062000
64001.72000000	| 0.00100000
60600.00000000	| 3.30432000
59513.35000000	| 0.00104000
58283.83000000	| 0.00073000
57830.00000000	| 0.03716000
42000.00000000	| 27.60986000
32127.97000000	| 0.00037000
------------------------------
Asks:
64255.96000000	| 0.38283000
64256.66000000	| 0.15543000
64259.42000000	| 0.01432000
64259.43000000	| 0.31085000
64262.65000000	| 0.15542000
64266.37000000	| 0.31082000
64266.63000000	| 0.91848000
64267.37000000	| 0.02389000
64269.99000000	| 0.67158000
64270.12000000	| 0.18656000
64270.43000000	| 0.43925000
64271.76000000	| 0.51221000
64273.88000000	| 0.04575000
64277.49000000	| 0.54325000
64278.41000000	| 0.12218000
64280.86000000	| 0.51221000
64283.09000000	| 0.13466000
64293.32000000	| 0.00271000
64295.03000000	| 0.10187000
64295.30000000	| 0.00243000
64301.50000000	| 0.00387000
64304.24000000	| 0.18656000
64305.72000000	| 0.01623000
64305.87000000	| 0.51326000
64305.88000000	| 0.35143000
64305.92000000	| 0.00388000
64307.28000000	| 0.31062000
64310.01000000	| 0.01150000
64313.10000000	| 0.00009000
64313.94000000	| 0.00009000
64314.78000000	| 0.00009000
64315.62000000	| 0.00009000
64343.85000000	| 0.31044000
64365.52000000	| 0.00155000
64371.00000000	| 0.00155000
64389.29000000	| 0.31022000
65000.00000000	| 5.07176000
66200.00000000	| 1.36072000
============================

Order Book: ETHUSDT
Previous volume: 1669814.7553020003 USDT
Current Change: 2296003.6235379986 USDT
Volume Change: 626188.8682359983 USDT
Price			| Quantity
------------------------------
Bids:
3019.74000000	| 23.94800000
3019.73000000	| 0.49880000
3019.72000000	| 3.00080000
3019.71000000	| 4.96120000
3019.66000000	| 5.00000000
3019.60000000	| 1.36700000
3019.59000000	| 5.00000000
3019.57000000	| 1.19950000
3019.54000000	| 1.99900000
3019.52000000	| 1.99820000
3019.49000000	| 4.00700000
3019.46000000	| 0.16300000
3019.45000000	| 0.34320000
3019.41000000	| 5.05240000
3019.40000000	| 1.36700000
3019.39000000	| 0.03300000
3019.37000000	| 6.74040000
3019.33000000	| 0.00300000
3019.25000000	| 0.82020000
3019.21000000	| 0.82170000
3019.20000000	| 1.36700000
3019.19000000	| 3.00000000
3019.15000000	| 1.80010000
3019.14000000	| 3.00000000
3019.09000000	| 3.00000000
3019.00000000	| 2.36410000
3018.97000000	| 3.49900000
3018.96000000	| 3.00000000
3018.95000000	| 0.00180000
3018.92000000	| 5.83170000
3018.90000000	| 1.80200000
3018.86000000	| 0.37840000
3018.85000000	| 0.00180000
3018.80000000	| 1.36880000
3018.75000000	| 0.00180000
3018.70000000	| 0.00180000
3018.65000000	| 0.00180000
3018.61000000	| 0.82140000
3018.58000000	| 6.56380000
3018.48000000	| 1.20060000
3018.44000000	| 2.53760000
3018.21000000	| 8.02400000
3018.05000000	| 16.54830000
3018.00000000	| 2.49040000
3017.61000000	| 3.37640000
3017.58000000	| 8.89840000
3017.57000000	| 0.01000000
3017.55000000	| 0.31920000
3017.51000000	| 2.40000000
3017.50000000	| 0.00500000
------------------------------
Asks:
3019.75000000	| 2.21210000
3019.85000000	| 0.00180000
3020.00000000	| 3.75050000
3020.09000000	| 0.15700000
3020.10000000	| 4.26950000
3020.11000000	| 7.11260000
3020.30000000	| 0.00180000
3020.33000000	| 8.98930000
3020.35000000	| 0.05140000
3020.39000000	| 5.68190000
3020.48000000	| 5.28520000
3020.49000000	| 8.27040000
3020.50000000	| 7.11440000
3020.51000000	| 5.51980000
3020.60000000	| 1.10770000
3020.61000000	| 1.84240000
3020.67000000	| 0.52700000
3020.70000000	| 0.00180000
3020.74000000	| 15.64640000
3020.75000000	| 4.96480000
3020.76000000	| 0.14940000
3020.77000000	| 3.10460000
3020.79000000	| 0.37920000
3020.80000000	| 0.00180000
3020.81000000	| 2.85420000
3020.89000000	| 7.08940000
3020.90000000	| 6.61810000
3020.92000000	| 2.50570000
3021.05000000	| 0.00180000
3021.10000000	| 0.00180000
3021.22000000	| 0.37420000
3021.24000000	| 3.97030000
3021.25000000	| 6.61630000
3021.30000000	| 0.65470000
3021.45000000	| 4.00000000
3021.47000000	| 13.20930000
3021.51000000	| 1.81500000
3021.52000000	| 0.37410000
3021.64000000	| 3.61910000
3021.66000000	| 4.06950000
3021.99000000	| 0.04290000
3022.07000000	| 9.92730000
3022.17000000	| 0.70880000
3022.44000000	| 3.75010000
3022.49000000	| 16.52230000
3023.11000000	| 0.04000000
3023.47000000	| 0.91570000
3023.56000000	| 8.08180000
3023.74000000	| 0.20670000
3023.75000000	| 5.14220000
============================

Order Book: BTCUSDT
Previous volume: 2636454.559086 USDT
Current Change: 2980374.818854802 USDT
Volume Change: 343920.25976880174 USDT
Price			| Quantity
------------------------------
Bids:
64228.14000000	| 2.37075000
64228.10000000	| 0.00282000
64228.09000000	| 0.12993000
64228.04000000	| 0.00097000
64227.46000000	| 0.15007000
64227.03000000	| 0.15550000
64226.62000000	| 0.10000000
64226.60000000	| 0.16995000
64225.16000000	| 0.06000000
64225.11000000	| 0.02275000
64224.97000000	| 0.00009000
64224.96000000	| 0.12993000
64224.70000000	| 0.04707000
64224.14000000	| 0.07807000
64224.12000000	| 0.12993000
64223.43000000	| 0.10000000
64223.31000000	| 0.01746000
64223.17000000	| 0.12993000
64222.75000000	| 0.00077000
64222.73000000	| 0.04707000
64222.60000000	| 0.10000000
64222.48000000	| 0.17613000
64221.78000000	| 0.39575000
64221.72000000	| 0.00242000
64220.62000000	| 0.65954000
64220.36000000	| 0.00012000
64220.32000000	| 0.04707000
64219.17000000	| 0.07617000
64218.63000000	| 0.21515000
64218.34000000	| 0.02317000
64217.70000000	| 0.00155000
64217.35000000	| 0.38378000
64212.13000000	| 0.18004000
64211.87000000	| 0.04621000
64206.62000000	| 0.30519000
64206.60000000	| 0.46889000
64204.05000000	| 0.38803000
64202.08000000	| 0.67187000
64201.52000000	| 0.51221000
64200.97000000	| 0.00067000
64200.72000000	| 0.31114000
64200.02000000	| 0.30519000
64199.98000000	| 0.54325000
64198.91000000	| 0.18656000
64198.61000000	| 0.06223000
64197.04000000	| 0.00050000
64197.01000000	| 0.23355000
64195.92000000	| 0.00009000
64195.65000000	| 0.39574000
64195.09000000	| 0.71822000
------------------------------
Asks:
64228.15000000	| 0.41050000
64234.60000000	| 0.15548000
64235.13000000	| 0.12005000
64237.48000000	| 0.02279000
64240.87000000	| 1.68786000
64241.62000000	| 0.51221000
64243.25000000	| 0.39501000
64243.26000000	| 0.42462000
64243.99000000	| 0.65828000
64244.79000000	| 0.00019000
64245.89000000	| 0.13000000
64246.74000000	| 0.00009000
64246.75000000	| 0.13000000
64247.59000000	| 0.13000000
64247.63000000	| 0.39357000
64248.57000000	| 0.51221000
64249.16000000	| 0.30851000
64250.00000000	| 0.00328000
64250.32000000	| 0.07801000
64250.33000000	| 0.13000000
64252.34000000	| 0.55238000
64252.35000000	| 0.92053000
64253.62000000	| 0.67179000
64254.79000000	| 0.54325000
64255.85000000	| 0.00050000
64255.95000000	| 0.00827000
64260.06000000	| 0.74036000
64260.53000000	| 0.85368000
64260.77000000	| 0.09466000
64261.24000000	| 0.02500000
64262.23000000	| 0.06000000
64262.59000000	| 0.00119000
64265.35000000	| 0.00243000
64268.24000000	| 0.01600000
64272.58000000	| 0.06233000
64272.69000000	| 0.31079000
64276.29000000	| 0.06002000
64278.03000000	| 0.19523000
64279.09000000	| 0.04172000
64283.09000000	| 0.00466000
64285.40000000	| 0.31073000
64288.95000000	| 0.04310000
64293.08000000	| 0.14176000
64295.30000000	| 0.15511000
64299.51000000	| 0.10722000
64301.42000000	| 0.27539000
64302.97000000	| 0.20000000
64312.70000000	| 0.18319000
64320.69000000	| 0.31055000
64321.54000000	| 0.11203000
============================

Order Book: ETHUSDT
Previous volume: 2296003.6235379986 USDT
Current Change: 779431.1379180002 USDT
Volume Change: -1516572.4856199985 USDT
Price			| Quantity
------------------------------
Bids:
3017.45000000	| 13.40120000
3017.42000000	| 4.96500000
3017.40000000	| 6.40890000
3017.37000000	| 2.30000000
3017.23000000	| 1.66750000
3017.13000000	| 0.82480000
3017.04000000	| 0.03300000
3016.70000000	| 0.38870000
3016.69000000	| 0.90190000
3016.49000000	| 5.26120000
3016.45000000	| 0.38370000
3016.41000000	| 3.55300000
3016.40000000	| 0.00180000
3016.39000000	| 1.21790000
3016.35000000	| 6.56560000
3016.20000000	| 10.15220000
3016.08000000	| 2.53760000
3015.84000000	| 16.55870000
3015.45000000	| 9.94010000
3015.43000000	| 2.40250000
3015.39000000	| 2.64650000
3015.30000000	| 8.17850000
3015.16000000	| 0.00350000
3015.15000000	| 0.00190000
3015.00000000	| 16.23310000
3014.90000000	| 4.13760000
3014.73000000	| 0.10350000
3014.49000000	| 1.31910000
3013.47000000	| 9.77950000
3013.30000000	| 6.11170000
3012.76000000	| 0.07630000
3012.64000000	| 1.00520000
3012.57000000	| 0.36350000
3012.19000000	| 0.04300000
3011.35000000	| 4.21800000
3009.40000000	| 18.00000000
3009.27000000	| 0.20000000
3007.32000000	| 0.00570000
2997.83000000	| 0.40020000
2990.66000000	| 0.01160000
2981.45000000	| 0.29830000
1086.28000000	| 0.00560000
------------------------------
Asks:
3017.46000000	| 11.41300000
3017.55000000	| 0.00180000
3017.60000000	| 1.36700000
3017.65000000	| 1.20350000
3017.79000000	| 0.82030000
3017.80000000	| 1.36700000
3017.82000000	| 1.31750000
3017.90000000	| 5.00280000
3018.05000000	| 0.00180000
3018.13000000	| 0.15700000
3018.15000000	| 8.27220000
3018.22000000	| 0.00220000
3018.30000000	| 5.68920000
3018.45000000	| 0.47210000
3018.59000000	| 4.34460000
3018.84000000	| 0.03300000
3019.03000000	| 9.15740000
3020.08000000	| 3.49270000
3020.09000000	| 5.82000000
3020.85000000	| 0.15190000
3021.23000000	| 0.61060000
3021.29000000	| 1.00000000
3021.85000000	| 4.60330000
3021.89000000	| 7.66760000
3022.29000000	| 6.61630000
3026.00000000	| 0.21330000
3035.00000000	| 14.18670000
3035.91000000	| 0.40000000
3037.54000000	| 0.00290000
3050.90000000	| 0.30120000
3060.31000000	| 0.01710000
============================
```