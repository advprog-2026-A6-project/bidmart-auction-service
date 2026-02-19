package id.ac.ui.cs.advprog.auction.dummy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DummyBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bidderName;

    @Column(nullable = false)
    private Integer amount;

    protected DummyBid() {
        // Required by JPA.
    }

    public DummyBid(String bidderName, Integer amount) {
        this.bidderName = bidderName;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getBidderName() {
        return bidderName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
