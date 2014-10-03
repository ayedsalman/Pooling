/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author kki8
 */
public class RerunReport {
    public static void main(String[] args) throws IOException
    {
         int[] ns = {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150};
         int[] maxPoolSizes = {15};
         int[] tests = {0,1,2,3,4,5,6,7,8,9};
         
/*         int[] ns = {100};
         int[] maxPoolSizes = {15};
         int[] tests = {1};*/
         
         int[] clcoeffs = {2};
         String genSampFolder = "Clean_by_cutoffs_meta10_split_new_1";
         for (int sz = 0; sz < maxPoolSizes.length; sz++)
         for (int in = 0; in < ns.length; in++)
             for (int it = 0; it < tests.length; it++)
                 for (int cl = 0; cl < clcoeffs.length; cl++)
                 {
//                     String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it;
                     String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + tests[it] + "_forRobust";
                     System.out.println(outdir);
                     PoolSimulator ps = new PoolSimulator();
                     String genlogfile = outdir + File.separator + "gen_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + tests[it] + ".txt";
                     String resFile = outdir + File.separator + "resReseq_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + tests[it] + ".txt";
                     String reportFile = outdir + File.separator + "report_reseq_new.txt";
                     ArrayList<ArrayList<Integer>> partitions = ps.retrievePartitions(outdir);
                     int nPools = partitions.size();
                     int n = ns[in];
                     ps.generateReportReseq(genlogfile, resFile, reportFile, nPools, 0, outdir, n,genSampFolder);
                     
                 }
    }
    
}
