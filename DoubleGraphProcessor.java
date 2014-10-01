package Pooling;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Pooling.DoubleGraph;
import Pooling.Cut;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author kki8
 */
public class DoubleGraphProcessor {
    
    TestSet findCliqueTestSetHeur(DoubleGraph g)
    {
        TestSet ts = new TestSet();
        HashSet<Vertex> coveredVert = new HashSet(); 
//        g.printToConsole();
//        g.addCrownNonsep();
        
//        while((g.getNEdgesNonsep() > 0) || (coveredVert.size() < g.getNVertices()))
        while(g.getNEdgesNonsep() > 0)
        {
//            g.printToConsole();
            Cut c = g.findMaxCutNonsep();
//            c.printToConsole();
            DoubleGraph g1 = g.subDoubleGraph(c.part1);
            DoubleGraph g2 = g.subDoubleGraph(c.part2);
//            g1.printToConsole();
//            g2.printToConsole();
            ArrayList<Vertex> clique1 = g1.findMaxCliqueNonrelHeuristics();
            ArrayList<Vertex> clique2 = g2.findMaxCliqueNonrelHeuristics();
            ArrayList<Edge> ecut1 = g.cutEdgesNonsep(clique1);
            ArrayList<Edge> ecut2 = g.cutEdgesNonsep(clique2);
            
            if (ecut1.size() >= ecut2.size())
            {
                g.removeEdgesNonsep(ecut1);
                ts.addTest(clique1);
                for (Vertex v : clique1)
                    coveredVert.add(v);
            }
            else
            {
                g.removeEdgesNonsep(ecut2);   
                ts.addTest(clique2);
                for (Vertex v : clique2)
                    coveredVert.add(v);
            }
        }
        
        return ts;
    }
    
    TestSet findCliqueTestSetHeur1(DoubleGraph g) throws IOException
    {
        TestSet ts = new TestSet();
        HashSet<Vertex> coveredVert = new HashSet(); 
//        g.printToConsole();
//        g.addCrownNonsep();
//        g.printToConsole();
        
        
//        while((g.getNEdgesNonsep() > 0) || (coveredVert.size() < g.getNVertices()))
        int nIter = 0;
        while(g.getNEdgesNonsep() > 0)
        {
            nIter++;
            System.out.println("Greedy iteration " + nIter + ", nEdgesNonsep=" + g.getNEdgesNonsep());
//            g.printToConsole();
            
            
            Graph mixg = g.generateMixedGraph();
            mixg.printToConsole();
            
            if (g.getNEdgesNonsep() == 1)
                System.out.println();
            
            Cut c = mixg.findMaxCut();
//            c.printToConsole();
            
            Cut bestCut = null;
            Cut[] cuts = new Cut[2];
            cuts[0] = new Cut(c.part1,c.part2,1);
            cuts[1] = new Cut(c.part2,c.part1,1);
            
//            g.printFilePajek("Iter" + nIter);
            if (g.getNEdgesNonsep() == 3)
            {
//                g.printFilePajek("Iter" + nIter);
                mixg.printFilePajek("MixgIter" + nIter + ".net");
                c.printToConsole();
            } 
            
            for (int i = 0; i < 2; i++)
            {
                boolean toIter = true;
                int M = g.getNEdgesNonsep();

                while (toIter)
                {
//                    System.out.println("Improvment of cut iteration");
                    toIter = false;
                    int record = 0;
                    Vertex vertRecord = null;
                    int deltaNonsepRecord = 0;
                    for (Vertex v : cuts[i].part1)
                    {
                        int deginNonsep = 0;
                        int degoutNonsep = 0;
                        int deginRel = 0;
                        HashSet<Vertex> relSet = new HashSet(g.vertices);
                        relSet.removeAll(g.adjListsNonrel.get(v));
                        relSet.remove(v);

                        for (Vertex u : relSet)
                            if (cuts[i].part1.contains(u))
                                deginRel++;
                        for (Vertex u : g.adjListsNonsep.get(v))
                        {
                            if (cuts[i].part1.contains(u))
                                deginNonsep++;
                            else
                               degoutNonsep++;
                        }
                        int delta = deginNonsep - degoutNonsep + M*deginRel;
                        int deltaNonsep = deginNonsep - degoutNonsep;
                        if (delta > record)
                        {
                            record = delta;
                            vertRecord = v;
                            deltaNonsepRecord = deltaNonsep;
                            toIter = true;
                        }
                        if (delta == record)
                            if (deltaNonsep > deltaNonsepRecord)
                            {
                                record = delta;
                                vertRecord = v;
                                deltaNonsepRecord = deltaNonsep;
                                toIter = true;
                            }
                    }
                    if (toIter)
                    {
                        cuts[i].part1.remove(vertRecord);
                        cuts[i].part2.add(vertRecord);
                    }
                }
            }
            

         
            ArrayList<Edge> ecut1 = g.cutEdgesNonsep(cuts[0].part1);
            ArrayList<Edge> ecut2 = g.cutEdgesNonsep(cuts[1].part1);
            
            if (ecut1.size() >= ecut2.size())
            {
                g.removeEdgesNonsep(ecut1);
                ts.addTest(cuts[0].part1);
                for (Vertex v : cuts[0].part1)
                    coveredVert.add(v);
                System.out.println("Part 1 has been chosen");
//                cuts[0].printToConsole();
            }
            else
            {
                g.removeEdgesNonsep(ecut2);   
                ts.addTest(cuts[1].part1);
                for (Vertex v : cuts[1].part1)
                    coveredVert.add(v);
                System.out.println("Part 2 has been chosen");
//                cuts[1].printToConsole();
            }
        }
        
        return ts;
    }
    TestSet findCliqueTestSetHeur2(DoubleGraph g, int tabuN) throws IOException
    {
        TestSet ts = new TestSet();
        HashSet<Vertex> coveredVert = new HashSet(); 
        int defTabuN = tabuN;
//        g.printToConsole();
//        g.addCrownNonsep();
//        g.printToConsole();
        
        
//        while((g.getNEdgesNonsep() > 0) || (coveredVert.size() < g.getNVertices()))
        int nIter = 0;
        while(g.getNEdgesNonsep() > 0)
        {
            nIter++;
            System.out.println("Greedy iteration " + nIter + ", nEdgesNonsep=" + g.getNEdgesNonsep());
            
//            g.printToConsole();
            int M = g.getNEdgesNonsep();
            Graph mixg = g.generateMixedGraph(0);
//            mixg.printToConsole();
            
            Cut c = mixg.findMaxCut();
//            c.printToConsole();
//            Cut c = g.findMaxCutNonsep();
//            c.printToConsole();
            
            Cut bestCut = null;
            Cut[] cuts = new Cut[2];
            cuts[0] = new Cut(c.part1,c.part2,1);
            cuts[1] = new Cut(c.part2,c.part1,1);
            
//            g.printFilePajek("Iter" + nIter);
//            if (g.getNEdgesNonsep() == 1)
            {
//                g.printFilePajek("Iter" + nIter);
//                mixg.printFilePajek("MixgIter" + nIter + ".net");
//                c.printToConsole();
            } 
            
            Cut[] recordCut = new Cut[2];
            double[] recordCutSize = new double[2];
            
            for (int i = 0; i < 2; i++)
            {
                boolean toIter = true;
                HashMap<Vertex,Integer> tabu = new HashMap();
                recordCut[i] = null;
                recordCutSize[i] = -1;
                Stack<Vertex> candLocalOptimums = new Stack();
                TabuStates tabust = new TabuStates();

                int itr = 0;
                while (toIter)
                {
                    itr++;
                    System.out.println("Improvment of cut iteration " + itr + "; tabuN=" + tabuN);
                    toIter = false;
                    int record = 0;
                    Vertex vertRecord = null;
                    int deltaNonsepRecord = 0;
                    
/*                    cuts[i].printToConsole();
                    System.out.println("----------------");
                    tabust.printToConsole();
                    System.out.println("~~~~~~~~~~~~~~~~~~");
                    System.out.println();*/
                    
                    for (Vertex v : cuts[i].part1)
                    {
                        if (tabu.containsKey(v))
                            continue;
                        int deginNonsep = 0;
                        int degoutNonsep = 0;
                        int deginRel = 0;
                        HashSet<Vertex> relSet = new HashSet(g.vertices);
                        relSet.removeAll(g.adjListsNonrel.get(v));
                        relSet.remove(v);

                        for (Vertex u : relSet)
                            if (cuts[i].part1.contains(u))
                                deginRel++;
                        for (Vertex u : g.adjListsNonsep.get(v))
                        {
                            if (cuts[i].part1.contains(u))
                                deginNonsep++;
                            else
                               degoutNonsep++;
                        }
                        int delta = deginNonsep - degoutNonsep + M*deginRel;
                        int deltaNonsep = deginNonsep - degoutNonsep;
                        if (delta > record)
                        {
                            record = delta;
                            vertRecord = v;
                            deltaNonsepRecord = deltaNonsep;
                            toIter = true;
                        }
                        if (delta == record)
                        {
                            if (deltaNonsep > deltaNonsepRecord)
                            {
                                record = delta;
                                vertRecord = v;
                                deltaNonsepRecord = deltaNonsep;
                                toIter = true;
                            }
                        }
                    }
                    for (Vertex v : cuts[i].part2)
                    {
                        if (tabu.containsKey(v))
                            continue;
                        int deginNonsep = 0;
                        int degoutNonsep = 0;
                        int degoutRel = 0;
                        HashSet<Vertex> relSet = new HashSet(g.vertices);
                        relSet.removeAll(g.adjListsNonrel.get(v));
                        relSet.remove(v);

                        for (Vertex u : relSet)
                            if (cuts[i].part1.contains(u))
                                degoutRel++;
                        for (Vertex u : g.adjListsNonsep.get(v))
                        {
                            if (cuts[i].part2.contains(u))
                                deginNonsep++;
                            else
                               degoutNonsep++;
                        }
                        int delta = deginNonsep - degoutNonsep - M*degoutRel;
                        if (delta > record)
                        {
                            record = delta;
                            vertRecord = v;
                            toIter = true;
                        }
                    }
                    
                    Iterator it = tabu.entrySet().iterator();
                    HashSet<Vertex> toRemove = new HashSet();
                    while (it.hasNext())
                    {
                        Map.Entry me = (Map.Entry) it.next();
                        Vertex v = (Vertex) me.getKey();
                        int k = (Integer) me.getValue();
                        if (k == 1)
                            toRemove.add(v);
                        else
                            tabu.put(v, k-1);
                    }
                    for (Vertex v : toRemove)
                        tabu.remove(v);
                    
                    if (toIter)
                    {
                        if (cuts[i].part1.contains(vertRecord))
                        {
                            cuts[i].part1.remove(vertRecord);
                            cuts[i].part2.add(vertRecord);
                        }
                        else
                        {
                            cuts[i].part2.remove(vertRecord);
                            cuts[i].part1.add(vertRecord);
                        }
                        candLocalOptimums.push(vertRecord);
                        continue;
                    }
                    else
                    {
                        int localOptCutSize = g.cutEdgesNonsep(cuts[i].part1).size();
//                        System.out.println(mixg.getCutWeight(cuts[i].part1));
//                        System.out.println(g.getNonrelEPartDiff(cuts[i].part1));
//                        double localOptCutSize = mixg.getCutWeight(cuts[i].part1) + g.getNonrelEPartDiff(cuts[i].part1);
                        if (localOptCutSize > recordCutSize[i])
                        {
                            recordCut[i] = new Cut(cuts[i]);
                            recordCutSize[i] = localOptCutSize;
                        }
                        if (candLocalOptimums.size() > 0)
                        {
                            Vertex localOptimum = candLocalOptimums.pop();
                            tabu.put(localOptimum, tabuN);
                            if (cuts[i].part1.contains(localOptimum))
                            {
                                cuts[i].part1.remove(localOptimum);
                                cuts[i].part2.add(localOptimum);
                            }
                            else
                            {
                                cuts[i].part2.remove(localOptimum);
                                cuts[i].part1.add(localOptimum);
                            }
                        }
                    }
                    TabuState t = new TabuState(cuts[i].part1, tabu);
                    if (!tabust.containState(t))
                    {
                        toIter = true;
                        tabust.addState(t);
                    }
                    else                        
                    {
                        toIter = false;
                    }
//                    if (candLocalOptimums.size() == 0)
//                        toIter = false;
                }
            }
           
             
            // reduce cuts to feasible size
            for (int i = 0; i < 2; i++)
            {
                while (recordCut[i].part1.size() > g.threshold)
                {
                    int record = Integer.MAX_VALUE;
                    Vertex vertRecord = null;
                    for (Vertex v : recordCut[i].part1)
                    {
                        int deginNonsep = 0;
                        int degoutNonsep = 0;
                        if (degoutNonsep - deginNonsep < record)
                        {
                            record = degoutNonsep - deginNonsep;
                            vertRecord = v;
                        }
                    }
                    recordCut[i].part1.remove(vertRecord);
                    recordCut[i].part2.add(vertRecord);
                }
            } 
            
            System.out.println("Finding ecut1");
            ArrayList<Edge> ecut1 = g.cutEdgesNonsep(recordCut[0].part1);
            System.out.println("Finding ecut2");
            ArrayList<Edge> ecut2 = g.cutEdgesNonsep(recordCut[1].part1);
            System.out.println("ecuts found");
            
            if ((ecut1.size() == 0)&&(ecut2.size() == 0))
            {
                tabuN++;
                continue;
            }
            
            
            if (ecut1.size() >= ecut2.size())
            {
                g.removeEdgesNonsep(ecut1);
                ts.addTest(recordCut[0].part1);
                for (Vertex v : recordCut[0].part1)
                    coveredVert.add(v);
                System.out.println("Part 1 has been chosen");
//                cuts[0].printToConsole();
            }
            else
            {
                g.removeEdgesNonsep(ecut2);   
                ts.addTest(recordCut[1].part1);
                for (Vertex v : recordCut[1].part1)
                    coveredVert.add(v);
                System.out.println("Part 2 has been chosen");
//                cuts[1].printToConsole();
            }
            tabuN = defTabuN;
/*            if (nIter > g.getNEdgesNonsep())
            {
                nIter = 0;
                tabuN++;
            }*/
        }
        
        return ts;
    }
    
}
