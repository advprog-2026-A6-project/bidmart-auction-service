package id.ac.ui.cs.advprog.auction.module.auction.dto;

import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal minIncrement;
    private BigDecimal reservePrice;
    private BigDecimal currentHighestBid;
    private BigDecimal winningBid;
    private String winnerBidderName;
    private Integer durationMinutes;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AuctionStatus status;

    public static AuctionResponse from(Auction auction) {
        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setTitle(auction.getTitle());
        response.setDescription(auction.getDescription());
        response.setStartPrice(auction.getStartPrice());
        response.setMinIncrement(auction.getMinIncrement());
        response.setReservePrice(auction.getReservePrice());
        response.setCurrentHighestBid(auction.getCurrentHighestBid());
        response.setWinningBid(auction.getWinningBid());
        response.setWinnerBidderName(auction.getWinnerBidderName());
        response.setDurationMinutes(auction.getDurationMinutes());
        response.setStartAt(auction.getStartAt());
        response.setEndAt(auction.getEndAt());
        response.setStatus(auction.getStatus());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getMinIncrement() {
        return minIncrement;
    }

    public void setMinIncrement(BigDecimal minIncrement) {
        this.minIncrement = minIncrement;
    }

    public BigDecimal getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }

    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(BigDecimal currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public BigDecimal getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(BigDecimal winningBid) {
        this.winningBid = winningBid;
    }

    public String getWinnerBidderName() {
        return winnerBidderName;
    }

    public void setWinnerBidderName(String winnerBidderName) {
        this.winnerBidderName = winnerBidderName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }
}
