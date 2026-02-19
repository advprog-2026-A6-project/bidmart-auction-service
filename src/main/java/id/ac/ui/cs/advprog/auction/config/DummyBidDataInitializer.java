package id.ac.ui.cs.advprog.auction.config;

import id.ac.ui.cs.advprog.auction.dummy.DummyBid;
import id.ac.ui.cs.advprog.auction.dummy.DummyBidRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyBidDataInitializer {

    @Bean
    CommandLineRunner seedDummyBids(DummyBidRepository dummyBidRepository) {
        return args -> {
            if (dummyBidRepository.count() == 0) {
                dummyBidRepository.save(new DummyBid("Alice", 150000));
                dummyBidRepository.save(new DummyBid("Bob", 175000));
                dummyBidRepository.save(new DummyBid("Charlie", 200000));
            }
        };
    }
}
