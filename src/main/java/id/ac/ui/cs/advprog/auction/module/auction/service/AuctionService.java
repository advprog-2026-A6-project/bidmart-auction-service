package id.ac.ui.cs.advprog.auction.module.auction.service;

import id.ac.ui.cs.advprog.auction.module.auction.dto.AuctionResponse;
import id.ac.ui.cs.advprog.auction.module.auction.dto.CreateAuctionRequest;
import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import id.ac.ui.cs.advprog.auction.module.auction.model.AuctionStatus;
import id.ac.ui.cs.advprog.auction.module.auction.repository.AuctionRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public AuctionResponse getById(Long id) {
        return AuctionResponse.from(findAuctionById(id));
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

    private Auction findAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));
    }
}
