package id.ac.ui.cs.advprog.auction.module.auction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuctionPageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/auctions-test";
    }

    @GetMapping("/auctions-test")
    public String auctionsTestPage() {
        return "auctions-test";
    }
}
