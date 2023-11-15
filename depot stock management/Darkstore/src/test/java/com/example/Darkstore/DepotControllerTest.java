package com.example.Darkstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DepotController.class)
public class DepotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepotRepository depotRepository;

    @MockBean
    private StockRepository stockRepository;

    @Test
    public void testGetAllDepots() throws Exception {
        List<Depot> depots = Arrays.asList(new Depot(), new Depot());
        when(depotRepository.findAll()).thenReturn(depots);
        mockMvc.perform(get("/depots/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testCreateDepot() throws Exception {
        Depot newDepot = new Depot();
        when(depotRepository.save(any(Depot.class))).thenReturn(newDepot);
        mockMvc.perform(post("/depots/")
                        .content(asJsonString(newDepot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetDepot() throws Exception {
        Depot depot = new Depot();
        when(depotRepository.findById(any(Long.class))).thenReturn(Optional.of(depot));
        mockMvc.perform(get("/depots/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateDepot() throws Exception {
        Depot existingDepot = new Depot();
        when(depotRepository.findById(any(Long.class))).thenReturn(Optional.of(existingDepot));
        Depot updatedDepot = new Depot();
        when(depotRepository.save(any(Depot.class))).thenReturn(updatedDepot);
        mockMvc.perform(put("/depots/1")
                        .content(asJsonString(updatedDepot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Depot has been updated.")));
    }

    @Test
    public void testDeleteDepot() throws Exception {
        mockMvc.perform(delete("/depots/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testShutdownDepot() throws Exception {
        Depot depot = new Depot();
        depot.setStatus("open");
        when(depotRepository.findById(any(Long.class))).thenReturn(Optional.of(depot));
        mockMvc.perform(put("/depots/1/shutdown"))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
