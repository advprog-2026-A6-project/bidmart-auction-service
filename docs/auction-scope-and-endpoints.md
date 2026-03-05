# Auction Scope & Planned Endpoints

## Scope for this module
- Auction lifecycle initialization from `DRAFT` to `ACTIVE`
- Basic endpoint to accept bids
- Validation for bid amount and allowed auction status

## Planned endpoints
- `POST /api/auctions` create auction draft
- `POST /api/auctions/{id}/activate` activate draft auction
- `POST /api/auctions/{id}/bids` place bid on active auction
- `GET /api/auctions/{id}` view auction detail
- `GET /api/auctions/{id}/bids` view bid history

## Notes
- Database configuration already exists in `application.yml` and supports local H2 by default.
- Legacy item endpoints under `/api/items` are deprecated. New auction lifecycle and bidding development should use `/api/auctions`.
