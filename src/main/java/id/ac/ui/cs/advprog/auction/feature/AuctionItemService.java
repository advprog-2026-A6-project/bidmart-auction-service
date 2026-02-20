package id.ac.ui.cs.advprog.auction.feature;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuctionItemService {

    private final AuctionItemRepository auctionItemRepository;

    public AuctionItemService(AuctionItemRepository auctionItemRepository) {
        this.auctionItemRepository = auctionItemRepository;
    }

    public List<AuctionItem> findAll() {
        return auctionItemRepository.findAll();
    }

    public AuctionItem findById(Long id) {
        return auctionItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    public AuctionItem create(AuctionItem request) {
        return auctionItemRepository.save(new AuctionItem(request.getName(), request.getStartingPrice()));
    }

    public AuctionItem update(Long id, AuctionItem request) {
        AuctionItem item = findById(id);
        item.setName(request.getName());
        item.setStartingPrice(request.getStartingPrice());
        return auctionItemRepository.save(item);
    }

    public void delete(Long id) {
        AuctionItem item = findById(id);
        auctionItemRepository.delete(item);
    }
}
