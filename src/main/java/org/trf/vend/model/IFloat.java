package org.trf.vend.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;
import java.util.Map;

public interface IFloat {

    /**
     * Perform an intialisation of the float contents.
     * This is destructive to any current state.
     * Arguably this might extract to a Float admin interface
     * @param initialfloat a map of coin value (key) number of coins (value)
     * @throws UnrecognisedDenominationException should any entry be an unsupported value from Denominations
     */
    void setBalance(Map<Integer, Integer> initialfloat) throws UnrecognisedDenominationException;

    /**
     *
     * @return the monetary amount of all coins in the float in pence
     */
    int totalHeld();

    /**
     * The supplied map has keys as coin value, values being the number of coins
     * of that value. These coins are merged into the internal store of coins
     * @param payment
     */
    void deposit(Map<Integer, Integer> payment);

    /**
     * Attempts to remove each of the coins in the argument from the float.
     * This should not fail (i.e. the coins are expected to be present )
     * @param coins
     */
    void withdraw(List<Integer> coins);

    /**
     *
     * @param changerequired the monetary amount in pence needed for change
     * @return a map of coin values:coin count which can possibly make the change. Coin
     * entries are not returned if the kitty has no coins of the value, or the
     * coin value is greater than the amount needed
     */
    Map<Integer, Integer> availableCoinMapForChange(int changerequired);

    /**
     *
     * @param changerequired the monetary amount in pence needed for change
     * @return A list of coin values which can possibly make the change. Coin
     * values are not returned if the kitty has no coins of the value, or the
     * coin value is greater than the amount needed
     */
    List<Integer> availableDenominations(int changerequired);

    @JsonGetter
    Map<Integer, Integer> status();
}
