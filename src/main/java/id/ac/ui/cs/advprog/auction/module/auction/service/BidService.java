package id.ac.ui.cs.advprog.auction.module.auction.service;

import id.ac.ui.cs.advprog.auction.module.auction.dto.ListingBidStatusResponse;
import id.ac.ui.cs.advprog.auction.module.auction.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BidService {

    private final BidRepository bidRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public ListingBidStatusResponse getListingBidStatus(UUID listingId) {
        long count = bidRepository.countByListingId(listingId);
        boolean hasBids = count > 0;
        return new ListingBidStatusResponse(listingId, hasBids, count);
    }
}
