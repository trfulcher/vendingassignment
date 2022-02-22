package org.trf.vend.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trf.vend.util.SequencePermutor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SalesTransactionService implements ISalesTransaction {

    private static Logger log = LoggerFactory.getLogger(SalesTransactionService.class);

    private final IFloat kitty;


    public SalesTransactionService() {
        kitty = new Kitty();
    }

    public SalesTransactionService(IFloat initialkitty) {
        kitty = initialkitty;
    }

    @Override
    public TransactionResult execute(Basket b, IOfferedAmount oa) throws InsufficientFundsException {

        TransactionResult r = this.execute(b.totalCost(), oa);
        if (r.isSuccess()) {
            b.vend();
        }
        return r;

    }

    @Override
    public TransactionResult execute(int basketamount, IOfferedAmount oa) throws InsufficientFundsException {

        TransactionResult result = new TransactionResult();

        if (!oa.sufficientFunds(basketamount)) {
            result.setSuccess(false);
            result.setOutcome("Please add more funds ");
            throw new InsufficientFundsException("please add more funds ");
        } else if (oa.exactChange(basketamount)) {
            // perfect txn
            result.setSuccess(true);
            result.setOutcome("Thanks for your custom");
            kitty.deposit(oa.getCoins());
        } else {
            result = calculateChangeStrategy(basketamount, oa);
        }
        return result;

    }


    private List<Integer> generateVariant(List<Integer> v, Map<Integer, Integer> base) {

        return v.stream().map(i -> Collections.nCopies(base.get(i), i))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private TransactionResult calculateChangeStrategy(int basketamount, IOfferedAmount oa) {

        TransactionResult r = new TransactionResult();

        // change needed
        int changerequired = oa.currentTotal() - basketamount;
        log.info("needs change of:" + changerequired);

        boolean decision = false;

        List<List<Integer>> lop = SequencePermutor.generatePermutations(kitty.availableDenominations(changerequired));
        Map<Integer, Integer> prospect = kitty.availableCoinMapForChange(changerequired);

        for (List<Integer> l : lop) {

            List<Integer> variant = this.generateVariant(l, prospect);
            decision = attemptVariantSolution(oa, r, changerequired, variant);

            if (decision) {
                break;
            }

        }

        if (!decision) {
            r.setOutcome("Sale impossible with current funds in machine");
            r.setSuccess(decision);
            // worst case - cancel the sale
            r.setChange(oa.getCoins());
        }
        return r;
    }

    private boolean attemptVariantSolution(IOfferedAmount oa, TransactionResult r, int changerequired, List<Integer> variant) {
        Map<Integer, Integer> coinMap;
        int variantchange = changerequired;
        boolean decision = false;

        log.debug("variant:" + variant);

        AtomicInteger changeNeeded = new AtomicInteger(variantchange);

        List<Integer> changeFound =
                variant.stream().filter(v -> v <= changeNeeded.get())
                        .peek(v -> {
                            changeNeeded.getAndAdd(-v);
                            log.info(v + " need:" + changeNeeded.get() + ", ");
                        })
                        .takeWhile(v -> changeNeeded.get() >= 0).collect(Collectors.toList());

        if (changeNeeded.get() == 0) {
            log.info("cf:" + changeFound);
            coinMap = changeFound.stream().collect(Collectors.toMap(i -> i, i -> 1, Integer::sum));
            decision = true;
            r.setSuccess(decision);
            r.setChange(coinMap);
            r.setOutcome("sale with change");

            kitty.deposit(oa.getCoins());
            kitty.withdraw(changeFound);

        } else {
           log.debug("change needed:" + changeNeeded.get());
        }
        return decision;
    }

}
