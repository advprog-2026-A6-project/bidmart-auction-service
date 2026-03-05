package id.ac.ui.cs.advprog.auction.module.auction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuctionLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private long createDraftAuction() throws Exception {
        String response = mockMvc.perform(post("/api/auctions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Gaming PC",
                                  "description": "RTX setup",
                                  "startPrice": 1000000,
                                  "minIncrement": 50000,
                                  "durationMinutes": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idValue = JsonPath.read(response, "$.id");
        return idValue.longValue();
    }

    @Test
    void bidShouldFailWhenAuctionIsDraft() throws Exception {
        long auctionId = createDraftAuction();

        mockMvc.perform(post("/api/auctions/{id}/bids", auctionId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "bidderName": "Ishak",
                                  "amount": 1200000
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void bidShouldFailWhenAmountTooLow() throws Exception {
        long auctionId = createDraftAuction();

        mockMvc.perform(post("/api/auctions/{id}/activate", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(post("/api/auctions/{id}/bids", auctionId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "bidderName": "Ishak",
                                  "amount": 1000000
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bidShouldSucceedAndUpdateCurrentHighestBid() throws Exception {
        long auctionId = createDraftAuction();

        mockMvc.perform(post("/api/auctions/{id}/activate", auctionId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auctions/{id}/bids", auctionId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "bidderName": "Ishak",
                                  "amount": 1200000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentHighestBid").value(1200000));

        mockMvc.perform(get("/api/auctions/{id}", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentHighestBid").value(1200000));
    }

    @Test
    void lateBidShouldExtendAuctionEndAt() throws Exception {
        String createResponse = mockMvc.perform(post("/api/auctions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Sneaker Limited",
                                  "description": "Drop edition",
                                  "startPrice": 500000,
                                  "minIncrement": 10000,
                                  "durationMinutes": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number idValue = JsonPath.read(createResponse, "$.id");
        long auctionId = idValue.longValue();

        String activateResponse = mockMvc.perform(post("/api/auctions/{id}/activate", auctionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String initialEndAtText = JsonPath.read(activateResponse, "$.endAt");
        LocalDateTime initialEndAt = LocalDateTime.parse(initialEndAtText);

        String bidResponse = mockMvc.perform(post("/api/auctions/{id}/bids", auctionId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "bidderName": "Ari",
                                  "amount": 600000
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String detailResponse = mockMvc.perform(get("/api/auctions/{id}", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXTENDED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newEndAtText = JsonPath.read(detailResponse, "$.endAt");
        LocalDateTime newEndAt = LocalDateTime.parse(newEndAtText);

        org.junit.jupiter.api.Assertions.assertTrue(newEndAt.isAfter(initialEndAt));
        org.junit.jupiter.api.Assertions.assertNotNull(JsonPath.read(bidResponse, "$.id"));
    }
}
