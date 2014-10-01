package Pooling;


import Pooling.EdgeComparator;
import Pooling.Edge;
import Pooling.Cut;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class Graph {
    ArrayList<Vertex> vertices;
    HashMap<Vertex,HashSet<Edge>> adjListsEdges;
    ArrayList<Edge> edgeList;
    Graph(ArrayList<Vertex> vertices, HashMap<Vertex,HashSet<Edge>> adjListsEdges, ArrayList<Edge> edgeList)
    {
        this.vertices = vertices;
        this.adjListsEdges = adjListsEdges;
        this.edgeList = edgeList;
    }
    
    Cut findMaxCut()
    {
        // find opt vert at each iteration
        Collections.sort(edgeList, new EdgeComparator());
//        HashSet<Vertex> part1 = new HashSet();
//        HashSet<Vertex> part2 = new HashSet();
        ArrayList<Vertex> part1 = new ArrayList();
        ArrayList<Vertex> part2 = new ArrayList();
        HashSet<Vertex> unsorted = new HashSet(vertices);
        for (Edge e : edgeList)
        {
            if (part1.contains(e.u) && (unsorted.contains(e.v)))
            {
                part2.add(e.v);
                unsorted.remove(e.v);
                continue;
            }
            if (part1.contains(e.v) && (unsorted.contains(e.u)))
            {
                part2.add(e.u);
                unsorted.remove(e.u);
                continue;
            }
            if (part2.contains(e.u) && (unsorted.contains(e.v)))
            {
                part1.add(e.v);
                unsorted.remove(e.v);
                continue;
            }
            if (part2.contains(e.v) && (unsorted.contains(e.u)))
            {
                part1.add(e.u);
                unsorted.remove(e.u);
                continue;
            }
            if (unsorted.contains(e.u) && unsorted.contains(e.v))
            {
                part1.add(e.u);
                part2.add(e.v);
                unsorted.remove(e.v);
                unsorted.remove(e.u);
            }            
        }
        
        boolean toIter = true;
        int itr = 0;
        while (toIter)
        {
            itr++;
            System.out.println("Maxcut iteration " + itr);
            toIter = false;
            
            double record = 0;
            Vertex vert_record = null;
            for (Vertex v : part1)
            {
                double degIn = 0;
                double degOut = 0;
                for (Edge e : adjListsEdges.get(v))
                {
                    if (part1.contains(e.anotherEnd(v)))
                        degIn += e.weight;
                    else
                        degOut += e.weight;                                
                }
                if (degIn - degOut > record)
                {
                    record = degIn - degOut;
                    vert_record = v;
                    toIter = true;
                }
            }
            for (Vertex v : part2)
            {
                double degIn = 0;
                double degOut = 0;
                for (Edge e : adjListsEdges.get(v))
                {
                    if (part2.contains(e.anotherEnd(v)))
                        degIn += e.weight;
                    else
                        degOut += e.weight;                                
                }
                if (degIn - degOut > record)
                {
                    toIter = true;
                    record = degIn - degOut;
                    vert_record = v;
                }
            }
            if (record > 0)
            {
                if (part1.contains(vert_record))
                {
                    part1.remove(vert_record);
                    part2.add(vert_record);
                }
                else
                {
                    part2.remove(vert_record);
                    part1.add(vert_record);
                }
            }
        }
        
        ArrayList<Vertex> part1_ar = new ArrayList(part1.size());
        ArrayList<Vertex> part2_ar = new ArrayList(part2.size());
        
        for (Vertex v : part1)
            part1_ar.add(v);
        for (Vertex v : part2)
            part2_ar.add(v);
        
        Cut c =new Cut(part1_ar, part2_ar);
        
        
        return c;
    }
    void printToConsole()
    {
        System.out.println("Vertices");
        for (Vertex v : vertices)
            System.out.println(v.index);
        System.out.println("Edges");
        for (Edge e: edgeList)
            System.out.println(e.u.index + " " + e.v.index + " " + e.weight);
        
    }
    void printFilePajek(String addr) throws IOException
    {
        FileWriter fw1 = new FileWriter(addr);
        fw1.write("*Vertices " + vertices.size() + "\n");
        for (int i = 0; i < this.vertices.size(); i++)
        {
            fw1.write((i+1) + " \"" + vertices.get(i).index + "\"\n");
        }
        fw1.write("*Edges\n");
        for (Edge e : this.edgeList)
            fw1.write(e.u.index + " " + e.v.index + " " + e.weight + "\n");
        fw1.close();                
    }
    double getCutWeight(ArrayList<Vertex> subset)
    {
        double w = 0;
        for (Edge e : edgeList)
        {
            if (subset.contains(e.u) && (!subset.contains(e.v)))
            {
                w+=e.weight;
                continue;
            }
            if (subset.contains(e.v) && (!subset.contains(e.u)))
                w+=e.weight;
        }
        return w;
    }
}
