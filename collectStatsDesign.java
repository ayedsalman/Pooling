/*
 * To change this template, choose Tools | Templates
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
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author kki8
 */
public class collectStatsDesign {
    public static void main(String[] args) throws IOException
    {
         int maxN = 1000;
         double[] prob = {0.25,0.5,0.75,1.00};
         int nTests = 50;
         
         String folder = "randGraphsSimRes";
         for (int t = 0; t < prob.length; t++)
         {
             FileWriter fw = new FileWriter(folder + File.separator + "statsDesign" + prob[t] + ".txt");
             for (int n = 10; n <= maxN; n+=10)
             {
                 DescriptiveStatistics stats = new DescriptiveStatistics();
                 DescriptiveStatistics stats1 = new DescriptiveStatistics();
                 for (int i = 1; i <= nTests; i++)
                 {
                     BufferedReader br = new BufferedReader(new FileReader(folder + File.separator + "testResults1_" + n + "_" + prob[t] + "_" + i + ".txt"));
                     String s = br.readLine();
                     StringTokenizer st = new StringTokenizer(s," ");
                     int k = Integer.parseInt(st.nextToken());
                     stats.addValue(((double)k)/n);
                     stats1.addValue(k);
                 }
                 fw.write(n + " " + stats.getMean() + " " + (stats.getStandardDeviation()/Math.sqrt(nTests)) + " " + stats1.getMean() + " " + (stats1.getStandardDeviation()/Math.sqrt(nTests)) + "\n");
             }
             fw.close();
         }
    }
    
}
