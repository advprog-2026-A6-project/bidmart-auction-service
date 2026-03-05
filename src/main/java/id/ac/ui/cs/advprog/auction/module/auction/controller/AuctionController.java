package id.ac.ui.cs.advprog.auction.module.auction.controller;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.service.AuctionService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/planned")
    public Map<String, Object> plannedEndpoints() {
        return Map.of(
                "scope", "Auction lifecycle and bidding",
                "plannedEndpoints", new String[]{
                    "POST /api/auctions",
                    "POST /api/auctions/{id}/activate",
                    "POST /api/auctions/{id}/bids"
                }
        );
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@Valid @RequestBody CreateAuctionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.createDraft(request));
    }

    @GetMapping
    public void listAuctionsInNextCommit() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "List auctions is not implemented yet");
    }
}
