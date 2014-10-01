package Pooling;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

/**
 *
 * @author kki8
 */
public class Cut {
    ArrayList<Vertex> part1;
    ArrayList<Vertex> part2;
    Cut(ArrayList<Vertex> p1, ArrayList<Vertex> p2)
    {
        part1 = p1;
        part2 = p2;
    }
    Cut (Cut c)
    {
        this.part1 = new ArrayList(c.part1);
        this.part2 = new ArrayList(c.part2);
    }
     Cut(ArrayList<Vertex> p1, ArrayList<Vertex> p2, int i)
    {
        part1 = new ArrayList(p1);
        part2 = new ArrayList(p2);
    }
    void printToConsole()
    {
        System.out.println("Cut");
        
        System.out.println("Part1");
        if (part1.size() == 0)
            System.out.println("Empty");
        else
            for (Vertex v : part1)
                System.out.print(v.index + " ");
        
        System.out.println();
        
        System.out.println("Part2");
        if (part2.size() == 0)
            System.out.println("Empty");
        else
            for (Vertex v : part2)
                System.out.print(v.index + " ");
        
        System.out.println();
        
    }
    
    
}
