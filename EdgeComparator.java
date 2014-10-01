package Pooling;


import Pooling.Edge;
import java.util.Comparator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class EdgeComparator implements Comparator{
    public int compare(Object o1, Object o2) {
        Edge e1 = (Edge) o1;
        Edge e2 = (Edge) o2;
        return (int) ((int) 10*(-e1.weight + e2.weight));
    }
    
}
