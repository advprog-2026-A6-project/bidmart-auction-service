package id.ac.ui.cs.advprog.auction.module.auction.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuctionPageControllerTest {

    private final AuctionPageController controller = new AuctionPageController();

    @Test
    void homeShouldRedirectToAuctionsTestPage() {
        assertEquals("redirect:/auctions-test", controller.home());
    }

    @Test
    void auctionsTestPageShouldReturnTemplateName() {
        assertEquals("auctions-test", controller.auctionsTestPage());
    }
}
