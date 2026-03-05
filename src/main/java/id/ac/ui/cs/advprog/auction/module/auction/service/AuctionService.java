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

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    public AuctionResponse createDraft(CreateAuctionRequest request) {
        Auction auction = new Auction();
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartPrice(request.getStartPrice());
        auction.setMinIncrement(request.getMinIncrement());
        auction.setDurationMinutes(request.getDurationMinutes());
        auction.setStatus(AuctionStatus.DRAFT);
        return AuctionResponse.from(auctionRepository.save(auction));
    }

    public AuctionResponse getById(Long id) {
        return AuctionResponse.from(findAuctionById(id));
    }

    public List<AuctionResponse> findAll() {
        return auctionRepository.findAll()
                .stream()
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
        Auction auction = findAuctionById(id);
        if (auction.getStatus() != AuctionStatus.ACTIVE && auction.getStatus() != AuctionStatus.EXTENDED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bid is only allowed when auction is ACTIVE or EXTENDED"
            );
        }

        BigDecimal minimumAllowed = minimumAllowedBid(auction);
        if (request.getAmount().compareTo(minimumAllowed) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bid amount must be greater than " + minimumAllowed.stripTrailingZeros().toPlainString()
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
        findAuctionById(id);
        return bidRepository.findByAuctionIdOrderByCreatedAtDesc(id)
                .stream()
                .map(bid -> BidResponse.from(bid, bid.getAmount()))
                .toList();
    }

    private void applyLateBidExtension(Auction auction, LocalDateTime bidTime) {
        if (auction.getEndAt() == null) {
            return;
        }
        Duration remaining = Duration.between(bidTime, auction.getEndAt());
        if (!remaining.isNegative() && remaining.compareTo(Duration.ofMinutes(2)) <= 0) {
            auction.setEndAt(bidTime.plusMinutes(2));
            auction.setStatus(AuctionStatus.EXTENDED);
        }
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
}
