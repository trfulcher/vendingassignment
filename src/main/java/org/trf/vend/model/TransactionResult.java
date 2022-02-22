package org.trf.vend.model;

import java.util.HashMap;
import java.util.Map;

public class TransactionResult {

    private boolean success = false;
    private String outcome = "";
    private Map<Integer, Integer> change = new HashMap<>(); // empty by default

    TransactionResult() {
    }

    public TransactionResult(String message) {
        this.setOutcome(message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Map<Integer, Integer> getChange() {
        return change;
    }

    public void setChange(Map<Integer, Integer> change) {
        this.change = change;
    }

    public int changeTotal() {
        return this.change.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
    }
}
