package Pooling;


import Pooling.PoolsOperator;
import Pooling.Pool;
import ErrorCorrection.DataSet;
import java.io.File;
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
public class RunTestIdExperimentalSamples {
     public static void main(String[] args) throws IOException, InterruptedException
     {
         int nSamples = 8;
         int n = 7;
         int clustCoeff = 2;
         
         String alignMethod = "Muscle";
         String clustMethod = "kGEM";
         
         PoolsOperator po = new PoolsOperator();
         ArrayList<ArrayList<Integer>> partitions = po.generatePartitions(nSamples);
         
         Integer badSample = new Integer(1);
         for (int i = 0; i < partitions.size(); i++)
             if (partitions.get(i).contains(badSample))
                 partitions.get(i).remove(badSample);
         
         for (ArrayList<Integer> ar : partitions)
         {
             for (Integer i : ar)
                 System.out.print(i + " ");
             System.out.println();
         }
        
         String outdir = "Experiment_" + clustMethod;
         File dir = new File(outdir);
         dir.mkdir();
         po.setOutdir(outdir);
         
         int nPools = partitions.size();
         ArrayList<Pool> pools = new ArrayList();
         String poolType = "cDNApool";
         for (int i = 0; i < nPools; i++)
         {
             String poolFile = poolType + (i+1) + "_reversed_unique_all_reads_norecomb_filtered.fas_nearNeigFilt.fas";
             DataSet ds = new DataSet(poolFile);
             pools.add(new Pool(ds,partitions.get(i)));
             pools.get(i).setFileName(poolFile);
             System.out.println();
         }
         

         
         String refFile = "refer_gener10.fas";
         ArrayList<sampRecovAction> ar = po.calcRecovActionsGeneral(partitions);
         for (int i = 0; i < ar.size(); i++)
         {
            System.out.print(i + ": ");
            ar.get(i).print();
         }
         
         ArrayList<Pool> intsDiffs = po.recover(pools, ar, clustCoeff,alignMethod,clustMethod);
         ArrayList<Pool> recovSamp = po.getSamps(intsDiffs);
         
         for (int i = 0; i < n; i++)
             recovSamp.get(i).printToFileUnique(outdir + File.separator + recovSamp.get(i).fileName);
 
         
         for (int i = 0; i < n; i++)
             recovSamp.get(i).compareWithRefPrintLabelledReads(refFile,outdir + File.separator + recovSamp.get(i).fileName + "_align_ref"); 
         
         
         
         System.out.println();
     }
    
}
