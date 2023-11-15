package com.example.Darkstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByDepot(Depot depot);
    Stock findByProductAndDepot(Product product, Depot depot);
}

