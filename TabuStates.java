package Pooling;


import Pooling.TabuState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pavel
 */
public class TabuStates {
    ArrayList<TabuState> states;
    TabuStates()
    {
        states = new ArrayList();
    }
    void addState(TabuState ts)
    {
        states.add(ts);
    }
    boolean containState(TabuState ts)
    {
        boolean conts = false;
        for (TabuState a : states)
            if (a.isEqual(ts))
                return true;
        return false;
    }
    void printToConsole()
    {
        System.out.println("Tabu states");
        for (TabuState ts : states)
        {
            System.out.print("Part: ");
            for (int i : ts.part)
                System.out.print(i + " ");
            System.out.print("\n");
            System.out.print("Tabu: ");
            for (int i : ts.tabu)
                System.out.print(i + " ");
            System.out.print("\n");
            
        }
    }
}
