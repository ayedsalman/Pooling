package Pooling;


import Pooling.DoubleGraphProcessor;
import Pooling.DoubleGraph;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class RunPoolsGeneration {
    public static void main(String[] args) throws IOException, Exception
     {
         int nTests = 10;
         double avsol = 0;
         long avworkTime = 0;
         ArrayList<Integer> solutions = new ArrayList(nTests);
         ArrayList<Long> worktimes = new ArrayList(nTests);
         ArrayList<Integer> solutions_n = new ArrayList(nTests);
         int n = 50;
         double t = 50;
         for (int i = 0; i < nTests; i++)
         {
             int maxWeight = 20;
             int gap = 4;
             double p = 0.75;
//             int n = i;
             int tabuN = 1;

//             DoubleGraph g = new DoubleGraph(n,p);
//             g.setThreshold(t);
//             DoubleGraph g = new DoubleGraph(n,p,t,false);
             DoubleGraph g = new DoubleGraph(n,maxWeight,gap,t,false);

//             DoubleGraph g = new DoubleGraph("Pajek_bad.net");
//             DoubleGraph g = new DoubleGraph("Iter4_Nonrel.net", "Iter4_Nonsep.net");
             DoubleGraph g1 = new DoubleGraph(g);
             g.reduce();
//             g.printToConsole();
//             g.printFilePajek("Pajek" + i + "_" + n + "_" + p + "_" + t);

    //         Cut c = g.findMaxCutNonsep();
    //         c.printToConsole();

             DoubleGraphProcessor dgp = new DoubleGraphProcessor();
//             TestSet ts = dgp.findCliqueTestSetHeur(g);
             
             long startTime = System.currentTimeMillis();
             TestSet ts = dgp.findCliqueTestSetHeur2(g, tabuN);
             long tm = System.currentTimeMillis() - startTime;
             worktimes.add(tm);
             avworkTime+=tm;
             
             ts.printToConsole();
//             ts.createMSTFileQP("init" + i + "_" + n + "_" + p + "_" + t + ".mst", n);
//             g1.printProblemLPFormatQP("problem" + i + "_" + n + "_" + p + "_" + t + ".lp",ts.getNTests(),true);
             System.out.println();
//             ts1.printToConsoleCheckFeasibility(g1);
//             ts.printToConsoleCheckFeasibility(g);

             System.out.println("NTests2: " + ts.getNTests());
             avsol+= ts.getNTests();
             solutions.add(ts.getNTests());
         }
         
         
         FileWriter fw = new FileWriter("testResults_" + n + "_" + (int) t + ".txt");
         for (int i =0; i < solutions.size(); i++)
             fw.write(solutions.get(i) + "\n");
         fw.close();
/*         fw = new FileWriter("testTimes.txt");
         for (int i =0; i < worktimes.size(); i++)
             fw.write(worktimes.get(i) + "\n");
         fw.close();*/
         System.out.println();
         System.out.println("Avsol=" + (avsol/nTests));
         System.out.println("AvworkTime=" + (((double) avworkTime)/nTests));
     }
}
