package id.ac.ui.cs.advprog.auction.module.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.dto.PlaceBidRequest;
import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import id.ac.ui.cs.advprog.auction.module.auction.model.Bid;
import id.ac.ui.cs.advprog.auction.module.auction.repository.AuctionRepository;
import id.ac.ui.cs.advprog.auction.module.auction.repository.BidRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private AuctionService auctionService;

    private Auction auction;

    @BeforeEach
    void setUp() {
        auction = new Auction();
        auction.setId(1L);
        auction.setTitle("Laptop");
        auction.setDescription("Gaming");
        auction.setStartPrice(new BigDecimal("10000"));
        auction.setMinIncrement(new BigDecimal("5000"));
        auction.setDurationMinutes(10);
        auction.setStatus(AuctionStatus.DRAFT);
    }

    @Test
    void createDraftShouldPersistAuctionWithDraftStatus() {
        CreateAuctionRequest request = new CreateAuctionRequest();
        request.setTitle("Phone");
        request.setDescription("Brand new");
        request.setStartPrice(new BigDecimal("2000"));
        request.setMinIncrement(new BigDecimal("100"));
        request.setDurationMinutes(20);

        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> {
            Auction saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        AuctionResponse response = auctionService.createDraft(request);

        assertEquals(10L, response.getId());
        assertEquals(AuctionStatus.DRAFT, response.getStatus());
        assertEquals("Phone", response.getTitle());
    }

    @Test
    void activateShouldRejectNonDraftAuction() {
        auction.setStatus(AuctionStatus.ACTIVE);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> auctionService.activate(1L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void placeBidShouldRejectWhenAuctionNotActiveOrExtended() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        PlaceBidRequest request = new PlaceBidRequest();
        request.setBidderName("A");
        request.setAmount(new BigDecimal("12000"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> auctionService.placeBid(1L, request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void placeBidShouldRejectLowerThanMinimumAllowed() {
        auction.setStatus(AuctionStatus.ACTIVE);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        PlaceBidRequest request = new PlaceBidRequest();
        request.setBidderName("A");
        request.setAmount(new BigDecimal("9999"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> auctionService.placeBid(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void placeBidShouldUpdateHighestAndExtendWhenInLastTwoMinutes() {
        auction.setStatus(AuctionStatus.ACTIVE);
        LocalDateTime bidTime = LocalDateTime.of(2026, 3, 6, 10, 0);
        auction.setEndAt(bidTime.plusMinutes(1));
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> {
            Bid bid = invocation.getArgument(0);
            bid.setId(99L);
            bid.setCreatedAt(bidTime);
            return bid;
        });

        PlaceBidRequest request = new PlaceBidRequest();
        request.setBidderName("A");
        request.setAmount(new BigDecimal("15000"));

        var response = auctionService.placeBid(1L, request);

        assertEquals(new BigDecimal("15000"), response.getCurrentHighestBid());
        assertEquals(AuctionStatus.EXTENDED, auction.getStatus());
        assertEquals(bidTime.plusMinutes(2), auction.getEndAt());
        verify(auctionRepository).save(auction);
    }

    @Test
    void findAllShouldMapEntities() {
        when(auctionRepository.findAll()).thenReturn(List.of(auction));

        List<AuctionResponse> responses = auctionService.findAll();

        assertEquals(1, responses.size());
        assertEquals("Laptop", responses.get(0).getTitle());
    }

    @Test
    void findBidsByAuctionIdShouldThrowWhenAuctionNotFound() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> auctionService.findBidsByAuctionId(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void findBidsByAuctionIdShouldReturnMappedBids() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        Bid bid = new Bid();
        bid.setId(11L);
        bid.setAuction(auction);
        bid.setBidderName("Budi");
        bid.setAmount(new BigDecimal("15000"));
        bid.setCreatedAt(LocalDateTime.now());
        when(bidRepository.findByAuctionIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(bid));

        var responses = auctionService.findBidsByAuctionId(1L);

        assertEquals(1, responses.size());
        assertEquals("Budi", responses.get(0).getBidderName());
        assertNotNull(responses.get(0).getAuctionId());
    }
}
