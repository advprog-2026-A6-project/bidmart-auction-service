package id.ac.ui.cs.advprog.auction.dummy;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DummyBidController {

    private final DummyBidService dummyBidService;
    private final String frontendStagingUrl;
    private final String backendStagingUrl;
    private final String databaseStagingUrl;

    public DummyBidController(
            DummyBidService dummyBidService,
            @Value("${staging.links.frontend-url:not-set}") String frontendStagingUrl,
            @Value("${staging.links.backend-url:not-set}") String backendStagingUrl,
            @Value("${staging.links.database-url:not-set}") String databaseStagingUrl) {
        this.dummyBidService = dummyBidService;
        this.frontendStagingUrl = frontendStagingUrl;
        this.backendStagingUrl = backendStagingUrl;
        this.databaseStagingUrl = databaseStagingUrl;
    }

    @GetMapping("/api/dummy-bids")
    @ResponseBody
    public List<DummyBid> listDummyBids() {
        return dummyBidService.getAllBids();
    }

    @GetMapping("/api/dummy-bids/{id}")
    @ResponseBody
    public DummyBid getDummyBidById(@PathVariable Long id) {
        return dummyBidService.getBidById(id);
    }

    @PostMapping("/api/dummy-bids")
    @ResponseBody
    public DummyBid createDummyBid(@RequestBody DummyBid bid) {
        return dummyBidService.createBid(bid);
    }

    @PutMapping("/api/dummy-bids/{id}")
    @ResponseBody
    public DummyBid updateDummyBid(@PathVariable Long id, @RequestBody DummyBid bid) {
        return dummyBidService.updateBid(id, bid);
    }

    @DeleteMapping("/api/dummy-bids/{id}")
    @ResponseBody
    public void deleteDummyBid(@PathVariable Long id) {
        dummyBidService.deleteBid(id);
    }

    @GetMapping("/api/staging-links")
    @ResponseBody
    public Map<String, String> stagingLinks() {
        return Map.of(
                "frontend", frontendStagingUrl,
                "backend", backendStagingUrl,
                "database", databaseStagingUrl);
    }

    @GetMapping("/dummy-bids")
    public String dummyBidPage(Model model) {
        model.addAttribute("bids", dummyBidService.getAllBids());
        model.addAttribute("frontendStagingUrl", frontendStagingUrl);
        model.addAttribute("backendStagingUrl", backendStagingUrl);
        model.addAttribute("databaseStagingUrl", databaseStagingUrl);
        return "dummy-bids";
    }
}
