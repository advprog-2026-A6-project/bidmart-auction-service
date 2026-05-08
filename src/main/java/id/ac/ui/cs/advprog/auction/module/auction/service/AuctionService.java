package id.ac.ui.cs.advprog.auction.module.auction.service;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.BidResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.dto.PlaceBidRequest;
import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import id.ac.ui.cs.advprog.auction.module.auction.model.Bid;
import id.ac.ui.cs.advprog.auction.module.auction.repository.AuctionRepository;
import id.ac.ui.cs.advprog.auction.module.auction.repository.BidRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuctionService {

    private static final Duration ANTI_SNIPING_EXTENSION = Duration.ofMinutes(2);
    private static final Duration ANTI_SNIPING_WINDOW = Duration.ofMinutes(2);

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    public AuctionResponse createDraft(CreateAuctionRequest request) {
        validateReservePrice(request);
        Auction auction = new Auction();
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartPrice(request.getStartPrice());
        auction.setMinIncrement(request.getMinIncrement());
        auction.setReservePrice(request.getReservePrice());
        auction.setDurationMinutes(request.getDurationMinutes());
        auction.setStatus(AuctionStatus.DRAFT);
        auction.setListingId(request.getListingId());
        return AuctionResponse.from(auctionRepository.save(auction));
    }

    public AuctionResponse getById(Long id) {
        Auction auction = finalizeAuctionIfExpired(findAuctionById(id));
        return AuctionResponse.from(auction);
    }

    public List<AuctionResponse> findAll() {
        return auctionRepository.findAll()
                .stream()
                .map(this::finalizeAuctionIfExpired)
                .map(AuctionResponse::from)
                .toList();
    }

    public AuctionResponse activate(Long id) {
        Auction auction = findAuctionById(id);
        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Auction can only be activated from DRAFT");
        }

        LocalDateTime startAt = LocalDateTime.now();
        auction.setStartAt(startAt);
        auction.setEndAt(startAt.plusMinutes(auction.getDurationMinutes()));
        auction.setStatus(AuctionStatus.ACTIVE);

        return AuctionResponse.from(auctionRepository.save(auction));
    }

    @Transactional
    public BidResponse placeBid(Long id, PlaceBidRequest request) {
        Auction auction = finalizeAuctionIfExpired(findAuctionByIdForUpdate(id));
        if (auction.getStatus() != AuctionStatus.ACTIVE && auction.getStatus() != AuctionStatus.EXTENDED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bid is only allowed when auction is ACTIVE or EXTENDED"
            );
        }

        BigDecimal minimumAllowed = minimumAllowedBid(auction);
        if (request.getAmount().compareTo(minimumAllowed) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bid amount must be greater than or equal to "
                            + minimumAllowed.stripTrailingZeros().toPlainString()
            );
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidderName(request.getBidderName());
        bid.setAmount(request.getAmount());

        Bid savedBid = bidRepository.save(bid);
        auction.setCurrentHighestBid(savedBid.getAmount());
        applyLateBidExtension(auction, savedBid.getCreatedAt());
        auctionRepository.save(auction);

        return BidResponse.from(savedBid, auction.getCurrentHighestBid());
    }

    public List<BidResponse> findBidsByAuctionId(Long id) {
        finalizeAuctionIfExpired(findAuctionById(id));
        return bidRepository.findByAuctionIdOrderByCreatedAtDesc(id)
                .stream()
                .map(bid -> BidResponse.from(bid, bid.getAmount()))
                .toList();
    }

    private void validateReservePrice(CreateAuctionRequest request) {
        if (request.getReservePrice() != null
                && request.getReservePrice().compareTo(request.getStartPrice()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reserve price must be greater than or equal to start price"
            );
        }
    }

    private void applyLateBidExtension(Auction auction, LocalDateTime bidTime) {
        if (auction.getEndAt() == null) {
            return;
        }
        Duration remaining = Duration.between(bidTime, auction.getEndAt());
        if (!remaining.isNegative() && remaining.compareTo(ANTI_SNIPING_WINDOW) <= 0) {
            auction.setEndAt(bidTime.plus(ANTI_SNIPING_EXTENSION));
            auction.setStatus(AuctionStatus.EXTENDED);
        }
    }

    private Auction finalizeAuctionIfExpired(Auction auction) {
        if (!isRunningAuction(auction) || auction.getEndAt() == null || auction.getEndAt().isAfter(LocalDateTime.now())) {
            return auction;
        }

        return finalizeAuction(auction);
    }

    private Auction finalizeAuction(Auction auction) {
        Bid highestBid = bidRepository.findTopByAuctionIdOrderByAmountDescCreatedAtAsc(auction.getId()).orElse(null);
        if (highestBid == null || !hasMetReservePrice(auction, highestBid.getAmount())) {
            auction.setStatus(AuctionStatus.UNSOLD);
            auction.setWinningBid(null);
            auction.setWinnerBidderName(null);
            return auctionRepository.save(auction);
        }

        auction.setStatus(AuctionStatus.WON);
        auction.setWinningBid(highestBid.getAmount());
        auction.setWinnerBidderName(highestBid.getBidderName());
        auction.setCurrentHighestBid(highestBid.getAmount());
        return auctionRepository.save(auction);
    }

    private boolean isRunningAuction(Auction auction) {
        return auction.getStatus() == AuctionStatus.ACTIVE || auction.getStatus() == AuctionStatus.EXTENDED;
    }

    private boolean hasMetReservePrice(Auction auction, BigDecimal amount) {
        return auction.getReservePrice() == null || amount.compareTo(auction.getReservePrice()) >= 0;
    }

    private BigDecimal minimumAllowedBid(Auction auction) {
        if (auction.getCurrentHighestBid() == null) {
            return auction.getStartPrice();
        }
        return auction.getCurrentHighestBid().add(auction.getMinIncrement());
    }

    private Auction findAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));
    }

    private Auction findAuctionByIdForUpdate(Long id) {
        return auctionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));
    }
}
