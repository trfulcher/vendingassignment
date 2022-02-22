package org.trf.vend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.swap;


public class SequencePermutor {

    private static final Map<List<Integer>, List<List<Integer>>> cache = new HashMap<>();

    private SequencePermutor() {
    }

    /**
     * OK, I did lift this snippet from online.
     * <p>
     * Ultimately this implementation is likely overkill for the vending problem, because many of the variants will
     * generate the same outcome. As such this method would be a primary point for optimization. Typically the penalty is
     * incurred when there is no possible change combination and all variants are tried
     *
     * @param sequence input list of integers
     * @param results  the holder for the event list of permutations
     * @param index    necessary to handle recursion
     */
    private static void permutationsInternal(List<Integer> sequence, List<List<Integer>> results, int index) {
        if (index == sequence.size() - 1) {
            results.add(new ArrayList<>(sequence));
        }

        for (int i = index; i < sequence.size(); i++) {
            swap(sequence, i, index);
            permutationsInternal(sequence, results, index + 1);
            swap(sequence, i, index);
        }
    }


    /**
     * Basic generator of permutations of a supplied sequence.
     *
     * @param sequence Expects a list of integers (e.g. coin denominations). No checks on value validity are made
     * @return a list of all permutations of the supplied sequence. First item will be the supplied input.
     */
    public static List<List<Integer>> generatePermutations(List<Integer> sequence) {

        if (cache.containsKey(sequence)) {
            return cache.get(sequence);
        } else {

            List<List<Integer>> permutations = new ArrayList<>();
            permutationsInternal(sequence, permutations, 0);
            cache.put(sequence, permutations);
            return permutations;
        }
    }

}
