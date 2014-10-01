package Pooling;


import Pooling.PoolsOperator;
import Pooling.Pool;
import ErrorCorrection.DataSet;
import ErrorCorrection.Read;
import java.io.FileWriter;
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
public class RunTestIdSamples {
    public static void main(String[] args) throws IOException, InterruptedException
     {
         int nSamples = 8;
         double cutoff = 0.15;
         double cutoff_align = 0.45;
         double cutoff_len = 325;
         int nearNeigThr = 3;
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
        
         
         int nPools = partitions.size();
         ArrayList<Pool> pools = new ArrayList();
         String poolType = "cDNApool";
         for (int i = 0; i < nPools; i++)
         {
//             String poolFile = poolType + (i+1) + "_reversed_unique.fas";
//             String poolFile = poolType + (i+1) + "_reversed_unique_all_reads.fas";
//             String poolFile = poolType + (i+1) + "_reversed_unique_norecomb.fas";
//             String poolFile = poolType + (i+1) + "_reversed_unique_all_reads_norecomb.fas";
             String poolFile = poolType + (i+1) + "_reversed_unique_all_reads_norecomb_filtered.fas_nearNeigFilt.fas";
//             String poolFile = poolType + (i+1) + "_reversed_unique_all_reads_norecomb_filtered.fas";
             DataSet ds = new DataSet(poolFile);
             pools.add(new Pool(ds,partitions.get(i)));
             pools.get(i).setFileName(poolFile);
//             po.clusteringButterfly(pools.get(i).indsamples.size(), poolFile);
             System.out.println();
         }
         

         
         
//         int i = 0;
         String refFile = "refer_gener10.fas";
         String refFileHaplKGEM = "haplotypes_cleaned.fas";
         for (int i = 0; i < nPools; i++)
         {
             pools.get(i).clusterFindCentroids();
//             pools.get(i).printClustersReads();
//             pools.get(i).printToFileUnique(poolType + (i+1) + "_reversed_unique_all_reads_norecomb_filtered_unique.fas");
//             pools.get(i).filter(refFile, cutoff);
//             pools.get(i).filter(refFile, cutoff_align, cutoff_len);
//             pools.get(i).filterERIF("ref.fas");
//             pools.get(i).printToFile(poolType + (i+1) + "_reversed_unique_all_reads_norecomb_filtered.fas");
//             pools.get(i).compareWithRef(refFile,poolType + (i+1) + "corrected_haplotypes_align_ref");
//             pools.get(i).compareWithRefPrintLabelledReads(refFile,poolType + (i+1) + "corrected_haplotypes_align_ref");
//             pools.get(i).compareWithRefKmers(refFile,poolType + (i+1)+"_corrected.fas");
//             pools.get(i).ds.PrintUniqueReads(poolType + (i+1) + "MID" + (i+5) + "HVR1_reversed.fas_unique.fas_filtered_refgenotype.fas");
//             pools.get(i).ds.PrintReads(poolType + (i+1) + "MID" + (i+5) + "HVR1_reversed.fas_filtered_refgenotype.fas");
//             String distMatrFile = poolType + (i+1) + "filtered_refgenotype_distMatr.txt";
//             pools.get(i).generateDistanceMatrix();
//             pools.get(i).filterDistNearNeighb(nearNeigThr);
//             pools.get(i).printToFile(pools.get(i).fileName + "_nearNeigFilt.fas");
//             pools.get(i).printDistanceMatrix(distMatrFile);
             
//             pools.get(i).readDistMatr(distMatrFile);
 
//             pools.get(i).separateClusters(poolType + (i+1) + "filtered_refgenotype_clusters.txt");
//             pools.get(i).printClustersReads(poolType + (i+1));
                     
                     
         }
         
/*         Pool samp2 = new Pool("Samp2.fas");
         samp2.indsamples.add(2);
         Pool samp3 = new Pool("Samp3.fas");
         samp3.indsamples.add(3);
         Pool samp4 = new Pool("Samp4.fas");
         samp4.indsamples.add(4);
         Pool samp5 = new Pool("Samp5.fas");
         samp5.indsamples.add(5);
         Pool samp6 = new Pool("Samp6.fas");
         samp6.indsamples.add(6);
         Pool samp7 = new Pool("Samp7.fas");
         samp7.indsamples.add(7);
                 
         Pool inter12 = new Pool("Inter12.fas");
         inter12.indsamples.add(5);
         inter12.indsamples.add(6);
         
         Pool inter13 = new Pool("Inter13.fas");
         inter13.indsamples.add(5);
         inter13.indsamples.add(7);*/
         
         
/*         Pool minus = pools.get(1).minus(inter13);        
         String nameMinus = "Mix68";
         minus.printToFile(nameMinus);
         minus.compareWithRefPrintLabelledReads(refFile,nameMinus + "_align_ref");*/
         
//         pools.get(1).compareWithRefPrintLabelledReads(refFile,"Mix5678" + "_align_ref");
//         pools.get(1).clusterkGEM();
         
         
         ArrayList<Pool> intersDiffs = new ArrayList(9);
         intersDiffs.add(pools.get(0).intersectCentroids(pools.get(2)));
         intersDiffs.add(pools.get(0).intersectCentroids(pools.get(3)));
         intersDiffs.add((pools.get(0).minusCentroids(intersDiffs.get(0))).minusCentroids(intersDiffs.get(1)));
         intersDiffs.add(pools.get(1).intersectCentroids(pools.get(2)));
         intersDiffs.add(pools.get(1).intersectCentroids(pools.get(3)));
         intersDiffs.add(intersDiffs.get(3).intersectCentroids(intersDiffs.get(4)));
         intersDiffs.add(intersDiffs.get(3).minusCentroids(intersDiffs.get(5)));
         intersDiffs.add(intersDiffs.get(4).minusCentroids(intersDiffs.get(5)));
         intersDiffs.add((pools.get(1).minusCentroids(intersDiffs.get(3))).minusCentroids(intersDiffs.get(7)));
         
         for (int i = 0; i < 9; i++)
             intersDiffs.get(i).compareWithRefPrintLabelledReads(refFile,intersDiffs.get(i).fileName + "_align_ref"); 
         
         
         
 
         
/*         Pool inter = pools.get(1).intersectCentroids(pools.get(3));
         
         inter.printToFile(inter.fileName);         
//         inter.compareWithRefPrintLabelledReads(refFile,inter.fileName + "_align_ref"); 
//         inter.printClustersReads();
         
         Pool diff = pools.get(1).minusCentroids(inter);
         diff.printToFile(diff.fileName);
         diff.compareWithRefPrintLabelledReads(refFile,diff.fileName + "_align_ref"); 
         diff.printClustersReads();
         
         
//         Pool pl = new Pool("Union_reads.fas");
//         pl.compareWithRefPrintLabelledReads(refFileHaplKGEM, "Union_reads.fas_align_ref.fas");*/
         
         System.out.println();
     }
    
}
