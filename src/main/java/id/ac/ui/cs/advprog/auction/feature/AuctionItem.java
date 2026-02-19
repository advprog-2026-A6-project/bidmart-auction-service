package id.ac.ui.cs.advprog.auction.feature;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer startingPrice;

    protected AuctionItem() {
        // Required by JPA.
    }

    public AuctionItem(String name, Integer startingPrice) {
        this.name = name;
        this.startingPrice = startingPrice;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartingPrice(Integer startingPrice) {
        this.startingPrice = startingPrice;
    }
}
