package com.example.Darkstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<String> createStock(@RequestBody Stock stock) {
        Depot depot = stock.getDepot();
        if (depot != null && depot.getType().equalsIgnoreCase("main depot")) {
            Stock savedStock = stockRepository.save(stock);
            return ResponseEntity.ok("Stock has been created.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stocks can only be defined in the main depot.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestBody Stock updatedStock) {
        Stock stock = stockRepository.findById(id).orElse(null);
        if (stock != null) {
            Depot depot = updatedStock.getDepot();
            stock.setProduct(updatedStock.getProduct());
            stock.setDepot(depot);
            stock.setQuantity(updatedStock.getQuantity());
            Stock updatedStockEntity = stockRepository.save(stock);
            return ResponseEntity.ok("Stock has been updated.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferStock(@RequestBody TransferRequest request) {
        Depot sourceDepot = depotRepository.findById(request.getSourceDepotId()).orElse(null);
        Depot destinationDepot = depotRepository.findById(request.getDestinationDepotId()).orElse(null);

        if (sourceDepot != null && destinationDepot != null) {
            Product product = productRepository.findById(request.getProductId()).orElse(null);
            if (product != null) {
                double distance = calculateDistance(sourceDepot, destinationDepot);

                if (sourceDepot.getCity().equalsIgnoreCase(destinationDepot.getCity())) {
                    if (distance < 100) {
                        Stock sourceStock = stockRepository.findByProductAndDepot(product, sourceDepot);
                        Stock destinationStock = stockRepository.findByProductAndDepot(product, destinationDepot);

                        if (sourceStock != null && sourceStock.getQuantity() >= request.getQuantity()) {
                            if (destinationStock != null) {
                                destinationStock.setQuantity(destinationStock.getQuantity() + request.getQuantity());
                            } else {
                                destinationStock = new Stock();
                                destinationStock.setProduct(product);
                                destinationStock.setDepot(destinationDepot);
                                destinationStock.setQuantity(request.getQuantity());
                            }

                            sourceStock.setQuantity(sourceStock.getQuantity() - request.getQuantity());
                            stockRepository.save(sourceStock);
                            stockRepository.save(destinationStock);

                            return ResponseEntity.ok("Stock transfer successful.");
                        } else {
                            return ResponseEntity.badRequest().body("Insufficient stock in the source depot.");
                        }
                    } else {
                        return ResponseEntity.badRequest().body("Inner-city transfers must be within 100km.");
                    }
                } else if (sourceDepot.getType().equalsIgnoreCase("main depot")) {
                        Stock sourceStock = stockRepository.findByProductAndDepot(product, sourceDepot);

                        if (sourceStock != null && sourceStock.getQuantity() >= request.getQuantity()) {
                            Stock destinationStock = stockRepository.findByProductAndDepot(product, destinationDepot);

                            if (destinationStock != null) {
                                destinationStock.setQuantity(destinationStock.getQuantity() + request.getQuantity());
                            } else {
                                destinationStock = new Stock();
                                destinationStock.setProduct(product);
                                destinationStock.setDepot(destinationDepot);
                                destinationStock.setQuantity(request.getQuantity());
                            }

                            sourceStock.setQuantity(sourceStock.getQuantity() - request.getQuantity());
                            stockRepository.save(sourceStock);
                            stockRepository.save(destinationStock);

                            return ResponseEntity.ok("Stock transfer successful.");
                        } else {
                            return ResponseEntity.badRequest().body("Insufficient stock in the source depot.");
                        }
                } else {
                    return ResponseEntity.badRequest().body("Inter-city transfers can only be made from the main depot.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    public double calculateDistance(Depot sourceDepot, Depot destinationDepot) {
        final int R = 6371;

        double lat1 = Double.parseDouble(sourceDepot.getLocation().split(",")[0]);
        double lon1 = Double.parseDouble(sourceDepot.getLocation().split(",")[1]);

        double lat2 = Double.parseDouble(destinationDepot.getLocation().split(",")[0]);
        double lon2 = Double.parseDouble(destinationDepot.getLocation().split(",")[1]);

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;
        return distance;
    }

    @GetMapping("/{id}")
    public Stock getStock(@PathVariable Long id) {
        return stockRepository.findById(id).orElse(null);
    }


    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id) {
        stockRepository.deleteById(id);
    }
}

