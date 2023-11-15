package com.example.Darkstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/depots")
public class DepotController {

    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/")
    public List<Depot> getAllDepots() {
        return depotRepository.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<String> createDepot(@RequestBody Depot depot) {
        if ("main depot".equalsIgnoreCase(depot.getType())) {
            List<Depot> mainDepots = depotRepository.findMainDepots();
            if (!mainDepots.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There can be only one main depot.");
            }
        }
        Depot savedDepot = depotRepository.save(depot);
        return ResponseEntity.ok("Depot has been created.");
    }

    @GetMapping("/{id}")
    public Depot getDepot(@PathVariable Long id) {
        return depotRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDepot(@PathVariable Long id, @RequestBody Depot updatedDepot) {
        Depot depot = depotRepository.findById(id).orElse(null);
        if (depot != null) {
            if ("main depot".equalsIgnoreCase(updatedDepot.getType())) {
                List<Depot> mainDepots = depotRepository.findMainDepotsExcludingCurrent(id);
                if (!mainDepots.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There can be only one main depot.");
                }
            }
            depot.setDepotName(updatedDepot.getDepotName());
            depot.setType(updatedDepot.getType());
            depot.setCity(updatedDepot.getCity());
            depot.setLocation(updatedDepot.getLocation());
            depot.setStatus(updatedDepot.getStatus());
            depot.setCostCenter(updatedDepot.getCostCenter());

            Depot updatedDepotEntity = depotRepository.save(depot);
            return ResponseEntity.ok("Depot has been updated.");
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteDepot(@PathVariable Long id) {
        depotRepository.deleteById(id);
    }

    @PutMapping("/{id}/shutdown")
    public ResponseEntity<String> shutdownDepot(@PathVariable Long id) {
        Depot depot = depotRepository.findById(id).orElse(null);
        if (depot != null) {
            if (depot.getStatus().equalsIgnoreCase("closed")) {
                return ResponseEntity.badRequest().body("Depot is already closed.");
            }
            else {
                depot.setStatus("closed");
                depotRepository.save(depot);

                transferStocksToMainDepot(depot);

                return ResponseEntity.ok("Depot has been closed, and stocks have been transferred to the main depot.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void transferStocksToMainDepot(Depot closedDepot) {
        Depot mainDepot = depotRepository.findByType("main depot");

        if (mainDepot != null) {
            List<Stock> closedDepotStocks = stockRepository.findByDepot(closedDepot);

            for (Stock closedDepotStock : closedDepotStocks) {
                Stock mainDepotStock = stockRepository.findByProductAndDepot(closedDepotStock.getProduct(), mainDepot);

                if (mainDepotStock != null) {
                    mainDepotStock.setQuantity(mainDepotStock.getQuantity() + closedDepotStock.getQuantity());
                } else {
                    mainDepotStock = new Stock();
                    mainDepotStock.setProduct(closedDepotStock.getProduct());
                    mainDepotStock.setDepot(mainDepot);
                    mainDepotStock.setQuantity(closedDepotStock.getQuantity());
                }

                stockRepository.save(mainDepotStock);
                stockRepository.delete(closedDepotStock);
            }
        }
    }

}
