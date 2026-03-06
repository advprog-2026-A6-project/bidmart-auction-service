package id.ac.ui.cs.advprog.auction.module.auction.controller;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.BidResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.dto.PlaceBidRequest;
import id.ac.ui.cs.advprog.auction.module.auction.service.AuctionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<AuctionResponse> listAuctions() {
        return auctionService.findAll();
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@Valid @RequestBody CreateAuctionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.createDraft(request));
    }

    @GetMapping("/{id}")
    public AuctionResponse getAuction(@PathVariable Long id) {
        return auctionService.getById(id);
    }

    @PostMapping("/{id}/activate")
    public AuctionResponse activateAuction(@PathVariable Long id) {
        return auctionService.activate(id);
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<BidResponse> placeBid(@PathVariable Long id, @Valid @RequestBody PlaceBidRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.placeBid(id, request));
    }

    @GetMapping("/{id}/bids")
    public List<BidResponse> listBids(@PathVariable Long id) {
        return auctionService.findBidsByAuctionId(id);
    }
}

// test