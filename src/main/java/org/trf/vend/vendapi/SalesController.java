package org.trf.vend.vendapi;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trf.vend.model.*;

@RestController
public class SalesController {

    ISalesTransaction sts;

    SalesHolder holder;

    public SalesController(ISalesTransaction sts, SalesHolder holder) {
        this.holder = holder;
        this.sts = sts;
    }

    @RequestMapping(path = "/makesale/{baskettotal}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResult> performSale(@PathVariable Integer baskettotal) {

        try {
            TransactionResult r = this.sts.execute(baskettotal, holder.getOa());
            // sts to reset for next sale on success/fail
            // TODO move this responsibility into Sales service
            this.holder.reset();

            if (r.isSuccess()) {
                return ResponseEntity.ok().body(r);
            } else {
                return ResponseEntity.internalServerError().body(r);
            }

        } catch (InsufficientFundsException e) {
            // default fail with no change, but doesn't destroy the holder
            TransactionResult r = new TransactionResult(e.getMessage());
            return ResponseEntity.badRequest().body(r);
        }

    }

    @RequestMapping(path = "/addFunds/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addFunds(@RequestParam String coin) {

        try {
            holder.getOa().addDenomination(coin);
            return ResponseEntity.ok().body(holder.getOa());
        } catch (UnrecognisedDenominationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
