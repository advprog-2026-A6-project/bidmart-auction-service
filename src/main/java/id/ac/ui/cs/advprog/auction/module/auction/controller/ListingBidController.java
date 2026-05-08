package id.ac.ui.cs.advprog.auction.module.auction.controller;


import id.ac.ui.cs.advprog.auction.module.auction.dto.ListingBidStatusResponse;
import id.ac.ui.cs.advprog.auction.module.auction.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auctions/internal/listings")
@RequiredArgsConstructor
public class ListingBidController {

    private final BidService bidService;

    @GetMapping("/{listingId}/bids/status")
    public ListingBidStatusResponse getListingBidStatus(@PathVariable UUID listingId) {
        return bidService.getListingBidStatus(listingId);
    }
}
