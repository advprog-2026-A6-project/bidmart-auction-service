package id.ac.ui.cs.advprog.auction.dummy;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DummyBidIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiDummyBidsShouldReturnSeededDataFromDatabase() throws Exception {
        mockMvc.perform(get("/api/dummy-bids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bidderName").value("Alice"))
                .andExpect(jsonPath("$[0].amount").value(150000));
    }

    @Test
    void dummyBidsPageShouldRenderTemplateWithSeededData() throws Exception {
        mockMvc.perform(get("/dummy-bids"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dummy Bid Dashboard")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("not-set")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Alice")));
    }

    @Test
    void stagingLinksShouldUseSafeDefaultsWhenEnvIsMissing() throws Exception {
        mockMvc.perform(get("/api/staging-links"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frontend").value("not-set"))
                .andExpect(jsonPath("$.backend").value("not-set"))
                .andExpect(jsonPath("$.database").value("not-set"));
    }

    @Test
    void createAndGetByIdShouldWork() throws Exception {
        mockMvc.perform(post("/api/dummy-bids")
                        .contentType(APPLICATION_JSON)
                        .content("{\"bidderName\":\"Dora\",\"amount\":225000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bidderName").value("Dora"))
                .andExpect(jsonPath("$.amount").value(225000));

        mockMvc.perform(get("/api/dummy-bids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.bidderName == 'Dora')]").exists());
    }

    @Test
    void updateAndDeleteShouldWork() throws Exception {
        String body = mockMvc.perform(post("/api/dummy-bids")
                        .contentType(APPLICATION_JSON)
                        .content("{\"bidderName\":\"Eve\",\"amount\":130000}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long createdId = JsonPath.read(body, "$.id");

        mockMvc.perform(put("/api/dummy-bids/{id}", createdId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bidderName\":\"Eve Updated\",\"amount\":140000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bidderName").value("Eve Updated"))
                .andExpect(jsonPath("$.amount").value(140000));

        mockMvc.perform(delete("/api/dummy-bids/{id}", createdId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dummy-bids/{id}", createdId))
                .andExpect(status().isNotFound());
    }
}
