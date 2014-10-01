package Pooling;


import Pooling.ReadsPair;
import java.util.Comparator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class ReadsPairsComp implements Comparator<ReadsPair> {

    @Override
    public int compare(ReadsPair p1, ReadsPair p2) {
        if (p1.dist - p2.dist > 0)
            return 1;
        else
            return -1;
    }
    
}
