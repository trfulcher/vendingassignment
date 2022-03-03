package org.trf.vend.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Originally used for testing, not core to the solution
 */
public class ExampleProductCatalogue {

    private static final Map<String,Integer> products = new HashMap<>();

    static {

        products.put("AAA", 13 );
        products.put("BBB", 65 );
        products.put("CCC", 99 );
        products.put("DDD", 115 );
        products.put("EEE", 25 );
        products.put("FFF", 37 );
    }

    public static Map<String,Integer> availableProducts( ){
        return Collections.unmodifiableMap(products);
    }

}
