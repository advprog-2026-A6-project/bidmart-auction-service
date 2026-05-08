package id.ac.ui.cs.advprog.auction.module.auction.repository;

import id.ac.ui.cs.advprog.auction.module.auction.model.Bid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);

    Optional<Bid> findTopByAuctionIdOrderByAmountDescCreatedAtAsc(Long auctionId);

    @Query("select count(b) from Bid b where b.auction.listingId = :listingId")
    long countByListingId(@Param("listingId") UUID listingId);
}
