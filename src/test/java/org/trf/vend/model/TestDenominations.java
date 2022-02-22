package org.trf.vend.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class TestDenominations {

    @Test
    public void testInit(){
        assertEquals( Denominations.cointypes().size(), Denominations.values().length );
        assertFalse( Denominations.cointypes().contains(7));
    }

    @Test
    public void testImmutable(){
        assertThrows( UnsupportedOperationException.class,  () -> Denominations.cointypes().add(9) );
    }

    @Test
    public void testBadCoins(){

        assertTrue(  Denominations.isValid("C50"));
        assertFalse(  Denominations.isValid("EUR10"));
        Set<String> somecoins = Set.of("EUR10", "C10");

        assertEquals( 1, somecoins.stream().filter(Denominations::isValid).map(Denominations::valueOf).count());
        assertEquals( 1, somecoins.stream().filter(Denominations::isValid).map(Denominations::valueOf).filter( d->  d == Denominations.C10 ).count() );

    }
}
