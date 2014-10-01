/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import ErrorCorrection.DataSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author kki8
 */
public class collectStats {
    public static void main(String[] args) throws IOException, InterruptedException
     {
         int[] ns = {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150};
         int[] maxPoolSizes = {15,25,35};
         int[] clcoeffs = {2};
         int nTests = 10;
         
         String[] params = {"percRecSamp", "percNonContSamp", "percGoodReads", "AvPercBadReadsSamp", "nPools", "Time", "PercClassified", "RMSE"};
          
         for (int sz = 0; sz < maxPoolSizes.length; sz++)
         {
             for (int cl = 0; cl < clcoeffs.length; cl++)
             {
                 FileWriter fw = new FileWriter("statistics_" + maxPoolSizes[sz] + "_" + clcoeffs[cl] + ".txt");
                 ArrayList<String[][]> data = new ArrayList();
                 for (int i = 0; i < params.length; i++)
                     data.add(new String[nTests][ns.length]);
                 fw.write("x ");
                 for (int i = 0; i < ns.length; i++)
                     fw.write(ns[i] + " ");
                 fw.write("\n");
                 for (int in = 0; in < ns.length; in++)
                 {
                     for (int it = 0; it < nTests; it++)
                     {
//                        String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + it;
                        String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + clcoeffs[cl] + "_" + it;
                        String repFile = outdir + File.separator + "report_new.txt";
                        BufferedReader br = new BufferedReader(new FileReader(repFile));
                        int i = 0;
                        String s = br.readLine();
                        while (s != null)
                        {
                            StringTokenizer st = new StringTokenizer(s,":");
                            st.nextToken();
                            data.get(i)[it][in] = st.nextToken();
                            s = br.readLine();
                            i++;
                        }
                     }
                 }

                 for (int u = 0; u < nTests; u++)
                     for (int v = 0; v < ns.length; v++)
                     {
                         try
                         {
                            data.get(1)[u][v] = "" + (100 - Double.parseDouble(data.get(1)[u][v]));
                         }
                         catch (Exception e)
                         {
                             System.err.println(maxPoolSizes[sz]);
                             System.err.println(u);
                             System.err.println(ns[v]);
                             System.err.println(data.get(1)[u][v]);
                         }
                     }


                 for (int i = 0; i < params.length; i++)
                 {
                     fw.write(params[i] + "\n");
                     for (int u = 0; u < nTests; u++)
                     {
                         fw.write((u+1) + " ");
                         for (int v = 0; v < ns.length; v++)
                         {
                             if (data.get(i)[u][v].contains("NaN"))
                                 data.get(i)[u][v] = "0";
                             fw.write(data.get(i)[u][v] + " ");
                         }
                         fw.write("\n");
                     }
                 }
                 fw.close();
             }
         }
                 
     }
}
