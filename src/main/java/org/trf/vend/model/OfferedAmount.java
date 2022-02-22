package org.trf.vend.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OfferedAmount implements IOfferedAmount {

    public static IOfferedAmount newInstance() {
        return new OfferedAmount();
    }

    OfferedAmount() {
    }

    private final Map<Integer, Integer> coins = new HashMap<>();

    private static Logger log = LoggerFactory.getLogger(OfferedAmount.class);

    @Override
    public void addCoin(Integer coinValue) throws UnrecognisedDenominationException {
        if (Denominations.cointypes().contains(coinValue)) {
            log.info("adding coin of value" + coinValue);
            coins.merge(coinValue, 1, Integer::sum);
        } else {
            log.error("unrecognised coin value:" + coinValue);
            throw new UnrecognisedDenominationException("Coin of value:" + coinValue + " unsupported");
        }
    }

    @Override
    public void addDenomination(String denomination) throws UnrecognisedDenominationException {
        try {
            log.info("add coin of denomination:" + denomination);
            this.addCoin(Denominations.valueOf(denomination).getCoinvalue());
        } catch (IllegalArgumentException iae) {
            throw new UnrecognisedDenominationException("Coin of name:" + denomination + " unsupported");
        }
    }

    @Override
    public boolean sufficientFunds(int amount) {
        return this.currentTotal() >= amount;
    }

    @Override
    public boolean exactChange(int amount) {
        return this.currentTotal() == amount;
    }

    @Override
    public int currentTotal() {
        return coins.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
    }

    @Override
    @JsonGetter
    public Map<Integer, Integer> getCoins() {
        return Collections.unmodifiableMap(coins);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.coins.forEach((k, v) -> sb.append("c:" + k + ", q:" + v + "\n"));
        sb.append(" tot:");
        sb.append(currentTotal());
        return sb.toString();

    }

}
