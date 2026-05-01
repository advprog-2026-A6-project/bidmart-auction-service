package id.ac.ui.cs.advprog.auction.module.auction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import id.ac.ui.cs.advprog.auction.module.auction.dto.PlaceBidRequest;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import id.ac.ui.cs.advprog.auction.module.auction.repository.AuctionRepository;
import id.ac.ui.cs.advprog.auction.module.auction.repository.BidRepository;
import id.ac.ui.cs.advprog.auction.module.auction.service.AuctionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
class AuctionLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionService auctionService;

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

    private long createDraftAuctionWithReserve(int reservePrice, int durationMinutes) throws Exception {
        String response = mockMvc.perform(post("/api/auctions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {%n\
                                  "title": "Gaming PC",%n\
                                  "description": "RTX setup",%n\
                                  "startPrice": 1000000,%n\
                                  "minIncrement": 50000,%n\
                                  "reservePrice": %d,%n\
                                  "durationMinutes": %d%n\
                                }%n\
                                """.formatted(reservePrice, durationMinutes)))
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
                                  "amount": 999999
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

    @Test
    void endedAuctionShouldBecomeUnsoldWhenReservePriceNotReached() throws Exception {
        long auctionId = createDraftAuctionWithReserve(1300000, 10);

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
                .andExpect(status().isCreated());

        var auction = auctionRepository.findById(auctionId).orElseThrow();
        auction.setEndAt(LocalDateTime.now().minusSeconds(1));
        auction.setStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);

        mockMvc.perform(get("/api/auctions/{id}", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNSOLD"))
                .andExpect(jsonPath("$.winnerBidderName").doesNotExist())
                .andExpect(jsonPath("$.winningBid").doesNotExist());
    }

    @Test
    void endedAuctionShouldSelectWinnerWhenReservePriceReached() throws Exception {
        long auctionId = createDraftAuctionWithReserve(1100000, 10);

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
                .andExpect(status().isCreated());

        var auction = auctionRepository.findById(auctionId).orElseThrow();
        auction.setEndAt(LocalDateTime.now().minusSeconds(1));
        auction.setStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);

        mockMvc.perform(get("/api/auctions/{id}", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WON"))
                .andExpect(jsonPath("$.winnerBidderName").value("Ishak"))
                .andExpect(jsonPath("$.winningBid").value(1200000));
    }

    @Test
    void concurrentBidsWithSameAmountShouldAcceptOnlyOneWinner() throws Exception {
        long auctionId = createDraftAuction();

        mockMvc.perform(post("/api/auctions/{id}/activate", auctionId))
                .andExpect(status().isOk());

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch ready = new CountDownLatch(2);
            CountDownLatch start = new CountDownLatch(1);

            Future<Integer> first = executor.submit(placeBidWhenReleased(auctionId, "Ishak", ready, start));
            Future<Integer> second = executor.submit(placeBidWhenReleased(auctionId, "Sari", ready, start));

            org.junit.jupiter.api.Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            List<Integer> statuses = List.of(first.get(5, TimeUnit.SECONDS), second.get(5, TimeUnit.SECONDS))
                    .stream()
                    .sorted()
                    .toList();

            org.junit.jupiter.api.Assertions.assertEquals(List.of(201, 400), statuses);
        }

        var bids = bidRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId);
        org.junit.jupiter.api.Assertions.assertEquals(1, bids.size());
        org.junit.jupiter.api.Assertions.assertEquals(0, new BigDecimal("1200000").compareTo(bids.getFirst().getAmount()));

        var auction = auctionRepository.findById(auctionId).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                new BigDecimal("1200000").compareTo(auction.getCurrentHighestBid())
        );
    }

    private Callable<Integer> placeBidWhenReleased(
            long auctionId,
            String bidderName,
            CountDownLatch ready,
            CountDownLatch start
    ) {
        return () -> {
            PlaceBidRequest request = new PlaceBidRequest();
            request.setBidderName(bidderName);
            request.setAmount(new BigDecimal("1200000"));

            ready.countDown();
            org.junit.jupiter.api.Assertions.assertTrue(start.await(5, TimeUnit.SECONDS));

            try {
                auctionService.placeBid(auctionId, request);
                return 201;
            } catch (ResponseStatusException ex) {
                return ex.getStatusCode().value();
            }
        };
    }
}
