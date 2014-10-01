package Pooling;


import Pooling.PoolsOperator;
import Pooling.PoolSimulator;
import Pooling.Pool;
import ErrorCorrection.DataSet;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class RerunDebug {
    public static void main(String[] args) throws IOException, InterruptedException
     {
         int nSamples = 10;
         int n = nSamples;
         PoolsOperator po = new PoolsOperator();
         ArrayList<ArrayList<Integer>> partitions = po.generatePartitions(nSamples);
         
         
         for (ArrayList<Integer> ar : partitions)
         {
             for (Integer i : ar)
                 System.out.print(i + " ");
             System.out.println();
         }
        
         
         int nPools = partitions.size();
         ArrayList<Pool> pools = new ArrayList();
         for (int i = 0; i < nPools; i++)
         {
             String poolFile = "SimPool" + i + ".fas";
             DataSet ds = new DataSet(poolFile);
             pools.add(new Pool(ds,partitions.get(i)));
             pools.get(i).setFileName(poolFile);
             System.out.println();
         }
         

         ArrayList<sampRecovAction> ar = po.calcRecovActionsRecursive(n);
         for (int i = 0; i < ar.size(); i++)
         {
            System.out.print(i + ": ");
            ar.get(i).print();
         }
         
         ArrayList<Pool> intsDiffs = po.recover(pools, ar);
         ArrayList<Pool> recovSamp = po.getSamps(intsDiffs);
         

         PoolSimulator ps = new PoolSimulator();
         for (int i = 0; i < recovSamp.size(); i++)
                 {
                     Pool p =recovSamp.get(i);
                     p.printToFileUnique(p.fileName);
                     String s = (i+1) + ": ";

                     s = s + ps.checkSolutionGetOutString(p);
                     System.out.println(s);

                     System.out.println();
                 }
                           
         
         System.out.println();
     }
    
}
