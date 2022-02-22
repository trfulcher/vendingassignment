package org.trf.vend.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Map;

public interface IOfferedAmount {

    /**
     * Attempt to add to offer one coin of a particular value
     * @param coinValue
     * @throws UnrecognisedDenominationException if the value is not permissible via Denominations values
     */
    void addCoin(Integer coinValue) throws UnrecognisedDenominationException;

    /**
     * Attempt to add to offer one coin of a particular denomination
     * @param denomination which must match permissible values in Denominations enum
     * @throws UnrecognisedDenominationException if the value type not recognised
     */
    void addDenomination(String denomination) throws UnrecognisedDenominationException;

    /**
     * indicates if the offer is equal or more than the amount needed
     * @param amount
     * @return
     */
    boolean sufficientFunds(int amount);

    /**
     * indicates if the offer matches the exact amount needed
     * @param amount
     * @return
     */
    boolean exactChange(int amount);

    /**
     *
     * @return the current value of all coins in the offer in pence
     */
    int currentTotal();

    @JsonGetter
    Map<Integer, Integer> getCoins();

}
