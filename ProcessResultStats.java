package Pooling;


import Pooling.Pool;
import ErrorCorrection.DataSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class ProcessResultStats {
    public static void main(String[] args) throws IOException, InterruptedException
    {

         int nSeqPool = 5000;
         int nRef = 3;
         int[] ns = {80};
         int nTests = 1;
         int[] maxPoolSizes = {25,35};

         for (int sz = 0; sz < maxPoolSizes.length; sz++)
         for (int in = 0; in < ns.length; in++)
             for (int it = 0; it < nTests; it++)
             {
                 int n = ns[in];
                 String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it;
                 String genlogfile = outdir + File.separator + "gen_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it + ".txt";
                 String resFile = outdir + File.separator + "res_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it + ".txt";
                 PoolSimulator ps = new PoolSimulator();
                 ps.generateReport(genlogfile, resFile, 10, 10, outdir, n);
             }

        
        

        
    }
    
}
