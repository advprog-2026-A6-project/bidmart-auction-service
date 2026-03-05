package id.ac.ui.cs.advprog.auction.module.auction.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

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

    @GetMapping
    public void listAuctionsPlaceholder() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Auction module skeleton only");
    }
}
