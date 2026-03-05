package id.ac.ui.cs.advprog.auction.module.auction.dto;

import id.ac.ui.cs.advprog.auction.module.auction.model.Bid;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidResponse {
    private Long id;
    private Long auctionId;
    private String bidderName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private BigDecimal currentHighestBid;

    public static BidResponse from(Bid bid, BigDecimal currentHighestBid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAuctionId(bid.getAuction().getId());
        response.setBidderName(bid.getBidderName());
        response.setAmount(bid.getAmount());
        response.setCreatedAt(bid.getCreatedAt());
        response.setCurrentHighestBid(currentHighestBid);
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(BigDecimal currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }
}
