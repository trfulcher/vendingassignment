package org.trf.vend.vendapi;


import org.trf.vend.model.IOfferedAmount;
import org.trf.vend.model.OfferedAmount;

public class SalesHolder {

    public SalesHolder() {
        this.oa = OfferedAmount.newInstance();
    }

    public void reset() {
        this.oa = OfferedAmount.newInstance();
    }

    public IOfferedAmount getOa() {
        return oa;
    }

    private IOfferedAmount oa;
    

}

