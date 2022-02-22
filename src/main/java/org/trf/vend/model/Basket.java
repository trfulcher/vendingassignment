package org.trf.vend.model;

import java.util.HashMap;
import java.util.Map;

public class Basket {

    private Map<String,Integer> products = new HashMap<>();

    public static Basket newInstance() {
        return new Basket();
    }

    public void addItem( String name, int quantity ){

        if( ExampleProductCatalogue.availableProducts().keySet().contains(name)){
            products.merge(name, quantity, (q1,q2) -> q1 + q2 );
        }
        else{
            //throw unknown product
        }
    }

    public int totalCost(){

      return  products.entrySet().stream().mapToInt( e -> e.getValue() * ExampleProductCatalogue.availableProducts().get(e.getKey())).sum();

    }

    public int itemcount(){
        return products.values().stream().reduce(0, Integer::sum);
    }

    public void vend(){
        //System.out.println("SALE complete:");
        //this.products.forEach( (k,v) -> System.out.println("Item:" + k + ", quantity:" + v) );
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        this.products.forEach( (k,v) -> sb.append("Item:" + k + ", quantity:" + v +"\n") );
        sb.append( " tot:" + this.totalCost());
        return sb.toString();
    
    }
    
}
