ALTER TABLE auctions
    ADD COLUMN reserve_price NUMERIC(19,2);

ALTER TABLE auctions
    ADD COLUMN winning_bid NUMERIC(19,2);

ALTER TABLE auctions
    ADD COLUMN winner_bidder_name VARCHAR(255);
