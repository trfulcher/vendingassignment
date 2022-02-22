package org.trf.vend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.trf.vend.model.Basket;

public class TestBasket {

    Basket b;

    @BeforeEach
    public void setup(){
        b = new Basket();
    }

    @Test
    public void checkBasketTotal(){

        b.addItem( "AAA", 1 );
        b.addItem( "CCC", 2 );

        assertEquals(3, b.itemcount());
        assertEquals( 13+99+99, b.totalCost());
    }
    
}
