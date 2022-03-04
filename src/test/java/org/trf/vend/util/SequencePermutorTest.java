package org.trf.vend.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class SequencePermutorTest {

    private List<List<Integer>> resultSet;

    @Test
    public void exampleSequences(){

        List<Integer> input = new ArrayList<>();

        resultSet = SequencePermutor.generatePermutations( input );
        assertEquals(List.of(  ), resultSet);

        input.add(1);
        
        resultSet = SequencePermutor.generatePermutations( input );
        assertEquals(List.of( List.of(1) ), resultSet);

        input.add(2);

        resultSet = SequencePermutor.generatePermutations( input );
        assertEquals(List.of( List.of(1,2), List.of(2,1) ), resultSet);

        input.add(3);

        resultSet = SequencePermutor.generatePermutations( input );
        assertEquals(List.of( 
            List.of(1,2,3),
            List.of(1,3,2),
            List.of(2,1,3),
            List.of(2,3,1),
            List.of(3,2,1),
            List.of(3,1,2)
             ), resultSet);

    }

}