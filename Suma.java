package Pooling;


import Pooling.PoolSimulator;
import ErrorCorrection.DataSet;
import ErrorCorrection.Read;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class Suma {
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException
    {
        String folder = "Suma";
        int nClust = 15;
         File folderF = new File(folder);
         File[] list_files = folderF.listFiles();
         int n = list_files.length;
         DataSet union_ds = new DataSet();
         for (int i = 0; i < n; i++)
         {
             String addr = list_files[i].getName();
             System.out.println(addr);
             DataSet ds = new DataSet(folder + File.separator + addr,"ET");
             for (int j = 0; j < ds.reads.size(); j++)
             {
                 ds.reads.get(j).name = addr + "_" + (j+1) + "_" + ds.reads.get(j).getFreq();
                 union_ds.reads.add(ds.reads.get(j));
             }
//             ds.PrintReadsNoCopyNumber(folder + File.separator + addr + "_renamed_all.fas");
         }
/*         int maxlen = 0;
         int minlen = Integer.MAX_VALUE;
         String maxname = "";
         for (Read r : union_ds.reads)
         {
             if (r.getLength() > maxlen)
             {
                 maxlen = r.getLength();
                 maxname = r.name;
             }
             if (r.getLength() < minlen)
                 minlen = r.getLength();
         }*/
         
         String addr_all = "union_all_suma.fas";
         union_ds.PrintReadsNoCopyNumber(addr_all);
         union_ds.PrintUniqueReads(addr_all + "_unique.fas");
         String s = "java -jar kgem-clustering-0.5c.jar " +  addr_all + " " +  nClust +  " -c " +  addr_all +  " -o clust_" +  addr_all;


        System.out.println(s);

        Process p = Runtime.getRuntime().exec(s);
        
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
         while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
         }
        p.waitFor();
         if (p.exitValue()!= 0)
         {
            System.out.println("Error in Butterfly");
         }
         System.out.println();
         
         PoolSimulator ps = new PoolSimulator();
        String addr = "clust_" + addr_all + File.separator + "reads_clustered.fas";
        
        FileWriter fw = new FileWriter("stats_for_Suma_" + nClust + "clusters.txt");
        fw.write(ps.checkClusteringGetOutString(addr));
        fw.close();
        
        HashMap<String,Integer> hm = new HashMap();
        BufferedReader br = new BufferedReader(new FileReader("stats_for_Suma_" + nClust + "clusters.txt"));
        s = br.readLine();
        int i = 0;
        int currNumber = 0;
        int[][] matr = new int[nClust][n];
        while (s != null)
        {
            StringTokenizer st = new StringTokenizer(s," ");
            st.nextToken();
            while (st.hasMoreTokens())
            {
                String name = st.nextToken();
                if (!hm.containsKey(name))
                {
                    hm.put(name, currNumber);
                    matr[i][currNumber] = 1;
                    currNumber++;
                }
                else
                    matr[i][hm.get(name)] = 1;
            }
            s = br.readLine();
            i++;
        }
        fw = new FileWriter("stats_for_Suma_" + nClust + "clusters_matrix.txt");
        for (i = 0; i < nClust; i++)
        {
            for (int j = 0; j < n; j++)
                fw.write(matr[i][j] + " ");
            fw.write("\n");
        }
        fw.close();
        fw = new FileWriter("stats_for_Suma_" + nClust + "samp_numbers.txt");
        for (Map.Entry me : hm.entrySet())
        {
            String name = (String) me.getKey();
            int num = (Integer) me.getValue();
            fw.write(name + " " + num + "\n");
        }
        fw.close();

    }
    
}
