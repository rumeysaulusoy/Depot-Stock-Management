package com.example.Darkstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Optional;
import static com.example.Darkstore.ProductControllerTest.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private DepotRepository depotRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testGetAllStocks() throws Exception {
        when(stockRepository.findAll()).thenReturn(Arrays.asList(new Stock(), new Stock()));
        mockMvc.perform(get("/stocks/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testCreateStockInMainDepot() throws Exception {
        Depot mainDepot = new Depot();
        mainDepot.setType("main depot");
        mainDepot.setCity("A");
        mainDepot.setLocation("0,0");

        Product product = new Product();

        Stock newStock = new Stock();
        newStock.setProduct(product);
        newStock.setDepot(mainDepot);
        newStock.setQuantity(50);

        when(stockRepository.save(any(Stock.class))).thenReturn(newStock);

        mockMvc.perform(post("/stocks/")
                        .content(asJsonString(newStock))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    public void testCreateStockInNonMainDepot() throws Exception {
        Depot distributionCenter = new Depot();
        distributionCenter.setType("distribution center");
        distributionCenter.setCity("B");
        distributionCenter.setLocation("0,0");
        Product product = new Product();
        Stock newStock = new Stock();
        newStock.setProduct(product);
        newStock.setDepot(distributionCenter);
        newStock.setQuantity(50);

        mockMvc.perform(post("/stocks/")
                        .content(asJsonString(newStock))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testUpdateStock() throws Exception {
        Depot mainDepot = new Depot();
        mainDepot.setType("main depot");
        mainDepot.setCity("A");
        mainDepot.setLocation("0,0");

        Product product = new Product();

        Stock existingStock = new Stock();
        existingStock.setProduct(product);
        existingStock.setDepot(mainDepot);
        existingStock.setQuantity(50);

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock updatedStock = new Stock();
        updatedStock.setProduct(product);
        updatedStock.setDepot(mainDepot);
        updatedStock.setQuantity(75);

        mockMvc.perform(put("/stocks/1")
                        .content(asJsonString(updatedStock))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testInnerCityTransferDistanceExceedsLimit() throws Exception {
        Depot sourceDepot = new Depot();
        sourceDepot.setType("main depot");
        sourceDepot.setCity("A");
        sourceDepot.setLocation("0,0");

        Depot destinationDepot = new Depot();
        destinationDepot.setType("distribution center");
        destinationDepot.setCity("A");
        destinationDepot.setLocation("1.5,1.5");

        Product product = new Product();

        when(depotRepository.findById(1L)).thenReturn(Optional.of(sourceDepot));
        when(depotRepository.findById(2L)).thenReturn(Optional.of(destinationDepot));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));


        TransferRequest request = new TransferRequest();
        request.setSourceDepotId(1L);
        request.setDestinationDepotId(2L);
        request.setProductId(3L);
        request.setQuantity(50);
        mockMvc.perform(post("/stocks/transfer")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testInterCityTransferFromDistributionCenter() throws Exception {
        Depot sourceDepot = new Depot();
        sourceDepot.setType("distribution center");
        sourceDepot.setCity("A");
        sourceDepot.setLocation("0,0");

        Depot destinationDepot = new Depot();
        destinationDepot.setType("main depot");
        destinationDepot.setCity("B");
        destinationDepot.setLocation("1,1");

        Product product = new Product();

        when(depotRepository.findById(1L)).thenReturn(Optional.of(sourceDepot));
        when(depotRepository.findById(2L)).thenReturn(Optional.of(destinationDepot));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        Stock sourceStock = new Stock();
        sourceStock.setProduct(product);
        sourceStock.setDepot(sourceDepot);
        sourceStock.setQuantity(100);

        when(stockRepository.findByProductAndDepot(product, sourceDepot)).thenReturn(sourceStock);

        TransferRequest request = new TransferRequest();
        request.setSourceDepotId(1L);
        request.setDestinationDepotId(2L);
        request.setProductId(3L);
        request.setQuantity(50);
        mockMvc.perform(post("/stocks/transfer")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteStock() throws Exception {
        Stock stockToDelete = new Stock();
        stockToDelete.setId(1L);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stockToDelete));
        mockMvc.perform(delete("/stocks/1"))
                .andExpect(status().isOk());

    }


}
