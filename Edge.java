package Pooling;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class Edge {
    Vertex u;
    Vertex v;
    double weight;
    Edge (Vertex i, Vertex j)
    {
        u = i;
        v = j;
    }
     Edge (Vertex i, Vertex j, double w)
    {
        u = i;
        v = j;
        weight = w;
    }
     Vertex anotherEnd(Vertex x)
     {
         if (u == x)
             return v;
         if (v == x)
             return u;
         return null;
     }
}
