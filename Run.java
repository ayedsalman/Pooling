package Pooling;


import ErrorCorrection.DataSet;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pavel
 */
public class Run {
     public static void main(String[] args) throws IOException
     {
         int n = 8;         
/*         PoolsOperator po = new PoolsOperator();
         ArrayList<ArrayList<Integer>> partitions = po.generatePartitions(n);
         for (ArrayList<Integer> ar : partitions)
         {
             for (Integer i : ar)
                 System.out.print(i + " ");
             System.out.println();
         }
         
        ArrayList<DataSet> samples = new ArrayList();
        for (int i = 1; i <= n; i++)
            samples.add(new DataSet("Patient" + i + ".fas_renamed.fas",'c'));
        
        PoolSimulator ps = new PoolSimulator(samples);
        ArrayList<Pool> pools = ps.generate();
        for (int i = 0; i < pools.size(); i++)
        {
            Pool p = pools.get(i);
            p.printToFile("Pool");
        }*/
         
         int m =  (int) (Math.log(n)/Math.log(2)) + 1;
         
         for (int i = 1; i <= m; i++)
         {
             String poolFile = "serumpool" + i + "MID" + i + "HVR1_reversed.fas";
             DataSet ds = new DataSet(poolFile);
             ds.findHaplotypes();
             ds.PrintHaplotypes(poolFile + "_haplotypes.fas");
             ds.PrintUniqueReads(poolFile + "_uniqueReads.fas");
         }
         
         System.out.println();
     }
    
}
