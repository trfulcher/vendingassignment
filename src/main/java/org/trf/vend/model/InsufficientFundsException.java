package org.trf.vend.model;

public class InsufficientFundsException extends Exception {

	public InsufficientFundsException(String message) {
        super( message );
	}
    
}
