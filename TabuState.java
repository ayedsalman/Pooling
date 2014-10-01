package Pooling;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class TabuState {
    ArrayList<Integer> part;
    ArrayList<Integer> tabu;
    TabuState()
    {
        part = new ArrayList();
        tabu = new ArrayList();
    }
    TabuState(ArrayList<Vertex> part, HashMap tabu)
    {
        this.part = new ArrayList();
        this.tabu = new ArrayList();
        for (Vertex v : part)
            this.part.add(v.index);
        Iterator ir = tabu.entrySet().iterator();
        while (ir.hasNext())
        {
            Map.Entry me = (Map.Entry) ir.next();
            this.tabu.add(((Vertex) me.getKey()).index);
        }
        Collections.sort(this.part);
        Collections.sort(this.tabu);
    }
    boolean isEqual(TabuState ts)
    {        
            if (this.part.size() != ts.part.size())
                return false;
            if (this.tabu.size() != ts.tabu.size())
                return false;
            for (int i = 0; i < this.part.size(); i++)
            {
                int x = this.part.get(i);
                int y = ts.part.get(i);
                if (x != y)
                    return false;
            }
            for (int i = 0; i < this.tabu.size(); i++)
            {
                int x = this.tabu.get(i);
                int y = ts.tabu.get(i);
//                if (this.tabu.get(i) != ts.tabu.get(i))
                if (x != y)
                    return false;
            }
            return true;           
    }
}
