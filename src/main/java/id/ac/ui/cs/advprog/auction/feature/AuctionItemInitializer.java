package id.ac.ui.cs.advprog.auction.feature;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AuctionItemInitializer implements ApplicationRunner {

    private final AuctionItemRepository auctionItemRepository;

    public AuctionItemInitializer(AuctionItemRepository auctionItemRepository) {
        this.auctionItemRepository = auctionItemRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (auctionItemRepository.count() == 0) {
            auctionItemRepository.save(new AuctionItem("Gaming Laptop", 12000000));
            auctionItemRepository.save(new AuctionItem("Mechanical Keyboard", 900000));
            auctionItemRepository.save(new AuctionItem("27-inch Monitor", 2600000));
        }
    }
}
