package org.trf.vend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestOfferedAmount {

    @Test
    public void exampleVend(){

        IOfferedAmount oa = new OfferedAmount();

        int basketamount = 150;

        try {
            oa.addCoin( 2 );
            assertFalse( oa.sufficientFunds(basketamount));
            oa.addCoin(100);
            assertFalse( oa.sufficientFunds(basketamount));
            assertEquals(102, oa.currentTotal());
            oa.addCoin(20);
            oa.addCoin(20);
            assertFalse( oa.sufficientFunds(basketamount));
            assertEquals(142, oa.currentTotal());
            oa.addCoin(5);
            oa.addCoin(2);
            oa.addCoin(1);
            assertTrue( oa.sufficientFunds(basketamount));
            assertEquals( basketamount, oa.currentTotal());
            assertTrue( oa.exactChange(basketamount));
            oa.addCoin(1);
            assertTrue( oa.sufficientFunds(basketamount));
            assertFalse( oa.exactChange(basketamount));
        } catch (UnrecognisedDenominationException e) {
            fail();
        }
    }

    @Test
    public void exampleVendWithDenominations(){

        IOfferedAmount oa = new OfferedAmount();

        int basketamount = 385;

        try {
            oa.addDenomination( "C200" );
            assertFalse( oa.sufficientFunds(basketamount));
            oa.addDenomination("C100");
            oa.addDenomination("C50");
            oa.addDenomination("C20");
            oa.addDenomination("C10");
            assertEquals(380, oa.currentTotal());
            assertFalse( oa.sufficientFunds(basketamount));
            oa.addDenomination("C5");
            assertTrue( oa.sufficientFunds(basketamount));
            assertEquals( basketamount, oa.currentTotal());
            assertTrue( oa.exactChange(basketamount));

            // over pay
            oa.addDenomination("C2");
            oa.addDenomination("C1");
            assertTrue( oa.sufficientFunds(basketamount));
            assertFalse( oa.exactChange(basketamount));
        } catch (UnrecognisedDenominationException e) {
            fail();
        }
    }

    @Test
    public void foreignCurrency(){

        IOfferedAmount oa = new OfferedAmount();
        assertThrows(UnrecognisedDenominationException.class, () -> oa.addDenomination("EUR10"));
        assertThrows(UnrecognisedDenominationException.class, () -> oa.addCoin(15));
    }
    
}
