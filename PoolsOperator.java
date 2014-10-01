package Pooling;


import Pooling.Pool;
import ErrorCorrection.*;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pavel
 */
public class PoolsOperator {
    String outdir;
    public PoolsOperator()
    {
    }
    Pool intersection (Pool p1, Pool p2)
    {
        Pool inter = new Pool();
        ArrayList<Integer> inter_indsamples = new ArrayList(p1.indsamples);
        inter_indsamples.retainAll(p2.indsamples);
        inter.setIndSample(inter_indsamples);
        
        int k = p1.indsamples.size() + p2.indsamples.size() - inter.indsamples.size();
        
        return inter;
    }
    ArrayList<ArrayList<Integer>> generatePartitions(int n)
    {
        if (n == 2)
        {
            ArrayList<ArrayList<Integer>> partitions = new ArrayList();
            ArrayList<Integer> part1 = new ArrayList();
            part1.add(1);
            partitions.add(part1);
            ArrayList<Integer> part2 = new ArrayList();
            part2.add(2);
            partitions.add(part2);
            return partitions;
        }
        if (n == 1)
        {
            ArrayList<ArrayList<Integer>> partitions = new ArrayList();
            ArrayList<Integer> part1 = new ArrayList();
            part1.add(1);
            partitions.add(part1);
            return partitions;
        }
        ArrayList<ArrayList<Integer>> partitionsPrev = generatePartitions((int) n/2);
        ArrayList<ArrayList<Integer>> partitionsNew = new ArrayList();
        for (ArrayList<Integer> partitionPrev : partitionsPrev)
        {
            ArrayList<Integer> partitionNew = new ArrayList();
            for (Integer i : partitionPrev)
            {
                partitionNew.add(2*i-1);
                partitionNew.add(2*i);
            }
            partitionsNew.add(partitionNew);
        }
        ArrayList<Integer> partitionNew = new ArrayList();
        for (int i = 1; i <= n; i+=2)
            partitionNew.add(i);
        partitionsNew.add(partitionNew);
        return partitionsNew;        
    }
    void renameSequences(String addr, String keyword) throws IOException
    {
        DataSet ds = new DataSet(addr,'c');
        FileWriter fw = new FileWriter(addr+"_renamed.fas");
        int i = 1;
        for (Read r : ds.reads)
        {
            fw.write(">" + i + "_" + keyword + "\n" + r.getNucl() + "\n");
            i++;
        }
        fw.close();            
    }
    double[][] generateDistanceMatrix(Pool p1, Pool p2) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        int n1 = p1.ds.reads.size();
        int n2 = p2.ds.reads.size();
        double[][] distMatr = new double[n1+n2][n1+n2];
        
        for (int i = 0; i < n1; i++)
            for (int j = i+1; j < n1; j++)
            {
                distMatr[i][j] = p1.distMatr[i][j];
                distMatr[j][i] = p1.distMatr[i][j];
            }
        
         for (int i = 0; i < n2; i++)
            for (int j = i+1; j < n2; j++)
            {
                distMatr[n1+i][n1+j] = p2.distMatr[i][j];
                distMatr[n1+j][n1+i] = p2.distMatr[i][j];
            }
        
        for (int i = 0; i < n1; i++)
            for (int j = 0; j < n2; j++)
            {
                System.out.println("Calculate (" + i + "," + j + ") of (" + n1 + "," + n2 + ")");
                distMatr[i][n1+j] = p1.ds.reads.get(i).calcEditDistAlign(p2.ds.reads.get(j),15,6);
//                distMatr[i][n1+j] = p1.ds.reads.get(i).calcEditDistKmer(p2.ds.reads.get(j));
                distMatr[n1+j][i] = distMatr[i][n1+j];           
            }
        
        return distMatr;
    }
    // </editor-fold>
     double[][] generateDistanceMatrixDirect(Pool p1, Pool p2) throws IOException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        int n1 = p1.ds.reads.size();
        int n2 = p2.ds.reads.size();
        double[][] distMatr = new double[n1+n2][n1+n2];
        
        ArrayList<Read> reads = new ArrayList(n1+n2);
        for (int i = 0; i < n1; i++)
            reads.add(p1.ds.reads.get(i));
        
        for (int i = 0; i < n2; i++)
            reads.add(p2.ds.reads.get(i));
        
        for (int i = 0; i < reads.size(); i++)
            for (int j = i; j < reads.size(); j++)
            {
                System.out.println("Calculate (" + i + "," + j + ") of " + reads.size());
                distMatr[i][j] = reads.get(i).calcEditDistAlign(reads.get(j),15,6);
                distMatr[j][i] = distMatr[i][j];           
            }
        
        return distMatr;
    }
    // </editor-fold>
    void clusteringMatlab(int nClust) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        Runtime run=Runtime.getRuntime();
        String s = "matlab -nodesktop -nosplash -r pooling(" + nClust + ")";

        Process p = Runtime.getRuntime().exec(s);
        p.waitFor();
        
        File f = new File("Union_clusters.txt");
        boolean b = f.exists();
        
        int i = 0;
        while (!b)
        {
            System.out.println("Waiting for Matlab:" + (i++));
            Thread.sleep(1000);
            b = f.exists();
        }

        if (p.exitValue()!= 0)
        {
            System.out.println("Error in Matlab script");
        }
    }
    // </editor-fold>
    void clusteringMatlabExtAlign(int nClust, String addr) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        
        System.out.println("Clustering");
        
        String addr_align = addr + "_aligned.fas";
        String[] com = new String[] {"Muscle" + File.separator + "muscle", "-in", addr + "_unique.fas", "-out", addr_align};
                    
        ProcessBuilder pb = new ProcessBuilder(com);
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        InputStream is = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);


        String ss;
        System.out.println("Here is the standard output of the command:\n");
        while ((ss = br.readLine()) != null) {
            System.out.println(ss);
        }
        if (proc.exitValue()!= 0)
            System.out.println("Error in allgnment program");
        
        DataSet ds = new DataSet(addr_align,"ET");
        for (Read r : ds.reads)
        {
            StringTokenizer st = new StringTokenizer(r.name,"_");
            String nm = "";
            int nTok = st.countTokens();
            for (int i = 0; i < nTok - 1; i++)
                nm = nm + st.nextToken() + "_";
            r.name = nm.substring(0, nm.length()-1);
        }
        addr_align = addr_align + "_all.fas";
        ds.PrintReadsNoCopyNumber(addr_align);
        
        
        String s = "matlab -nodesktop -nosplash -r pooling(" + nClust + ")";

        Process p = Runtime.getRuntime().exec(s);
        p.waitFor();
        
        File f = new File("Union_clusters.txt");
        boolean b = f.exists();
        
        int i = 0;
        while (!b)
        {
            System.out.println("Waiting for Matlab:" + (i++));
            Thread.sleep(1000);
            b = f.exists();
        }

        if (p.exitValue()!= 0)
        {
            System.out.println("Error in Matlab script");
        }
    }
    void clusteringMatlabExtAlign(int nClust, String addr, String typeAlign) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        
        System.out.println("Clustering");
        String addr_align = "";
        
        if (typeAlign.equalsIgnoreCase("Muscle"))
        {
        
            addr_align = this.outdir + File.separator + addr + "_aligned.fas";
            String[] com = new String[] {"Muscle" + File.separator + "muscle", "-in", this.outdir + File.separator + addr + "_unique.fas", "-out", addr_align};

            ProcessBuilder pb = new ProcessBuilder(com);
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);


            String ss;
            System.out.println("Here is the standard output of the command:\n");
            while ((ss = br.readLine()) != null) {
                System.out.println(ss);
            }
            if (proc.exitValue()!= 0)
                System.out.println("Error in allgnment program");
        
        }
        if (typeAlign.equalsIgnoreCase("Nothing"))
        {
            addr_align = this.outdir + File.separator + addr + "_unique.fas";
        }
        
        DataSet ds_align = new DataSet(addr_align,"ET");        
        int n = ds_align.reads.size();
        double[][] distMatr = new double[n][n];
        
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
            {
                System.out.println("Calculate (" + i + "," + j + ") of " + n);
                distMatr[i][j] = ds_align.reads.get(i).calcHammingDist(ds_align.reads.get(j));
                distMatr[j][i] = distMatr[i][j];           
            }
        
        String outFolder = this.outdir + File.separator + "clust_" +  addr;
        File folder = new File(outFolder);
        folder.mkdir();
        
        FileWriter fw = new FileWriter(outFolder + File.separator + "distMatr.txt");
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
                fw.write(distMatr[i][j] + " ");
            fw.write("\n");
        }
        fw.close();
        
        
        String s = "matlab -nodesktop -nosplash -r pooling(" + nClust + ",'" + outFolder + "')";

        Process p = Runtime.getRuntime().exec(s);
        p.waitFor();
        
        File f = new File(outFolder + File.separator + "clusters.txt");
        boolean b = f.exists();
        
        int i = 0;
        while (!b)
        {
            System.out.println("Waiting for Matlab:" + (i++));
            Thread.sleep(1000);
            b = f.exists();
        }

        if (p.exitValue()!= 0)
        {
            System.out.println("Error in Matlab script");
        }
        
        fw = new FileWriter(outFolder + File.separator + "reads_clustered.fas");
        BufferedReader br = new BufferedReader(new FileReader(outFolder + File.separator + "clusters.txt"));
        for (i = 0; i < ds_align.reads.size(); i++)
        {
            Read r = ds_align.reads.get(i);
            r.delGaps();
            int c = Integer.parseInt(br.readLine());
            String newName = "c" + c + "_h" + c + "_" + r.name;
            r.name = newName;
            fw.write(">" + r.name + "\n" + r.getNucl() + "\n");
        }
        br.close();
        fw.close();
        System.out.println();

        
        
        
    }
    // </editor-fold>
    void clusteringKGEM(int nClust) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        Runtime run=Runtime.getRuntime();
        
        
        String s = "java -jar ERIF-1.0.jar -g ref.fas -i union_reads.fas -noHashing";

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
        s = "java -jar kgem-0.4.2.jar aligned_reads.fas " +  nClust + " -c " + addr ;
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
        f.renameTo(new File(addr + "_haplotypes_cleaned.fas"));
        f = new File("reads.fas");
        f.delete();
        f = new File("reads_cleaned.fas");
        f.delete();
        f = new File("aligned_reads.fas");
        f.delete();
        f = new File("reads.sam");
        f.delete();
        f = new File("reads.sam_ext.txt");
        f.delete();
        f = new File("ref.fas_ext.fasta");
        f.delete();
        f = new File("trash.fasta");
        f.delete();
        f = new File("reads_clustered.fas");
        f.renameTo(new File(addr + "_reads_clustered.fas"));
    }
    void clusteringButterfly(int nClust, String addr) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        Runtime run=Runtime.getRuntime();
        
        
//        String s = "java -jar butterfly-1.0.jar -i " + addr +  " -g ref.fas -k " + nClust + " -o clust_" +  addr;
        String s = "java -jar butterfly-1.0.1.jar -i " + addr +  " -g ref.fas -k " + nClust + " -o clust_" +  addr;
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
    }
    void clusteringHcukk(int nClust, String addr) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        Runtime run=Runtime.getRuntime();
        
        File dir = new File("clust_" +  addr);
        dir.mkdir();
        
//        String s = "java -jar butterfly-1.0.jar -i " + addr +  " -g ref.fas -k " + nClust + " -o clust_" +  addr;
        String outfile = "clust_" +  addr + File.separator + "reads_clustered.fas";
        String s = "java -jar hcukk-1.0.jar -in " + addr + " -k " + nClust + " -o " + outfile;

        System.out.println(s);

        Process p = Runtime.getRuntime().exec(s);
        
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
         while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
         }
        p.waitFor();
         if (p.exitValue()!= 0)
         {
            System.out.println("Error in hcukk");
         }
    }
    void clusteringButterflyExtAlign(int nClust, String addr) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        
        
        String addr_align = addr + "_aligned.fas";
        String[] com = new String[] {"Muscle" + File.separator + "muscle", "-in", addr + "_unique.fas", "-out", addr_align};
                    
        ProcessBuilder pb = new ProcessBuilder(com);
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        InputStream is = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);


        String ss;
        System.out.println("Here is the standard output of the command:\n");
        while ((ss = br.readLine()) != null) {
            System.out.println(ss);
        }
        if (proc.exitValue()!= 0)
            System.out.println("Error in allgnment program");
        
        DataSet ds = new DataSet(addr_align,"ET");
        for (Read r : ds.reads)
        {
            StringTokenizer st = new StringTokenizer(r.name,"_");
            String nm = "";
            int nTok = st.countTokens();
            for (int i = 0; i < nTok - 1; i++)
                nm = nm + st.nextToken() + "_";
            r.name = nm.substring(0, nm.length()-1);
        }
        addr_align = addr_align + "_all.fas";
        ds.PrintReadsNoCopyNumber(addr_align);

        
        String s = "java -jar kgem-clustering-0.5c.jar " +  addr_align + " " +  nClust +  " -c " +  addr +  " -o clust_" +  addr;


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
    }
    void clusteringButterflyExtAlign(int nClust, String addr, String typeAlign) throws IOException, InterruptedException
            // <editor-fold defaultstate="collapsed" desc=" DESCRIPTION ">
    {
        System.out.println("Clustering");
        
        String addr_align = "";
        
        if (typeAlign.equalsIgnoreCase("Muscle"))
        {
        
            addr_align = this.outdir + File.separator + addr + "_aligned.fas";
            String[] com = new String[] {"Muscle" + File.separator + "muscle", "-in", this.outdir + File.separator + addr + "_unique.fas", "-out", addr_align};

            ProcessBuilder pb = new ProcessBuilder(com);
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);


            String ss;
            System.out.println("Here is the standard output of the command:\n");
            while ((ss = br.readLine()) != null) {
                System.out.println(ss);
            }
            if (proc.exitValue()!= 0)
                System.out.println("Error in allgnment program");
            
            DataSet ds = new DataSet(addr_align,"ET");
            for (Read r : ds.reads)
            {
                StringTokenizer st = new StringTokenizer(r.name,"_");
                String nm = "";
                int nTok = st.countTokens();
                for (int i = 0; i < nTok - 1; i++)
                    nm = nm + st.nextToken() + "_";
                r.name = nm.substring(0, nm.length()-1);
            }
            addr_align = addr_align + "_all.fas";
            ds.PrintReads(addr_align);
        }
        if (typeAlign.equalsIgnoreCase("Nothing"))
        {
            addr_align = this.outdir + File.separator + addr;
        }
        


        String s = "java -jar kgem-clustering-0.5c.jar " +  addr_align + " " +  nClust +  " -c " + this.outdir + File.separator + addr +  " -o " + this.outdir + File.separator + "clust_" +  addr;

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
    }
    ArrayList<sampRecovAction> calcRecovActionsRecursive(int n)
    {
        ArrayList <sampRecovAction> ar = new ArrayList();
        if (n == 2)
        {
            ar.add(new sampRecovAction(0,0,"i"));
            ar.add(new sampRecovAction(1,1,"i"));
            return ar;
        }
        if (n == 1)
        {
            ar.add(new sampRecovAction(0,0,"i"));
            return ar;
        }
        int n_prev = (int) (n/2);
        ArrayList<sampRecovAction> ar_prev = this.calcRecovActionsRecursive(n_prev);
//        int m = 31 - Integer.numberOfLeadingZeros(n);
        for (int i = 0; i < ar_prev.size(); i++)
            ar.add(ar_prev.get(i));
        ar.add(new sampRecovAction(ar.size(),ar.size(),"i"));
        int i_lastPool = ar.size()-1;
        for (int i = i_lastPool-n_prev; i < i_lastPool; i++)
        {
            ar.add(new sampRecovAction(i,i_lastPool,"i"));
            ar.add(new sampRecovAction(i,ar.size()-1,"d"));
        }
        return ar;
    }
    ArrayList<sampRecovAction> calcRecovActionsGeneral(ArrayList<ArrayList<Integer>> partitions)
    {
        ArrayList <sampRecovAction> ar = new ArrayList();
        LinkedList<Integer> numeration = new LinkedList();
        for (int i = 0; i < partitions.size(); i++)
        {
            ar.add(new sampRecovAction(i,i,"i"));
            numeration.add(i);
        }
        LinkedList<ArrayList<Integer>> ls= new LinkedList(partitions);
                
/*        for (int i = 0; i < ls.size(); i++)
        {
            System.out.print(numeration.get(i) + ": ");
            for (Integer j : ls.get(i))
                System.out.print(j + " ");
            System.out.println();
        }*/
        int nAct = partitions.size();
            
        while (ls.size() > 0)
        {
            ArrayList<Integer> samp1 = ls.remove();
            int indSamp1 = numeration.remove(); 
            for (int i = 0; i < ls.size(); i++)
            {
                ArrayList<Integer> samp2 = ls.get(i);
                int indSamp2 = numeration.get(i); 
                ArrayList<Integer> inter = new ArrayList(samp1);
                inter.retainAll(samp2);
                if (inter.size() > 0)
                {
                    ArrayList<Integer> diff1 = new ArrayList(samp1);
                    diff1.removeAll(samp2);
                    ArrayList<Integer> diff2 = new ArrayList(samp2);
                    diff2.removeAll(samp1);
                    
                    ls.remove(i);
                    numeration.remove(i);
                    ls.add(inter);
                    numeration.add(nAct++);
                    ar.add(new sampRecovAction(indSamp1,indSamp2,"i"));
                    int nInter = ar.size() - 1;
                    int s = 0;
                    if (diff1.size() > 0)
                    {
                        ls.add(diff1);
                        numeration.add(nAct++);
                        ar.add(new sampRecovAction(indSamp1,nInter,"d"));
                        s++;
                    }
                    if (diff2.size() > 0)
                    {
                        ls.add(diff2);
                        numeration.add(nAct++);
                        ar.add(new sampRecovAction(indSamp2,nInter,"d"));
                        s++;
                    }                    
                    
 /*                   for (int j = numeration.size() - 1-s; j < numeration.size(); j++)
                    {
                        System.out.print(numeration.get(j) + ": ");
                        for (Integer i1 : ls.get(j))
                            System.out.print(i1 + " ");
                        System.out.println();
                    }*/
                    break;
                }
            }
            
        }
        
        System.out.println();
        
        return ar;
    }
    ArrayList<Pool> recover(ArrayList<Pool> pools,ArrayList<sampRecovAction> ar) throws IOException, InterruptedException
    {
        ArrayList<Pool> intsDiffs = new ArrayList();
        int j = 0;
        for (int i = 0; i < ar.size(); i++)
        {
            sampRecovAction ac = ar.get(i);
            if (ac.i == ac.j)
            {
                intsDiffs.add(pools.get(j));
                pools.get(j).fileName = "a" + i + ".fas";
                j++;
                continue;
            }
            if (ac.oper.equalsIgnoreCase("i"))
            {
                Pool p = intsDiffs.get(ac.i).intersectKGEM(intsDiffs.get(ac.j));
                p.fileName = "a" + i + ".fas";
                intsDiffs.add(p);
            }
            if (ac.oper.equalsIgnoreCase("d"))
            {
                Pool p = intsDiffs.get(ac.i).minus(intsDiffs.get(ac.j));
                p.fileName = "a" + i + ".fas";;
                intsDiffs.add(p); 
            }
        }
        return intsDiffs;
    }
    ArrayList<Pool> recover(ArrayList<Pool> pools,ArrayList<sampRecovAction> ar, int clustCoeff) throws IOException, InterruptedException
    {
        ArrayList<Pool> intsDiffs = new ArrayList();
        int j = 0;
        for (int i = 0; i < ar.size(); i++)
        {
            sampRecovAction ac = ar.get(i);
            if (ac.i == ac.j)
            {
                intsDiffs.add(pools.get(j));
                pools.get(j).fileName = "a" + i + ".fas";
                j++;
                continue;
            }
            if (ac.oper.equalsIgnoreCase("i"))
            {
                Pool p = intsDiffs.get(ac.i).intersectKGEM(intsDiffs.get(ac.j), clustCoeff,this.outdir);
                p.fileName = "a" + i + ".fas";
                intsDiffs.add(p);
            }
            if (ac.oper.equalsIgnoreCase("d"))
            {
                Pool p = intsDiffs.get(ac.i).minus(intsDiffs.get(ac.j));
                p.fileName = "a" + i + ".fas";;
                intsDiffs.add(p); 
            }
        }
        return intsDiffs;
    }
    ArrayList<Pool> recover(ArrayList<Pool> pools,ArrayList<sampRecovAction> ar, int clustCoeff, String alignMethod, String clustMethod) throws IOException, InterruptedException
    {
        ArrayList<Pool> intsDiffs = new ArrayList();
        int j = 0;
        for (int i = 0; i < ar.size(); i++)
        {
            sampRecovAction ac = ar.get(i);
            if (ac.i == ac.j)
            {
                intsDiffs.add(pools.get(j));
                pools.get(j).fileName = "a" + i + ".fas";
                j++;
                continue;
            }
            if (ac.oper.equalsIgnoreCase("i"))
            {
                Pool p = intsDiffs.get(ac.i).intersect(intsDiffs.get(ac.j), clustCoeff, this.outdir, alignMethod, clustMethod);
                p.fileName = "a" + i + ".fas";
                intsDiffs.add(p);
            }
            if (ac.oper.equalsIgnoreCase("d"))
            {
                Pool p = intsDiffs.get(ac.i).minus(intsDiffs.get(ac.j));
                p.fileName = "a" + i + ".fas";;
                intsDiffs.add(p); 
            }
        }
        return intsDiffs;
    }
    ArrayList<Pool> getSamps(ArrayList<Pool> intsDiffs)
    {
        ArrayList<Pool> recovSamp = new ArrayList();
        HashMap<Integer,Pool> indRecov = new HashMap();
        for (int i = intsDiffs.size()-1; i >= 0; i--)
        {
            Pool p =intsDiffs.get(i);
            if (p.indsamples.size() == 1)
                if (!indRecov.containsKey(p.indsamples.get(0)))
                        indRecov.put(p.indsamples.get(0), p);
        }
        Iterator ir = indRecov.entrySet().iterator();
        ArrayList<Integer> indSamp = new ArrayList<Integer>();
        while (ir.hasNext())
        {
            Map.Entry me = (Map.Entry) ir.next();
            indSamp.add((Integer) me.getKey());
        }
        Collections.sort(indSamp);
        for (Integer i : indSamp)
            recovSamp.add(indRecov.get(i));        
        return recovSamp;
    }
    void setOutdir(String addr)
    {
        this.outdir = addr;
    }
    ArrayList<Pool> postprocessReseq(ArrayList<Pool> recovSamp, ArrayList<Pool> reseqSamp, int clustCoeff,String alignMethod, String clustMethod) throws IOException, InterruptedException
    {
        for (Pool p : reseqSamp)
            for (Pool s : recovSamp)
            {
                if (!s.isEmpty())
                {
                    Pool inter = p.intersect(s, clustCoeff, this.outdir, alignMethod, clustMethod);
                    if (!inter.isEmpty())
                    {
                        p.add(inter);
                        s.remove(inter);
                        System.out.println();
                    }
                }
            }
        for (int i = 0; i < reseqSamp.size(); i++)
            for (int j = 0; j < recovSamp.size(); j++)
                if (reseqSamp.get(i).indsamples.containsAll(recovSamp.get(j).indsamples))
                {
                    recovSamp.get(j).ds = reseqSamp.get(i).ds;
                    break;
                }
        return recovSamp;
    }
    ArrayList<Pool> postprocessReseq(ArrayList<Pool> recovSamp, ArrayList<Pool> reseqSamp, int clustCoeff,String alignMethod, String clustMethod, int minReads) throws IOException, InterruptedException
    {
        for (Pool p : reseqSamp)
            for (Pool s : recovSamp)
            {
                if (s.ds.getTotalNReads() > minReads)
                {
                    Pool inter = p.intersect(s, clustCoeff, this.outdir, alignMethod, clustMethod);
                    if (!inter.isEmpty())
                    {
                        p.add(inter);
                        s.remove(inter);
                        System.out.println();
                    }
                }
            }
        for (int i = 0; i < reseqSamp.size(); i++)
            for (int j = 0; j < recovSamp.size(); j++)
                if (reseqSamp.get(i).indsamples.containsAll(recovSamp.get(j).indsamples))
                {
                    recovSamp.get(j).ds = reseqSamp.get(i).ds;
                    break;
                }
        return recovSamp;
    }
    boolean checkDeconvComplete(ArrayList<Pool> recovSamp)
    {
        for (Pool p : recovSamp)
            if (p.isEmpty())
                return false;
        return true;
    }
    boolean checkDeconvComplete(ArrayList<Pool> recovSamp, int minReads)
    {
        for (Pool p : recovSamp)
            if (p.ds.getTotalNReads() <= minReads)
                return false;
        return true;
    }
    void clean() throws IOException
    {
        File folder = new File(this.outdir);
        File[] list_files = folder.listFiles();
        for (int i = 0; i < list_files.length; i++)
        {
            if (list_files[i].getName().startsWith("u"))
            {
//                System.out.println("Deleting " + list_files[i].getName());
                list_files[i].delete();
            }
            if (list_files[i].getName().startsWith("clust"))
            {
//                System.out.println("Deleting " + list_files[i].getName());
                Files.deleteRecursively(list_files[i]);
            }
        }
    }

}
