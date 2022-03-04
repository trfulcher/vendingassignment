package org.trf.vend.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.trf.vend.util.SequencePermutor;

public class TestSalesTransaction {

    ISalesTransaction st;
    Basket b;
    IFloat k;

    @BeforeEach
    public void setup() {

        b = new Basket(); // empty

        // one of each coin
        TreeMap<Integer, Integer> initialCoins = Denominations.cointypes().stream().map(i -> new AbstractMap.SimpleEntry<Integer, Integer>(i, 1)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1,
                TreeMap::new));

        try {
            k = new Kitty(initialCoins);
            st = new SalesTransactionService( k );

        } catch (UnrecognisedDenominationException e) {
            fail();
        }
    }

    @Test
    public void insufficientCash() {
        b.addItem("AAA", 1); // 13p
        IOfferedAmount oa = new OfferedAmount();
        try {
            oa.addCoin(10);
            assertThrows(InsufficientFundsException.class, () -> st.execute(b.totalCost(), oa));
        } catch (UnrecognisedDenominationException e) {
            fail();
        }


    }

    @Test
    public void noChangeNeeded() {

        try {
            b.addItem("AAA", 1); //
            IOfferedAmount oa = new OfferedAmount();
            oa.addCoin(10);
            oa.addCoin(2);
            oa.addCoin(1);
            TransactionResult r = st.execute(b.totalCost(), oa);
            assertTrue(r.getChange().values().isEmpty());
            assertTrue(r.isSuccess());
        } catch (InsufficientFundsException | UnrecognisedDenominationException e) {
            fail();
        }

    }

    @Test
    public void changeNeeded() {


        try {
            b.addItem("DDD", 1); //
            IOfferedAmount oa = new OfferedAmount();
            oa.addCoin(100);
            oa.addCoin(50);
            TransactionResult r = st.execute(b.totalCost(), oa);
            assertFalse(r.getChange().values().isEmpty());
            assertTrue( r.getChange().containsKey( 20 ) );
            assertEquals( 1, r.getChange().get(20) );
            assertTrue( r.getChange().containsKey( 10 ) );
            assertEquals( 1, r.getChange().get(10) );
            assertTrue( r.getChange().containsKey( 5 ) );
            assertEquals( 1, r.getChange().get(5) );
            assertTrue( r.isSuccess() );
        } catch (InsufficientFundsException | UnrecognisedDenominationException e) {
            fail();
        }

    }

    @Test
    public void multiVariantOfCoins() {


        try {

            b.addItem("EEE", 1); // 25p
            IOfferedAmount oa = new OfferedAmount();
            oa.addCoin(20);
            oa.addCoin(20);
            oa.addCoin(1);
            // 16 p required which will be fooled by the 5p in kitty variant 1

            // adjust kitty
            Map<Integer,Integer> p = Map.of( 2,2 );
            k.deposit(p);
            k.withdraw( List.of(1));

            System.out.println( k.toString() );
            TransactionResult r = st.execute(b.totalCost(), oa);
            assertFalse(r.getChange().values().isEmpty());
            assertTrue( r.getChange().containsKey( 10 ) );
            assertEquals( 1, r.getChange().get(10) );
            assertTrue( r.getChange().containsKey( 2 ) );
            assertEquals( 3, r.getChange().get(2) );
            assertTrue( r.isSuccess() );
        } catch (InsufficientFundsException | UnrecognisedDenominationException e) {
            fail();
        }

    }

    @Test
    public void testStatefulService(){

        List<Integer> one = new ArrayList<>();
        one.add(1);
        System.out.println( "1:::" + SequencePermutor.generatePermutations( one )  );

        try {
            System.out.println( "1)" + k.toString() );

            int ktot = k.totalHeld();

            IOfferedAmount oa = new OfferedAmount();
            oa.addCoin(20);

            b.addItem("AAA", 1);
            // 7p change needed, will wipe out the C5 & C2
            TransactionResult r = st.execute(b, oa);
            assertTrue( r.isSuccess() );
            assertEquals(  Map.of( 5,1,2,1), r.getChange()  );
            assertEquals( ktot + ExampleProductCatalogue.availableProducts().get("AAA")  ,    k.totalHeld() );
            ktot += ExampleProductCatalogue.availableProducts().get("AAA");

            System.out.println( "2)" + k.toString() );
            oa = new OfferedAmount();
            oa.addCoin(100);
            b = new Basket();
            b.addItem("CCC", 1);

            // 1p change needed, will wipe out the C1
            r = st.execute(b, oa);
            assertTrue( r.isSuccess() );
            assertEquals(  Map.of(1,1), r.getChange()  );
            assertEquals( ktot + ExampleProductCatalogue.availableProducts().get("CCC")  ,    k.totalHeld() );

            ktot += ExampleProductCatalogue.availableProducts().get("CCC");

            System.out.println( "3)"+ k.toString() );
            oa = new OfferedAmount();
            oa.addCoin(20);
            oa.addCoin( 20 );
            b = new Basket();
            b.addItem("FFF", 1);
            // 3p change can't be honoured
            r = st.execute(b, oa);
            assertFalse( r.isSuccess() );
            assertEquals( r.getChange().size(), oa.getCoins().size() );


        } catch (UnrecognisedDenominationException | InsufficientFundsException e) {
            fail();
        }

    }

    @RepeatedTest( 20 )
    public void changePermutations() {

        IOfferedAmount oa = new OfferedAmount();
        try {
            Random random = new Random();
            ExampleProductCatalogue.availableProducts().keySet().forEach(k -> {
                b.addItem(k, random.nextInt(3) );
            });


            Map<Integer,Integer> topup = new HashMap<>();
            Denominations.cointypes().forEach(c-> {
                for( int i = 0; i < random.nextInt(4); i++ ){
                    try {
                        oa.addCoin(c);
                    } catch (UnrecognisedDenominationException e) {
                        fail();
                    }
                    if( oa.sufficientFunds(b.totalCost())){
                        break;
                    }
                }
                topup.put(c, random.nextInt(3));

            });
            k.deposit(topup);
            System.out.println( "basket:"  + b );
            System.out.println( "k:"  + k );
            System.out.println( "oa:"  + oa );

            TransactionResult r = st.execute(b.totalCost(), oa);

            if( b.totalCost() == oa.currentTotal() ){
                assertTrue( r.isSuccess() );
                assertTrue(r.getChange().values().isEmpty());
                assertEquals(0, r.changeTotal() );
            }
            else if( r.isSuccess() ){
                assertFalse(r.getChange().values().isEmpty());
            }
            else {
                // sale cancelled
                assertEquals( r.getChange().size(), oa.getCoins().size() );
                assertEquals( r.changeTotal(), oa.currentTotal() );
            }
        } catch (InsufficientFundsException e) {
            if(  oa.sufficientFunds( b.totalCost() ) ){
                fail();
            }
        }

    }

    @Test
    public void streamOfCoins() {
        Map<Integer, Integer> payment = new HashMap<>();
        payment.put(5, 1);
        payment.put(2, 3);
        k.withdraw( List.of( 200,100,50,20, 10,1  ));
        k.deposit(payment);
        int tgt = 18;
        assertEquals( tgt, k.totalHeld() );
    }

}
