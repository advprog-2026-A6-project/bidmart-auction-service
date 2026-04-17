package id.ac.ui.cs.advprog.auction.module.auction.repository;

import id.ac.ui.cs.advprog.auction.module.auction.model.Bid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);

    Optional<Bid> findTopByAuctionIdOrderByAmountDescCreatedAtAsc(Long auctionId);
}
