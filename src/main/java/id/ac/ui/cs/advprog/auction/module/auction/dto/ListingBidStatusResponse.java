package id.ac.ui.cs.advprog.auction.module.auction.dto;

import java.util.UUID;

public class ListingBidStatusResponse {
    private UUID listingId;
    private boolean hasBids;
    private long bidCount;

    public ListingBidStatusResponse(UUID listingId, boolean hasBids, long bidCount) {
        this.listingId = listingId;
        this.hasBids = hasBids;
        this.bidCount = bidCount;
    }

    public UUID getListingId() { return listingId; }
    public boolean isHasBids() { return hasBids; }
    public long getBidCount() { return bidCount; }
    public void setListingId(UUID listingId) { this.listingId = listingId; }
    public void setHasBids(boolean hasBids) { this.hasBids = hasBids; }
    public void setBidCount(long bidCount) { this.bidCount = bidCount; }
}
