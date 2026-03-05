package id.ac.ui.cs.advprog.auction.module.auction.repository;

import id.ac.ui.cs.advprog.auction.module.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
