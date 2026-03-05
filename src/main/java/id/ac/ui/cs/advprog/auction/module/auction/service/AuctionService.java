package id.ac.ui.cs.advprog.auction.module.auction.service;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import id.ac.ui.cs.advprog.auction.module.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
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
}
