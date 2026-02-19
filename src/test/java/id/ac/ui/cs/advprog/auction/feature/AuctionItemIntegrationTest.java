package id.ac.ui.cs.advprog.auction.feature;

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
class AuctionItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listItemsShouldReturnSeedData() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].startingPrice").exists());
    }

    @Test
    void itemsPageShouldRenderFromDatabaseData() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Auction Items")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("not-set")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Gaming Laptop")));
    }

    @Test
    void stagingLinksShouldUseSafeDefaults() throws Exception {
        mockMvc.perform(get("/api/staging-links"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frontend").value("not-set"))
                .andExpect(jsonPath("$.backend").value("not-set"))
                .andExpect(jsonPath("$.database").value("not-set"));
    }

    @Test
    void crudApiShouldWork() throws Exception {
        String createResponse = mockMvc.perform(post("/api/items")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Wireless Mouse\",\"startingPrice\":350000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.startingPrice").value(350000))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idValue = JsonPath.read(createResponse, "$.id");
        long id = idValue.longValue();

        mockMvc.perform(put("/api/items/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Wireless Mouse Pro\",\"startingPrice\":420000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse Pro"))
                .andExpect(jsonPath("$.startingPrice").value(420000));

        mockMvc.perform(delete("/api/items/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/items/{id}", id))
                .andExpect(status().isNotFound());
    }
}
