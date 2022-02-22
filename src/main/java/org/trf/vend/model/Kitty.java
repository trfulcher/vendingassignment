package org.trf.vend.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Kitty implements IFloat {

    private static Logger log = LoggerFactory.getLogger(Kitty.class);

    private SortedMap<Integer,Integer> balance = new TreeMap<>();

    public Kitty( SortedMap<Integer,Integer> initialfloat ) throws UnrecognisedDenominationException {

        this.setBalance( initialfloat );
    }
    
    public Kitty() {

        for( int c: Denominations.cointypes() ){
            balance.put(c, 0);
        }

    }

    @Override
    public void setBalance(Map<Integer, Integer> initialfloat) throws UnrecognisedDenominationException {
        if( ! Denominations.cointypes().containsAll( initialfloat.keySet()) ){
            log.error("bad coins setting balance");
            throw new UnrecognisedDenominationException("can't recognise some coins upon init");
        }

        TreeMap<Integer,Integer> sortedfloat =
                Denominations.cointypes().
                        stream().filter(initialfloat::containsKey).map(i->  new AbstractMap.SimpleEntry<Integer,Integer>(i, initialfloat.get(i) ) ).
                        collect(Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1,o2) -> o1, TreeMap::new ));
        log.info("set balance to:" + sortedfloat);
        balance = sortedfloat;
    }

    @Override
    public int totalHeld(){

        return balance.entrySet().stream().mapToInt(e-> e.getKey() * e.getValue() ).sum();
       
    }

   @Override
   public void deposit(Map<Integer, Integer> payment){
        log.info("deposit of:" + payment );
        payment.entrySet().forEach(e -> balance.merge(e.getKey(), e.getValue(), Integer::sum) );
       
   }

   @Override
   public void withdraw(List<Integer> coins){
        log.info("withdraw of:" + coins );
    coins.forEach( c -> this.balance.merge(c, -1, Integer::sum) );
   }

   @Override
   public Map<Integer, Integer> availableCoinMapForChange(int changerequired){

       log.info("available coins for:" + changerequired);
    return this.balance.headMap(changerequired + 1).entrySet()
    .stream()
    .filter( e -> e.getValue() > 0 )
    .collect( Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue ));


}

   @Override
   public List<Integer> availableDenominations(int changerequired){


    List<Integer> c = this.balance.headMap(changerequired +1 ).entrySet()
    .stream()
    .filter( e -> e.getValue() > 0 )
    .map(Map.Entry::getKey )
    .collect( Collectors.toList());
    Collections.reverse(c);
    log.info("available denominations:"+ c );
    return c;

}
    @Override
    @JsonGetter
    public Map<Integer, Integer> status(){
        return  Collections.unmodifiableMap( this.balance );
    }

   public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append( this.availableCoinMapForChange( this.totalHeld() +1) );
    sb.append( " tot:" + this.totalHeld());
    return sb.toString();
    }
}


