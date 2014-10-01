/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author kki8
 */
public class collectStatsTime {
    public static void main(String[] args) throws IOException
    {
/*         int maxN = 1000;
         double[] prob = {0.25,0.5,0.75,1.00};
         int[] maxPoolSize = {35};
         int nTests = 50;
         
         String folder = "randGraphsSimRes";
         for (int t = 0; t < maxPoolSize.length; t++)
         {
             FileWriter fw = new FileWriter(folder + File.separator + "statsDesignTime" + maxPoolSize[t] + ".txt");
             for (int n = 10; n <= maxN; n+=10)
             {
                 DescriptiveStatistics stats = new DescriptiveStatistics();
                 for (int i = 1; i <= nTests; i++)
                 {
                     String resfile = "testResults_" + n + "_" + maxPoolSize[t] + "_" + i + ".txt";
                     BufferedReader br = new BufferedReader(new FileReader(folder + File.separator + resfile));
                     System.out.println(resfile);
                     String s = br.readLine();
                     StringTokenizer st = new StringTokenizer(s," ");
                     st.nextToken();
                     int k = Integer.parseInt(st.nextToken());
                     stats.addValue(((double)k)/1000);
                 }
                 fw.write(n + " " + stats.getMean() + " " + (stats.getStandardDeviation()/Math.sqrt(nTests)) +  "\n");
             }
             fw.close();
         } */
        
         int[] ns = {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150};
         int[] maxPoolSizes = {15,25,35};
         int nTests = 10;
         for (int sz = 0; sz < maxPoolSizes.length; sz++)
         {
             FileWriter fw = new FileWriter("statisticsTime_" + maxPoolSizes[sz] + ".txt");
             for (int in = 0; in < ns.length; in++)
                 {
                     DescriptiveStatistics stats = new DescriptiveStatistics();
                     for (int it = 0; it < nTests; it++)
                     {
                        String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it;
                        String repFile = outdir + File.separator + "report.txt";
                        System.out.println(outdir);
                        BufferedReader br = new BufferedReader(new FileReader(repFile));
                        String s = "";
                        for (int i = 0; i < 6; i++)
                            s = br.readLine();
                        StringTokenizer st = new StringTokenizer(s,":");
                        st.nextToken();
                        double tm = Double.parseDouble(st.nextToken());
                        stats.addValue(tm);
                     }
                     double maxtime = stats.getMax();
                     DescriptiveStatistics stats1 = new DescriptiveStatistics();
                     for (int i = 0; i < nTests; i++)
                         if (stats.getElement(i) != maxtime)
                             stats1.addValue(stats.getElement(i));
                     fw.write(ns[in] + " " + stats1.getMean() + " " + (stats1.getStandardDeviation()/Math.sqrt((nTests-1))) +  "\n");
                 }
             fw.close();
         }
    }
    
}
