package org.trf.vend.model;

public interface ISalesTransaction {

    /**
     *
     * @param b
     * @param oa
     * @return
     * @throws InsufficientFundsException
     */
    TransactionResult execute(Basket b, IOfferedAmount oa) throws InsufficientFundsException;

    /**
     *
     * @param basketamount
     * @param oa
     * @return
     * @throws InsufficientFundsException
     */
    TransactionResult execute(int basketamount, IOfferedAmount oa) throws InsufficientFundsException;
}
