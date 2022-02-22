package org.trf.vend.model;

import java.util.*;
import java.util.stream.Collectors;

public enum Denominations {

    C200(200), C100(100), C50(50), C20(20), C10(10), C5(5), C2(2), C1(1);

    private int coinvalue;

    Denominations(int c) {
        this.coinvalue = c;
    }

    public int getCoinvalue() {
        return this.coinvalue;
    }

    private static final Map<String, Integer> coins;

    static {
        coins = EnumSet.allOf(Denominations.class).stream().collect(Collectors.toUnmodifiableMap(
                Enum::name, Denominations::getCoinvalue
        ));
    }

    public static Set<Integer> cointypes() {
        return Collections.unmodifiableSet(new HashSet<>(coins.values()));
    }

    public static boolean isValid(String coinname) {
        return coins.containsKey(coinname);
    }


}