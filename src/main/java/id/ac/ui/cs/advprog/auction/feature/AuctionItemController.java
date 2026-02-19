package id.ac.ui.cs.advprog.auction.feature;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public class AuctionItemController {

    private final AuctionItemService auctionItemService;
    private final String frontendStagingUrl;
    private final String backendStagingUrl;
    private final String databaseStagingUrl;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring manages singleton service lifecycle")
    public AuctionItemController(
            AuctionItemService auctionItemService,
            @Value("${staging.links.frontend-url:not-set}") String frontendStagingUrl,
            @Value("${staging.links.backend-url:not-set}") String backendStagingUrl,
            @Value("${staging.links.database-url:not-set}") String databaseStagingUrl) {
        this.auctionItemService = auctionItemService;
        this.frontendStagingUrl = frontendStagingUrl;
        this.backendStagingUrl = backendStagingUrl;
        this.databaseStagingUrl = databaseStagingUrl;
    }

    @GetMapping("/api/items")
    @ResponseBody
    public List<AuctionItem> listItems() {
        return auctionItemService.findAll();
    }

    @GetMapping("/api/items/{id}")
    @ResponseBody
    public AuctionItem getItem(@PathVariable Long id) {
        return auctionItemService.findById(id);
    }

    @PostMapping("/api/items")
    @ResponseBody
    public AuctionItem createItem(@RequestBody AuctionItem request) {
        return auctionItemService.create(request);
    }

    @PutMapping("/api/items/{id}")
    @ResponseBody
    public AuctionItem updateItem(@PathVariable Long id, @RequestBody AuctionItem request) {
        return auctionItemService.update(id, request);
    }

    @DeleteMapping("/api/items/{id}")
    @ResponseBody
    public void deleteItem(@PathVariable Long id) {
        auctionItemService.delete(id);
    }

    @GetMapping("/api/staging-links")
    @ResponseBody
    public Map<String, String> stagingLinks() {
        return Map.of(
                "frontend", frontendStagingUrl,
                "backend", backendStagingUrl,
                "database", databaseStagingUrl);
    }

    @GetMapping("/items")
    public String itemsPage(Model model) {
        model.addAttribute("items", auctionItemService.findAll());
        model.addAttribute("frontendStagingUrl", frontendStagingUrl);
        model.addAttribute("backendStagingUrl", backendStagingUrl);
        model.addAttribute("databaseStagingUrl", databaseStagingUrl);
        return "items";
    }
}
