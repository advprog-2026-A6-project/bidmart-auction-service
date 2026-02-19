package id.ac.ui.cs.advprog.auction.dummy;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DummyBidService {

    private final DummyBidRepository dummyBidRepository;

    public DummyBidService(DummyBidRepository dummyBidRepository) {
        this.dummyBidRepository = dummyBidRepository;
    }

    public List<DummyBid> getAllBids() {
        return dummyBidRepository.findAll();
    }

    public DummyBid getBidById(Long id) {
        return dummyBidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid not found"));
    }

    public DummyBid createBid(DummyBid bid) {
        return dummyBidRepository.save(new DummyBid(bid.getBidderName(), bid.getAmount()));
    }

    public DummyBid updateBid(Long id, DummyBid updatedBid) {
        DummyBid existingBid = getBidById(id);
        existingBid.setBidderName(updatedBid.getBidderName());
        existingBid.setAmount(updatedBid.getAmount());
        return dummyBidRepository.save(existingBid);
    }

    public void deleteBid(Long id) {
        DummyBid existingBid = getBidById(id);
        dummyBidRepository.delete(existingBid);
    }
}
