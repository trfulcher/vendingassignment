package org.trf.vend.model;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestKitty {

    @Test
    public void valueOfEmptyKittyIsZero(){

        IFloat k = new Kitty();
        assertEquals(0, k.totalHeld());
    }

    @Test
    public void randomKittyTotalsCorrect(){

        TreeMap<Integer,Integer> rk = 
         Denominations.cointypes().
            stream().map( i-> new AbstractMap.SimpleEntry<Integer,Integer>(i, 1 )).
            collect(Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1,o2) -> o1, TreeMap::new ));

        IFloat k = null;
        try {
            k = new Kitty( rk );
            assertEquals( Denominations.cointypes().stream().mapToInt(i-> i).sum()   , k.totalHeld());
            System.out.println("kity holds "+ k.totalHeld());
        } catch (UnrecognisedDenominationException e) {
            fail();
        }


    }

    @Test
    public void testDeposit(){
        IFloat k = new Kitty();

        Map<Integer,Integer> dep = new HashMap<>();
        dep.put(5, 2);
        dep.put(1, 2);
        k.deposit(dep);

        assertEquals(12, k.totalHeld());
        assertEquals(2, k.availableCoinMapForChange(100).size());
    }

    @Test
    public void testAvailableDenominationsForChange(){
        IFloat k = new Kitty();

        Map<Integer,Integer> dep = new HashMap<>();
        dep.put(5, 2);
        dep.put(1, 2);
        dep.put(100, 1);
        k.deposit(dep);

        assertEquals( List.of( 100,5,1), k.availableDenominations(120)  );
        assertEquals( List.of( 5,1), k.availableDenominations(21 )  );

    }

    @Test
    public void testWitdraw(){
        IFloat k = new Kitty();

        Map<Integer,Integer> dep = new HashMap<>();
        dep.put(5, 2);
        dep.put(1, 2);
        k.deposit(dep);

        List<Integer> w = List.of(5, 1);
        k.withdraw(w);

        assertEquals(6, k.totalHeld());
        assertEquals( 2, k.availableDenominations(100).size() );

    }
    
}
