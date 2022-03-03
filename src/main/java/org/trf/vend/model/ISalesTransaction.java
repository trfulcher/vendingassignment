package org.trf.vend.model;

public interface ISalesTransaction {

    /**
     * Was used in early tests, should be deprecated from interface
     * @param b
     * @param oa
     * @return TransactionResult encapsulates the success of the transaction, including possible change returned
     * @throws InsufficientFundsException
     */
    TransactionResult execute(Basket b, IOfferedAmount oa) throws InsufficientFundsException;

    /**
     * Given the target sale price and a holder of all coins supplied (the IOfferedAmount) attempt to determine and return a result
     * Possible outcomes -
     * InsufficientFundsException thrown if offered amount less that basket amount
     * result success and no change if exact amount supplied
     * result success and a set of change in TransactionResult when IOfferedAmount greater than sale price
     * result fail and all of IOfferedAmount in change if we can't offer suitable change
     * @param basketamount
     * @param oa
     * @return TransactionResult encapsulates the success of the transaction, including possible change returned
     * @throws InsufficientFundsException
     */
    TransactionResult execute(int basketamount, IOfferedAmount oa) throws InsufficientFundsException;
}
