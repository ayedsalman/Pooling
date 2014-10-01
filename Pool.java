package Pooling;

import ErrorCorrection.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pavel
 */
public class Pool {
    public DataSet ds;
    public ArrayList<Integer> indsamples;
    public double[][] distMatr;
    public String fileName;
    HashMap<Read,DataSet> centroidsClusters;
    public Pool()
    {
        
    }
     public Pool(ArrayList<Integer> indsamples)
    {
        this.indsamples = indsamples;
        this.ds = new DataSet();
    }
    public Pool(String addr, ArrayList<Integer> s) throws IOException
    {
        ds = new DataSet(addr);
        indsamples = new ArrayList(s);
    }
     public Pool(String addr) throws IOException
    {
        ds = new DataSet(addr);
        indsamples = new ArrayList();
    }
    public Pool (DataSet ds1, ArrayList<Integer> s)
    {
        ds = ds1;
        indsamples = new ArrayList(s);
    }
        public Pool (DataSet ds1)
    {
        ds = ds1;
        indsamples = new ArrayList();
    }
    public void printToFile(String addr) throws IOException
    {
        String numsamples = "";
        for (Integer i : indsamples)
            numsamples += ("_" + i);
//        ds.PrintReads(addr + numsamples + ".fas");
//        ds.PrintReadsNoCopyNumber(addr);
        ds.PrintReads(addr);
    }
    public void printToFileUnique(String addr) throws IOException
    {
        String numsamples = "";
        for (Integer i : indsamples)
            numsamples += ("_" + i);
//        ds.PrintReads(addr + numsamples + ".fas");
        ds.PrintUniqueReads(addr + numsamples + ".fas");
    }
    public void setIndSample(ArrayList<Integer> is)
    {
        indsamples = is;
    }
    public void setDS(DataSet ds1)
    {
        ds = ds1;
    }
    public void setFileName(String addr)
    {
        fileName = addr;
    }
    void generateDistanceMatrix() throws IOException
    {
        int n = ds.reads.size();
        distMatr = new double[n][n];
        
        int k = 25;
        ds.setK(k);
        ds.calculateKMersAndKCounts();
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
            {
                System.out.println("Calculate (" + i + "," + j + ") of " + n);
//                distMatr[i][j] = ds.reads.get(i).calcEditDistAbsAlign(ds.reads.get(j),15,6);
                distMatr[i][j] = ds.reads.get(i).calcEditDistKmer(ds.reads.get(j));
                distMatr[j][i] = distMatr[i][j];           
            }
    }
    void separateClustersFromFile(String clustFile) throws FileNotFoundException, IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
 /*       BufferedReader br = new BufferedReader(new FileReader(clustFile));
        int nClusters = indsamples.size();
        dsClusters = new ArrayList<DataSet>();
        for (int i = 0; i < nClusters; i++)
            dsClusters.add(new DataSet());
        for (int i = 0; i < ds.reads.size(); i++)
        {
            int cl = Integer.parseInt(br.readLine())-1;
            dsClusters.get(cl).reads.add(ds.reads.get(i));
        } */
    }
    // </editor-fold>
    void printClustersUnReads(String addr) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Iterator it = centroidsClusters.entrySet().iterator();
        int i = 0;
        while (it.hasNext())
        {
            i++;
            Map.Entry me = (Map.Entry) it.next();
            ((DataSet) me.getValue()).PrintUniqueReads(this.fileName + "_Cluster" + i + ".fas");
        }
    }
    // </editor-fold>
     void printClustersReads() throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
       Iterator it = centroidsClusters.entrySet().iterator();
        int i = 0;
        while (it.hasNext())
        {
            i++;
            Map.Entry me = (Map.Entry) it.next();
            ((DataSet) me.getValue()).PrintReads(this.fileName + "_Cluster" + i + ".fas");
            
            Pool p = new Pool(this.fileName + "_Cluster" + i + ".fas");
//            p.compareWithRefPrintLabelledReads("refer_gener5.fas",this.fileName + "_Cluster" + i + ".fas" + "_align_ref"); 
        }
    }
    // </editor-fold>
    void filter(String refFile, double cutoff) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        DataSet refds = new DataSet(refFile,'c');
        ArrayList badreads = new ArrayList();
//        FileWriter fw = new FileWriter("stat_ref.txt");
        for (int i = 0; i < ds.reads.size(); i++)
        {
            System.out.println("Read " + i + " of " + ds.reads.size());
            double mindist = Double.MAX_VALUE;
            Read minRef = null;
            for (Read r : refds.reads)
            {
                double d = ds.reads.get(i).calcEditDistAlign(r);
                if (d < mindist)
                {
                    mindist = d;
                    minRef = r;
                }
            }
//            fw.write(i + " " + minRef.name + " " + mindist + "\n");
            if (mindist > cutoff)
                badreads.add(ds.reads.get(i));
        }
//        fw.close();
        ds.reads.removeAll(badreads);
        
    }
    void filter(String refFile, double cutoff_align, double cutoff_len) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        DataSet refds = new DataSet(refFile,'c');
        ArrayList badreads = new ArrayList();
//        FileWriter fw = new FileWriter("stat_ref.txt");
        for (int i = 0; i < ds.reads.size(); i++)
        {
            System.out.println("Read " + i + " of " + ds.reads.size());
            
            if (ds.reads.get(i).getLength() > cutoff_len)
            {
                badreads.add(ds.reads.get(i));
                continue;
            }
            
            double mindist = Double.MAX_VALUE;
            Read minRef = null;
            for (Read r : refds.reads)
            {
                double d = ds.reads.get(i).calcEditDistAlign(r);
                if (d < mindist)
                {
                    mindist = d;
                    minRef = r;
                }
            }
//            fw.write(i + " " + minRef.name + " " + mindist + "\n");
            if (mindist > cutoff_align)
                badreads.add(ds.reads.get(i));
        }
//        fw.close();
        ds.reads.removeAll(badreads);
        
    }
    // </editor-fold>
    void filterERIF(String refFile) throws IOException, InterruptedException
    {
        ds.PrintReads("pool_reads.fas");
        
        Runtime run=Runtime.getRuntime();
        String s = "java -jar ERIF-1.0.jar -g " + refFile + " -i pool_reads.fas -noHashing";

        Process p = Runtime.getRuntime().exec(s);
        
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
         while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
         }
         p.waitFor();
         if (p.exitValue()!= 0)
         {
            System.out.println("Error in ERIF");
         }
         
         DataSet filt = new DataSet("trash.fasta");
         ArrayList<Read> toRemove = new ArrayList();
         
         for (Read r : ds.reads)
             if (filt.containRead(r))
                 toRemove.add(r);
                  
         
         ds.reads.removeAll(toRemove);
         
         File f = new File("pool_reads.fas");
         f.delete();
        f = new File("reads.sam");
        f.delete();
        f = new File("reads.sam_ext.txt");
        f.delete();
        f = new File("ref.fas_ext.fasta");
        f.delete();
        f = new File("trash.fasta");
        f.delete();
    }
    Pool union(Pool p)
    {
        Pool un = new Pool();
        un.setFileName("u_" + this.fileName + "_" + p.fileName + ".fas");
        un.indsamples = new ArrayList(this.indsamples);
        un.indsamples.removeAll(p.indsamples);
        un.indsamples.addAll(p.indsamples);
        un.ds = new DataSet();
        for (Read r : this.ds.reads)
            un.ds.reads.add(new Read(r.getNucl(),r.name + "_p1",r.getFreq()));
        for (Read r : p.ds.reads)
            un.ds.reads.add(new Read(r.getNucl(),r.name + "_p2",r.getFreq()));
        return un;
        
    }
    void add(Pool p)
    {
        for (Read r : p.ds.reads)
        {
            boolean toAdd = true;
            for (Read s : this.ds.reads)
                if (r.nucl.equalsIgnoreCase(s.nucl))
                {
                    s.setFrequency(s.getFreq() + r.getFreq());
                    toAdd = false;
                    break;
                }
            if (toAdd)
                this.ds.reads.add(new Read(r.nucl,r.name,r.getFreq()));
        } 
    }
    void remove(Pool p)
    {
        for (Read r : p.ds.reads)
        {
            for (Read s : this.ds.reads)
                if (r.nucl.equalsIgnoreCase(s.nucl))
                {
                    this.ds.reads.remove(s);
                    break;
                }
        } 
    }
    public Pool intersectMatlab(Pool p) throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        
        int k = this.indsamples.size() + p.indsamples.size() - inter.indsamples.size();
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
        PoolsOperator po = new PoolsOperator();
        double[][] matr = po.generateDistanceMatrix(this, p);
        
        FileWriter fw = new FileWriter("Union_distMatr.txt");
        for (int l = 0; l < matr.length; l++)
        {
            for (int j = 0; j < matr.length; j++)
                fw.write(matr[l][j] + " ");
            fw.write("\n");
        }
        fw.close();
        
        int n1 = this.ds.reads.size();
        int n2 = p.ds.reads.size();
        
        po.clusteringMatlab(k);
        int[] clusters = new int[n1+n2];
        String clustFile = "Union_clusters.txt";
        BufferedReader br = new BufferedReader(new FileReader(clustFile));
        for (int i = 0; i < clusters.length; i++)
            clusters[i] = Integer.parseInt(br.readLine())-1;
        br.close();
        
        File fl = new File("Union_clusters.txt");
        fl.delete();
        
        
        DataSet ds = new DataSet();
        for (int i = 0; i < n1; i++)
            ds.reads.add(this.ds.reads.get(i));        
        for (int i = 0; i < n2; i++)
            ds.reads.add(p.ds.reads.get(i));
        for (int i = 0; i < ds.reads.size(); i++)
            ds.reads.get(i).name = "Cluster" + clusters[i];
        
        ds.PrintReads("Union_reads.fas");
        
        
        int[] clustNPools = new int[k];
        
        
        for (int i = 0; i < n1; i++)
            if(clustNPools[clusters[i]] == 0)
                clustNPools[clusters[i]] = 1;
        
        for (int i = n1; i < n1+n2; i++)
        {
            if(clustNPools[clusters[i]] == 1)
                clustNPools[clusters[i]] = 2;
        }
        
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        ArrayList<Integer> interSeqInd = new ArrayList();
        for (int i = 0; i < n1; i++)
            if (clustNPools[clusters[i]] == 2)
            {
                interSeqInd.add(i);
                Read r = this.ds.reads.get(i);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());	
            }
        for (int i = 0; i < n2; i++)
            if (clustNPools[clusters[n1+i]] == 2)
            {
                interSeqInd.add(n1+i);
                Read r = p.ds.reads.get(i);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());	
            }
        
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            String nm = "read" + count;
            inter.ds.reads.add(new Read(nucleo,nm,f));
            count++;
	}
        
        
        return inter;
    }
    // </editor-fold>
    public Pool intersectKGEM(Pool p) throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.setFileName("i_" + this.fileName + "_" + p.fileName + ".fas");
        
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
        PoolsOperator po = new PoolsOperator();   
        Pool un = this.union(p);
        un.printToFile(un.fileName);
        un.ds.PrintUniqueReads(un.fileName + "_unique.fas");
        
        int clustCoeff = 2;
//        un.cluster(clustCoeff);
        un.clusterExtAlign(clustCoeff);
        
        String fold = "clust_" + un.fileName;
        
        File fl = new File(fold + File.separator + "reads_clustered.fas");
        boolean b = fl.exists();
        
        int iters = 0;
        while (!b)
        {
            System.out.println("Waiting for kGEM:" + (iters++));
            Thread.sleep(1000);
            b = fl.exists();
        }
        
        
        DataSet union_ds = new DataSet(fold + File.separator + "reads_clustered.fas",'c');
        HashSet<Integer> clids = new HashSet();
        
        int[] clusters = new int[union_ds.reads.size()];
//        Map<Integer, Integer> indMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            StringTokenizer st = new StringTokenizer(union_ds.reads.get(i).name,"_");
            String s1 = st.nextToken();
            int bestHaplo = Integer.parseInt(s1.substring(1));
            clids.add(bestHaplo);
/*            if (!indMap.containsKey(bestHaplo))
            {
                indMap.put(bestHaplo, indMap.size());
            }
            bestHaplo = indMap.get(bestHaplo); */

            clusters[i] = bestHaplo;
            while (st.hasMoreTokens())
                s1 = st.nextToken();
            int freq = Integer.parseInt(s1);
            union_ds.reads.get(i).setFrequency(freq);
        }
        
/*        File fl = new File("reads_clustered.fas");
        fl.delete();
        fl = new File("union_reads.fas");
        fl.delete();*/
                
        
        HashMap<Integer,HashSet<Integer>> clustPools = new HashMap();
        for (Integer i : clids)
            clustPools.put(i, new HashSet());
        
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            boolean b1 = this.ds.containRead(union_ds.reads.get(i));
            boolean b2 = p.ds.containRead(union_ds.reads.get(i));
            try
            {
            if (b1)
                clustPools.get(clusters[i]).add(1);
            if (b2)
                clustPools.get(clusters[i]).add(2);       
            }
            catch (java.lang.NullPointerException e)
            {
                System.out.println(i);
                System.out.println(clusters[i]);
                System.out.println();
                e.printStackTrace();
            }
        }
        
        
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        HashMap<String,String> seqNames = new HashMap<String, String>();
        for (int i = 0; i < union_ds.reads.size(); i++)
            if (clustPools.get(clusters[i]).size() == 2)
            {
                Read r = union_ds.reads.get(i);
                StringTokenizer st = new StringTokenizer(r.name,"_");
                int pref_len = st.nextToken().length();
                pref_len += st.nextToken().length();
                String newName = r.name.substring(pref_len+2);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                seqNames.put(r.getNucl(), newName);
            }
        
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            inter.ds.reads.add(new Read(nucleo,seqNames.get(nucleo),f));
            count++;
	}
        
        
        return inter;
    }
    public Pool intersectKGEM(Pool p, int clustCoeff) throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.setFileName("i_" + this.fileName + "_" + p.fileName + ".fas");
        
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
        PoolsOperator po = new PoolsOperator();   
        Pool un = this.union(p);
        un.printToFile(un.fileName);
        un.ds.PrintUniqueReads(un.fileName + "_unique.fas");
        
//        un.cluster(clustCoeff);
        un.clusterExtAlign(clustCoeff);
        
        String fold = "clust_" + un.fileName;
        
        File fl = new File(fold + File.separator + "reads_clustered.fas");
        boolean b = fl.exists();
        
        int iters = 0;
        while (!b)
        {
            System.out.println("Waiting for kGEM:" + (iters++));
            Thread.sleep(1000);
            b = fl.exists();
        }
        
        
        DataSet union_ds = new DataSet(fold + File.separator + "reads_clustered.fas",'c');
        HashSet<Integer> clids = new HashSet();
        
        int[] clusters = new int[union_ds.reads.size()];
//        Map<Integer, Integer> indMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            StringTokenizer st = new StringTokenizer(union_ds.reads.get(i).name,"_");
            String s1 = st.nextToken();
            int bestHaplo = Integer.parseInt(s1.substring(1));
            clids.add(bestHaplo);
/*            if (!indMap.containsKey(bestHaplo))
            {
                indMap.put(bestHaplo, indMap.size());
            }
            bestHaplo = indMap.get(bestHaplo); */

            clusters[i] = bestHaplo;
            while (st.hasMoreTokens())
                s1 = st.nextToken();
            int freq = Integer.parseInt(s1);
            union_ds.reads.get(i).setFrequency(freq);
        }
        
/*        File fl = new File("reads_clustered.fas");
        fl.delete();
        fl = new File("union_reads.fas");
        fl.delete();*/
                
        
        HashMap<Integer,HashSet<Integer>> clustPools = new HashMap();
        for (Integer i : clids)
            clustPools.put(i, new HashSet());
        
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            boolean b1 = this.ds.containRead(union_ds.reads.get(i));
            boolean b2 = p.ds.containRead(union_ds.reads.get(i));
            try
            {
            if (b1)
                clustPools.get(clusters[i]).add(1);
            if (b2)
                clustPools.get(clusters[i]).add(2);       
            }
            catch (java.lang.NullPointerException e)
            {
                System.out.println(i);
                System.out.println(clusters[i]);
                System.out.println();
                e.printStackTrace();
            }
        }
        
        
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        HashMap<String,String> seqNames = new HashMap<String, String>();
        for (int i = 0; i < union_ds.reads.size(); i++)
            if (clustPools.get(clusters[i]).size() == 2)
            {
                Read r = union_ds.reads.get(i);
                StringTokenizer st = new StringTokenizer(r.name,"_");
                int pref_len = st.nextToken().length();
                pref_len += st.nextToken().length();
                String newName = r.name.substring(pref_len+2);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                seqNames.put(r.getNucl(), newName);
            }
        
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            inter.ds.reads.add(new Read(nucleo,seqNames.get(nucleo),f));
            count++;
	}
        
        
        return inter;
    }
    public Pool intersectKGEM(Pool p, int clustCoeff, String kGEMfolder) throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.setFileName("i_" + this.fileName + "_" + p.fileName + ".fas");
        
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
        PoolsOperator po = new PoolsOperator();   
        Pool un = this.union(p);
        un.printToFile(kGEMfolder + File.separator + un.fileName);
        un.ds.PrintUniqueReads(kGEMfolder + File.separator + un.fileName + "_unique.fas");
        
        
//        un.cluster(clustCoeff);
        un.clusterExtAlign(clustCoeff,kGEMfolder);
        
        String fold = kGEMfolder + File.separator + "clust_" + un.fileName;
        
        File fl = new File(fold + File.separator + "reads_clustered.fas");
        boolean b = fl.exists();
        
        int iters = 0;
        while (!b)
        {
            System.out.println("Waiting for kGEM:" + (iters++));
            Thread.sleep(1000);
            b = fl.exists();
        }
        
        
        DataSet union_ds = new DataSet(fold + File.separator + "reads_clustered.fas",'c');
        HashSet<Integer> clids = new HashSet();
        
        int[] clusters = new int[union_ds.reads.size()];
//        Map<Integer, Integer> indMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            StringTokenizer st = new StringTokenizer(union_ds.reads.get(i).name,"_");
            String s1 = st.nextToken();
            int bestHaplo = Integer.parseInt(s1.substring(1));
            clids.add(bestHaplo);
/*            if (!indMap.containsKey(bestHaplo))
            {
                indMap.put(bestHaplo, indMap.size());
            }
            bestHaplo = indMap.get(bestHaplo); */

            clusters[i] = bestHaplo;
            while (st.hasMoreTokens())
                s1 = st.nextToken();
            int freq = Integer.parseInt(s1);
            union_ds.reads.get(i).setFrequency(freq);
        }
        
/*        File fl = new File("reads_clustered.fas");
        fl.delete();
        fl = new File("union_reads.fas");
        fl.delete();*/
                
        
        HashMap<Integer,HashSet<Integer>> clustPools = new HashMap();
        for (Integer i : clids)
            clustPools.put(i, new HashSet());
        
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            boolean b1 = this.ds.containRead(union_ds.reads.get(i));
            boolean b2 = p.ds.containRead(union_ds.reads.get(i));
            try
            {
            if (b1)
                clustPools.get(clusters[i]).add(1);
            if (b2)
                clustPools.get(clusters[i]).add(2);       
            }
            catch (java.lang.NullPointerException e)
            {
                System.out.println(i);
                System.out.println(clusters[i]);
                System.out.println();
                e.printStackTrace();
            }
        }
        
        
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        HashMap<String,String> seqNames = new HashMap<String, String>();
        for (int i = 0; i < union_ds.reads.size(); i++)
            if (clustPools.get(clusters[i]).size() == 2)
            {
                Read r = union_ds.reads.get(i);
                StringTokenizer st = new StringTokenizer(r.name,"_");
                int pref_len = st.nextToken().length();
                pref_len += st.nextToken().length();
                String newName = r.name.substring(pref_len+2);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                seqNames.put(r.getNucl(), newName);
            }
        
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            inter.ds.reads.add(new Read(nucleo,seqNames.get(nucleo),f));
            count++;
	}
        
        
        return inter;
    }
    public Pool intersect(Pool p, int clustCoeff, String kGEMfolder, String alignMethod, String clustMethod) throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.setFileName("i_" + this.fileName + "_" + p.fileName + ".fas");
        
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
         if (this.isEmpty() && p.isEmpty())
         {
             return inter;
         }  
             
        PoolsOperator po = new PoolsOperator();   
        Pool un = this.union(p);
        un.printToFile(kGEMfolder + File.separator + un.fileName);
        un.ds.PrintUniqueReads(kGEMfolder + File.separator + un.fileName + "_unique.fas");
        
//        DataSet auxds = new DataSet(kGEMfolder + File.separator + un.fileName);
//        auxds.PrintUniqueReads(kGEMfolder + File.separator + un.fileName + "_unique_aux.fas");
        
//        un.cluster(clustCoeff);
        un.clusterExtAlign(clustCoeff,kGEMfolder,alignMethod, clustMethod);
        
        String fold = kGEMfolder + File.separator + "clust_" + un.fileName;
        
        File fl = new File(fold + File.separator + "reads_clustered.fas");
        boolean b = fl.exists();
        
        int iters = 0;
        while (!b)
        {
            System.out.println("Waiting for clustering:" + (iters++));
            Thread.sleep(1000);
            b = fl.exists();
        }
        
        
        DataSet union_ds = new DataSet(fold + File.separator + "reads_clustered.fas",'c');
        HashSet<Integer> clids = new HashSet();
        
        int[] clusters = new int[union_ds.reads.size()];
//        Map<Integer, Integer> indMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            StringTokenizer st = new StringTokenizer(union_ds.reads.get(i).name,"_");
            String s1 = st.nextToken();
            int bestHaplo = Integer.parseInt(s1.substring(1));
            clids.add(bestHaplo);
/*            if (!indMap.containsKey(bestHaplo))
            {
                indMap.put(bestHaplo, indMap.size());
            }
            bestHaplo = indMap.get(bestHaplo); */

            clusters[i] = bestHaplo;
            while (st.hasMoreTokens())
                s1 = st.nextToken();
            int freq = Integer.parseInt(s1);
            union_ds.reads.get(i).setFrequency(freq);
        }
        
/*        File fl = new File("reads_clustered.fas");
        fl.delete();
        fl = new File("union_reads.fas");
        fl.delete();*/
                
        
        HashMap<Integer,HashSet<Integer>> clustPools = new HashMap();
        for (Integer i : clids)
            clustPools.put(i, new HashSet());
        
        for (int i = 0; i < union_ds.reads.size(); i++)
        {
            boolean b1 = this.ds.containRead(union_ds.reads.get(i));
            boolean b2 = p.ds.containRead(union_ds.reads.get(i));
            try
            {
            if (b1)
                clustPools.get(clusters[i]).add(1);
            if (b2)
                clustPools.get(clusters[i]).add(2);       
            }
            catch (java.lang.NullPointerException e)
            {
                System.out.println(i);
                System.out.println(clusters[i]);
                System.out.println();
                e.printStackTrace();
            }
        }
        
        
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        HashMap<String,String> seqNames = new HashMap<String, String>();
        for (int i = 0; i < union_ds.reads.size(); i++)
            if (clustPools.get(clusters[i]).size() == 2)
            {
                Read r = union_ds.reads.get(i);
                StringTokenizer st = new StringTokenizer(r.name,"_");
                int pref_len = st.nextToken().length();
                pref_len += st.nextToken().length();
                String newName = r.name.substring(pref_len+2);
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                seqNames.put(r.getNucl(), newName);
            }
        
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            inter.ds.reads.add(new Read(nucleo,seqNames.get(nucleo),f));
            count++;
	}
        
        
        return inter;
    }
    // </editor-fold>
    public Pool intersectExact(Pool p)
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.setFileName("i_" + this.fileName + "_" + p.fileName + ".fas");
        
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        
/*        for (Read r : p.ds.reads)
            if (this.ds.containRead(r))
                inter.ds.reads.add(r);*/
        
        for (Read r : this.ds.reads)
            if (p.ds.containRead(r))
                inter.ds.reads.add(r);
        
        return inter;
    }
    public Pool intersectCentroids(Pool p) throws IOException
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(this.indsamples);
        inter_indsamples.retainAll(p.indsamples);
        inter.setIndSample(inter_indsamples);
        inter.centroidsClusters = new HashMap();
        
        int k = this.indsamples.size() + p.indsamples.size() - inter.indsamples.size();
        DataSet inter_ds = new DataSet();
        inter.setDS(inter_ds);
        ArrayList<ReadsPair> centrPairs = new ArrayList();
        Iterator ir1 = this.centroidsClusters.entrySet().iterator();
        while (ir1.hasNext())
        {
            Map.Entry me1 = (Map.Entry) ir1.next();
            Read c1 = (Read) me1.getKey();
            Iterator ir2 = p.centroidsClusters.entrySet().iterator();
            while (ir2.hasNext())
            {
                Map.Entry me2 = (Map.Entry) ir2.next();
                Read c2 = (Read) me2.getKey();
                centrPairs.add(new ReadsPair(c1,c2));
            }
            
        }
        HashMap<String,Integer> seq = new HashMap<String, Integer>();
        ReadsPairsComp rpc = new ReadsPairsComp();
        Collections.sort(centrPairs, rpc);  
        for (int i = 0; i < inter.indsamples.size(); i++)
        {
            Read c1 = centrPairs.get(i).r1;
            Read c2 = centrPairs.get(i).r2;
            HashMap<String,Integer> seq_clust = new HashMap<String, Integer>();
            for (Read r : this.centroidsClusters.get(c1).reads)
            {
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                
                if (seq_clust.containsKey(r.getNucl()))
                    seq_clust.put(r.getNucl(), seq_clust.get(r.getNucl())+r.getFreq());
		else
                    seq_clust.put(r.getNucl(), r.getFreq());
            }
            for (Read r : p.centroidsClusters.get(c2).reads)
            {
                if (seq.containsKey(r.getNucl()))
                    seq.put(r.getNucl(), seq.get(r.getNucl())+r.getFreq());
		else
                    seq.put(r.getNucl(), r.getFreq());
                
                if (seq_clust.containsKey(r.getNucl()))
                    seq_clust.put(r.getNucl(), seq_clust.get(r.getNucl())+r.getFreq());
		else
                    seq_clust.put(r.getNucl(), r.getFreq());	
            }
            DataSet ds_clust = new DataSet();
            Iterator it = seq_clust.entrySet().iterator();
            int count = 1;
            while (it.hasNext())
            {
                Map.Entry me = (Map.Entry)it.next();
                String nucleo = (String) me.getKey(); 
                int f = (Integer) me.getValue();
                String nm = "read" + count;
                ds_clust.reads.add(new Read(nucleo,nm,f));
                count++;
            }
            inter.centroidsClusters.put(c1.findAverage(c2, 15, 6), ds_clust);
            
        }
        Iterator it = seq.entrySet().iterator();
	int count = 1;
	while (it.hasNext())
	{
            Map.Entry me = (Map.Entry)it.next();
            String nucleo = (String) me.getKey(); 
            int f = (Integer) me.getValue();
            String nm = "read" + count;
            inter.ds.reads.add(new Read(nucleo,nm,f));
            count++;
	}
 
        inter.setName();
        return inter;
        
    }
    void setName()
    {
         String s = "Pool";
        for (int i : this.indsamples)
            s+="_" + i;
        this.fileName = s+".fas";
    }
    void printDistanceMatrix(String addr) throws IOException
    {
             FileWriter fw = new FileWriter(addr);
             for (int l = 0; l < distMatr.length; l++)
             {
                 for (int j = 0; j < distMatr.length; j++)
                     fw.write(distMatr[l][j] + " ");
                 fw.write("\n");
             }
             fw.close(); 
    }
    void readDistMatr(String addr) throws FileNotFoundException, IOException
    {
        int n = 0;
        BufferedReader br = new BufferedReader(new FileReader(addr));
        String s = "";
        s = br.readLine();
        while (s!= null)
        {
            n++;
            s = br.readLine();
        }
        br.close();
        
        distMatr = new double[n][n];
        
        br = new BufferedReader(new FileReader(addr));
        s = "";
        int i = 0;
        s = br.readLine();
        while (s!= null)
        {
            System.out.println("Reading line " + (i+1) + " of " + n);
            StringTokenizer st = new StringTokenizer(s," \n");
            int j = 0;
            while (st.hasMoreTokens())
            {
                distMatr[i][j] = Double.parseDouble(st.nextToken());
                j++;
            }
            i++;
            s = br.readLine();
        }
        br.close();
     }
     void compareWithRef(String refFile, String nameTag) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        DataSet refds = new DataSet(refFile,'c');        
        FileWriter fw = new FileWriter(nameTag + "_stat_ref.txt");
        for (int i = 0; i < ds.reads.size(); i++)
        {
            System.out.println("Read " + i + " of " + ds.reads.size());
            double mindist = Double.MAX_VALUE;
            Read minRef = null;
            for (Read r : refds.reads)
            {
                double d = ds.reads.get(i).calcEditDistAlign(r,15,6);
//                double d = ds.reads.get(i).calcEditDistKmer(r);
                if (d < mindist)
                {
                    mindist = d;
                    minRef = r;
                }
            }
            if (mindist < 0)
                System.out.println();
            fw.write(i + " " + ds.reads.get(i).getLength() + " " + minRef.name + " " + mindist + "\n");
        }
        fw.close();
        
    }
    // </editor-fold>
     void compareWithRefPrintLabelledReads(String refFile, String nameTag) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        DataSet refds = new DataSet(refFile,'c');        
//        FileWriter fw = new FileWriter(nameTag + "_stat_ref.txt");
        FileWriter fw_seq = new FileWriter(nameTag + "_label.fas");
        for (int i = 0; i < ds.reads.size(); i++)
        {
            System.out.println("Read " + i + " of " + ds.reads.size());
            double mindist = Double.MAX_VALUE;
            Read minRef = null;
            for (Read r : refds.reads)
            {
                double d = ds.reads.get(i).calcEditDistAlign(r, 15, 6);
//                double d = ds.reads.get(i).calcEditDistAbsAlign(r);
//                double d = ds.reads.get(i).calcEditDistKmer(r);
                if (d < mindist)
                {
                    mindist = d;
                    minRef = r;
                }
            }
//            fw.write(i + " " + minRef.name + " " + mindist + "\n");
            fw_seq.write(">" + minRef.name + "\n" + ds.reads.get(i).getNucl() + "\n");
        }
//        fw.close();
        fw_seq.close();
        
    }
    // </editor-fold>
      void compareWithRefKmers(String refFile, String nameTag) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        DataSet refds = new DataSet(refFile,'c');        
        FileWriter fw = new FileWriter(nameTag + "_stat_ref.txt");
        int k = 25;
        ds.setK(k);
        ds.calculateKMersAndKCounts();
        refds.setK(k);
        refds.calculateKMersAndKCounts();
        for (int i = 0; i < ds.reads.size(); i++)
        {
            System.out.println("Read " + i + " of " + ds.reads.size());
            double mindist = Double.MAX_VALUE;
            Read minRef = null;
            for (Read r : refds.reads)
            {
                double d = ds.reads.get(i).calcEditDistKmer(r);
                if (d < mindist)
                {
                    mindist = d;
                    minRef = r;
                }
            }
            fw.write(i + " " + minRef.name + " " + mindist + "\n");
        }
        fw.close();
        
    }
    // </editor-fold>
      Pool minus(Pool p) throws IOException
      {
            Pool minus = new Pool();
            ArrayList<Integer> minus_indsamples = new ArrayList(this.indsamples);
            minus_indsamples.removeAll(p.indsamples);
            minus.setIndSample(minus_indsamples);
            minus.setFileName("m_" + this.fileName + "_" + p.fileName + ".fas");

            DataSet minus_ds = new DataSet();
            
            minus_ds.reads = new ArrayList();
            int i = 1;
            for (Read r : this.ds.reads)
            {
                System.out.println("Checking read " + i + " of " + this.ds.reads.size());
                if (!p.ds.containRead(r))
                    minus_ds.reads.add(r);
                i++;
            }
            
            
            minus.setDS(minus_ds);
            return minus;
      }
      Pool minusCentroids(Pool p) throws IOException
      {
            Pool minus = new Pool();
            ArrayList<Integer> minus_indsamples = new ArrayList(this.indsamples);
            minus_indsamples.removeAll(p.indsamples);
            minus.setIndSample(minus_indsamples);

            DataSet minus_ds = new DataSet();
            
            minus_ds.reads = new ArrayList();
            int i = 1;
            for (Read r : this.ds.reads)
            {
                System.out.println("Checking read " + i + " of " + this.ds.reads.size());
                if (!p.ds.containRead(r))
                    minus_ds.reads.add(r);
                i++;
            }
            
            minus.setDS(minus_ds);
            minus.centroidsClusters = new HashMap(this.centroidsClusters);
            ArrayList<Read> centroidsRemove = new ArrayList();
            Iterator ir = minus.centroidsClusters.entrySet().iterator();
            while (ir.hasNext())
            {
                Map.Entry me = (Map.Entry) ir.next();
                Read c = (Read) me.getKey();
                DataSet ds = (DataSet) me.getValue();
                boolean toDel = false;
                for (Read r : ds.reads)
                    if (!minus.ds.containRead(r))
                    {
                        toDel = true;
                        break;
                    }
                if (toDel)
                    centroidsRemove.add(c);
            }
            for (Read c : centroidsRemove)
                minus.centroidsClusters.remove(c);
            minus.setName();
            return minus;
      }
      void removeReads(ArrayList<Read> ar)
      {
          for (Read r : ar)
              if (this.ds.containRead(r))
                      this.ds.reads.remove(r);
      }
      void calcNearestNeighbDist(String addr) throws IOException
      {
          FileWriter fw = new FileWriter(addr);
          for (int i = 0; i < ds.reads.size(); i++)
          {
              double mindist = Double.MAX_VALUE;
              for (int j = 0; j < ds.reads.size(); j++)
                  if (j != i)
                      if (this.distMatr[i][j] < mindist)
                          mindist = this.distMatr[i][j];
              fw.write(i + " " + mindist + "\n");
          }
          fw.close();
      }
      void filterDistNearNeighb(int k)
      {
          ArrayList<Read> badReads = new ArrayList();
          double[] mindists = new double[ds.reads.size()];
          DescriptiveStatistics stats = new DescriptiveStatistics();
          for (int i = 0; i < ds.reads.size(); i++)
          {
//              System.out.println(i);
              double mindist = Double.MAX_VALUE;
              for (int j = 0; j < ds.reads.size(); j++)
                  if (j != i)
                  {
                      int dist = ds.reads.get(i).calcHammingDist(ds.reads.get(j));
                      if (dist < mindist)
                          mindist = dist;
                  }
              mindists[i] = mindist;
              stats.addValue(mindist);
          }
          double mean = stats.getMean();
          double std = stats.getStandardDeviation();
          double thr = mean + k*std;
          for (int i = 0; i < ds.reads.size(); i++)
              if (mindists[i] >= thr)
                  badReads.add(ds.reads.get(i));
          ds.reads.removeAll(badReads);
          System.out.println("Removed: " + badReads.size());
      }
      void clusteringKGEM(int nClust, String addr) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        Runtime run=Runtime.getRuntime();
        
        
        String s = "java -jar ERIF-1.0.jar -g ref.fas -i " + addr + " -noHashing";

        Process p = Runtime.getRuntime().exec(s);
        
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
         while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
         }
        p.waitFor();
         if (p.exitValue()!= 0)
         {
            System.out.println("Error in ERIF");
         }
        
//         DataSet x = new DataSet("aligned_reads.fas");
//        x.PrintUniqueReads("aligned_reads_unique.fas");
         
//        s = "java -jar kgem-0.3.1.jar aligned_reads.fas " +  nClust + " -c union_reads.fas" ;
        s = "java -jar kgem-0.4.2.jar aligned_reads.fas " +  nClust + " -c union_reads.fas" ;
        p = Runtime.getRuntime().exec(s);
        stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((s = stdInput.readLine()) != null) {
           System.out.println(s);
        }
        p.waitFor();
        if (p.exitValue()!= 0)
        {
           System.out.println("Error in kGEM");
        }

        
        File f = new File("haplotypes.fas");
        f.delete();
        f = new File("haplotypes_cleaned.fas");
//        f.delete();
        f = new File("reads.fas");
        f.delete();
        f = new File("reads_cleaned.fas");
        f.delete();
        f = new File("reads.sam");
        f.delete();
        f = new File("reads.sam_ext.txt");
        f.delete();
        f = new File("ref.fas_ext.fasta");
        f.delete();
        f = new File("trash.fasta");
        f.delete();
    }
      void clusterFindCentroids() throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterfly(this.indsamples.size(), this.fileName);
            DataSet centroids = new DataSet("clust_" + this.fileName + File.separator + "haplotypes_cleaned.fas",'c');
            HashMap<Integer,Read> centrNumbers = new HashMap();
            this.centroidsClusters = new HashMap();
            for (Read r : centroids.reads)
            {
                this.centroidsClusters.put(r, new DataSet());
                StringTokenizer st = new StringTokenizer(r.name,"_");
                int n = Integer.parseInt(st.nextToken().substring(9));
                centrNumbers.put(n, r);
            }
            DataSet clustered_ds = new DataSet("clust_" + this.fileName + File.separator + "reads_clustered.fas",'c');
        

            for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
                System.out.println(clustered_ds.reads.get(i).name);
                String s1 = st.nextToken();
                int bestHaplo = Integer.parseInt(s1.substring(1));

                while (st.hasMoreTokens())
                    s1 = st.nextToken();
                int freq = Integer.parseInt(s1);
                clustered_ds.reads.get(i).setFrequency(freq);
                this.centroidsClusters.get(centrNumbers.get(bestHaplo)).reads.add(clustered_ds.reads.get(i));
            }
            this.ds = new DataSet();
            Iterator ir = this.centroidsClusters.entrySet().iterator();
            while (ir.hasNext())
            {
                Map.Entry me = (Map.Entry) ir.next();
                this.ds.reads.addAll(((DataSet) me.getValue()).reads);
            }
      }
      void cluster() throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterfly(this.indsamples.size(), this.fileName);
      }
       void cluster(int clustCoeff) throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterfly(clustCoeff*this.indsamples.size(), this.fileName);
      }
      void clusterExtAlign() throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterflyExtAlign(this.indsamples.size(), this.fileName);
      }
      void clusterExtAlign(int clustCoeff) throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterflyExtAlign(clustCoeff*this.indsamples.size(), this.fileName, "Nothing");
      }
      void clusterExtAlign(int clustCoeff, String kGEMfolder) throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.setOutdir(kGEMfolder);
            po.clusteringButterflyExtAlign(clustCoeff*this.indsamples.size(), this.fileName, "Nothing");
//            po.clusteringButterflyExtAlign(clustCoeff*this.indsamples.size(), this.fileName, "Muscle");
      }
      public void clusterExtAlign(int clustCoeff, String kGEMfolder, String alignMethod, String clustMethod) throws IOException, InterruptedException
      {
            PoolsOperator po = new PoolsOperator();
            po.setOutdir(kGEMfolder);
            
            if (clustMethod.equalsIgnoreCase("kGEM"))
                po.clusteringButterflyExtAlign(clustCoeff*this.indsamples.size(), this.fileName, alignMethod);
            if (clustMethod.equalsIgnoreCase("Matlab"))
                po.clusteringMatlabExtAlign(clustCoeff*this.indsamples.size(), this.fileName, alignMethod);
      }
      void clusterMatlabIndelFixer() throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        
        PoolsOperator po = new PoolsOperator();
        DataSet alignment = new DataSet("clust_" + this.fileName + File.separator + "aligned_reads.fas",'c');
        int n = alignment.reads.size();
        double[][] matr = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
            {
                String si = alignment.reads.get(i).getNucl();
                String sj = alignment.reads.get(j).getNucl();
                for (int l = 0; l < si.length(); l++)
                    if (si.charAt(l) != sj.charAt(l))
                        matr[i][j]++;
                matr[j][i] = matr[i][j];
            }        
        FileWriter fw = new FileWriter("Union_distMatr.txt");
        for (int l = 0; l < matr.length; l++)
        {
            for (int j = 0; j < matr.length; j++)
                fw.write(matr[l][j] + " ");
            fw.write("\n");
        }
        fw.close();
                
        po.clusteringMatlab(this.indsamples.size());
        int[] clusters = new int[n];
        String clustFile = "Union_clusters.txt";
        BufferedReader br = new BufferedReader(new FileReader(clustFile));
        for (int i = 0; i < clusters.length; i++)
            clusters[i] = Integer.parseInt(br.readLine())-1;
        br.close();
        
        File fl = new File("Union_clusters.txt");
        fl.delete();
        
        for (int i = 0; i < alignment.reads.size(); i++)
        {
            alignment.reads.get(i).name = "c" + clusters[i] + "_h1_" + alignment.reads.get(i).name;
            alignment.reads.get(i).setFrequency(1);
        }
        
        alignment.PrintReads("clust_" + this.fileName + File.separator + "reads_clustered_matlab.fas");
        
        
  
    }
      void clusterMatlab() throws IOException, InterruptedException
             // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        int n = distMatr.length;
        PoolsOperator po = new PoolsOperator();
        FileWriter fw = new FileWriter("Union_distMatr.txt");
        for (int l = 0; l < distMatr.length; l++)
        {
            for (int j = 0; j < distMatr.length; j++)
                fw.write(distMatr[l][j] + " ");
            fw.write("\n");
        }
        fw.close();
                
        po.clusteringMatlab(this.indsamples.size());
        int[] clusters = new int[n];
        String clustFile = "Union_clusters.txt";
        BufferedReader br = new BufferedReader(new FileReader(clustFile));
        for (int i = 0; i < clusters.length; i++)
            clusters[i] = Integer.parseInt(br.readLine())-1;
        br.close();
        
        File fl = new File("Union_clusters.txt");
        fl.delete();
        
        for (int i = 0; i < this.ds.reads.size(); i++)
        {
            this.ds.reads.get(i).name = "c" + clusters[i] + "_h1_" + this.ds.reads.get(i).name;
//            this.ds.reads.get(i).setFrequency(1);
        }
        
        this.ds.PrintReads("clust_" + this.fileName + File.separator + "reads_clustered_matlab.fas");
        
        
  
    }
    // </editor-fold>
      public boolean isEmpty()
      {
          if (this.ds.reads.size() == 0)
              return true;
          else
              return false;
      }

}
