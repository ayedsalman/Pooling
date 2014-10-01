package Pooling;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Pooling.DoubleGraph;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author kki8
 */
public class TestSet {
    ArrayList<ArrayList<Vertex>> testSet;
    TestSet()
    {
        testSet = new ArrayList();
    }
    void addTest(ArrayList<Vertex> alv)
    {
        testSet.add(alv);
    }
    void printToConsole()
    {
        System.out.println("Test set");
        int i = 1;
        for (ArrayList<Vertex> alv : testSet)
        {
            System.out.println("Test " + i);
            for (Vertex v : alv)
                System.out.print(v.index + " ");
            System.out.println();
            i++;
        }
    }
    void printToFile(String addr) throws IOException
    {
        FileWriter fw = new FileWriter(addr);
        int i = 1;
        for (ArrayList<Vertex> alv : testSet)
        {
            fw.write("Test " + i + "\n");
            for (Vertex v : alv)
                fw.write(v.index + " ");
            fw.write("\n");
            i++;
        }
        fw.close();
    }
    int getNTests()
    {
        return testSet.size();
    }
    void printToConsoleCheckFeasibility(DoubleGraph g)
    {
        System.out.println("Test set");
        int nset = 1;
        for (ArrayList<Vertex> alv : testSet)
        {
            System.out.println("Test " + nset);
            for (Vertex v : alv)
                System.out.print(v.index + " ");
            System.out.println();
            nset++;
            for (int i = 0; i < alv.size(); i++)
                for (int j = i+1; j < alv.size(); j++)
                {
                    if (!g.adjListsNonrel.get(alv.get(i)).contains(alv.get(j)))
                        System.out.println("The pair (" + alv.get(i).index + "," + alv.get(j).index + ") is infeasible");
                }
            System.out.println();
            
        }
    }
    void printToConsoleCheckFeasibility1(DoubleGraph g)
    {
        System.out.println("Test set");
        int nset = 1;
        for (ArrayList<Vertex> alv : testSet)
        {
            System.out.println("Test " + nset);
            for (Vertex v : alv)
                System.out.print(v.index + " ");
            System.out.println();
            nset++;
            for (int i = 0; i < alv.size(); i++)
                for (int j = i+1; j < alv.size(); j++)
                {
                    Vertex u1 = alv.get(i);
                    Vertex v1 = alv.get(j);
                    Vertex u2 = null;
                    Vertex v2 = null;
                    for (Vertex x : g.vertices)
                    {
                        if (x.index == u1.index)
                            u2 = x;
                        if (x.index == v1.index)
                            v2 = x;
                    }
                    if (!g.adjListsNonrel.get(u2).contains(v2))
                        System.out.println("The pair (" + alv.get(i).index + "," + alv.get(j).index + ") is infeasible");
                }
            System.out.println();
            
        }
    }
    void readFromCplexFile(String addr) throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(addr));
        String s = br.readLine();
        
        while (!s.contains("variables"))
            s = br.readLine();
        HashMap<Integer,ArrayList<Vertex>> hm = new HashMap();
        s = br.readLine();
        while (!s.contains("variables"))
        {
            StringTokenizer st = new StringTokenizer(s," =\"_");
            String s1 = st.nextToken();
            s1 = st.nextToken();
            s1 = st.nextToken();
            if (s1.charAt(0) == 'y')
            {
                int k = Integer.parseInt(s1.substring(1));
                s1 = st.nextToken();
                s1 = st.nextToken();
                s1 = st.nextToken();
                s1 = st.nextToken();
                int var = Integer.parseInt(s1);
//                if (var == 1)
//                    hm.put(k, new ArrayList<Vertex>());
            }
            if (s1.charAt(0) == 'z')
            {
                int i = Integer.parseInt(s1.substring(1));
                s1 = st.nextToken();
                int k = Integer.parseInt(s1);
                s1 = st.nextToken();
                s1 = st.nextToken();
                s1 = st.nextToken();
                s1 = st.nextToken();
                long var = Math.round(Double.parseDouble(s1));
                if (var == 1)
                {
                    if (hm.containsKey(k))
                        hm.get(k).add(new Vertex(i));
                    else
                    {
                        ArrayList<Vertex> ar = new ArrayList();
                        ar.add(new Vertex(i));
                        hm.put(k, ar);
                    }
                }
            }
            s = br.readLine();
        }
        testSet = new ArrayList();
        Iterator ir = hm.entrySet().iterator();
        while (ir.hasNext())
        {
            Map.Entry me = (Map.Entry) ir.next();
            testSet.add((ArrayList<Vertex>) me.getValue());
        }
        br.close();
    }
    boolean separates(int i, Vertex u, Vertex v)
    {
        if (testSet.get(i).contains(u) && (!testSet.get(i).contains(v)))
            return true;
        if (testSet.get(i).contains(v) && (!testSet.get(i).contains(u)))
            return true;
        return false;
        
    }
    void createMSTFile(String addr, int n) throws IOException
    {
        FileWriter fw = new FileWriter(addr);
        ArrayList<Vertex> vertices = new ArrayList();
        for (ArrayList<Vertex> ar : testSet)
            for (Vertex v : ar)
                if (! vertices.contains(v))                    
                    vertices.add(v);
        
       ArrayList<Integer> hs = new ArrayList();
        for (int i = 1; i <= n; i++)
            hs.add(i);
        for (Vertex v : vertices)
            if (hs.contains(v.index))
                hs.remove((Integer) v.index);
        if (hs.size() > 0)
            vertices.add(new Vertex(hs.get(0)));
        
        
        fw.write("NAME\n");
        for (int i = 1; i <= testSet.size(); i++)
            fw.write("\ty" + i + " 1\n");
        for (int i = testSet.size() + 1; i <= n; i++)
            fw.write("\ty" + i + " 0 " + "\n");
        
        for (int k = 1; k <= testSet.size(); k++)
        {
            ArrayList<Vertex> ar  = testSet.get(k-1);
            for (Vertex v : vertices)
            {
                if (ar.contains(v))
                    fw.write("\tz" + v.index + "_" + k + " 1\n");
                else
                    fw.write("\tz" + v.index + "_" + k + " 0\n");
            }
        }
         for (int k = testSet.size() + 1; k <= n; k++)
        {
            for (Vertex v : vertices)
            {
                    fw.write("\tz" + v.index + "_" + k + " 0\n");
            }
        }
        
        for (int k = 1; k <= testSet.size(); k++)
        {
            for (int i = 0; i < vertices.size(); i++)
                for (int j = i+1; j < vertices.size(); j++)
                {
                    int u = 0;
                    int v = 0;
                    if (vertices.get(i).index < vertices.get(j).index)
                    {
                        u = vertices.get(i).index;
                        v = vertices.get(j).index;
                    }
                    else
                    {
                        u = vertices.get(j).index;
                        v = vertices.get(i).index;
                    }
                    if (this.separates(k-1,vertices.get(i),vertices.get(j)))
                        fw.write("\tt" + u + "_" + v + "_" + k + " 1\n");
                    else
                        fw.write("\tt" + u + "_" + v + "_" + k + " 0\n");
                }
        }
         for (int k = testSet.size() + 1; k <= n; k++)
        {
            for (int i = 0; i < vertices.size(); i++)
                for (int j = i+1; j < vertices.size(); j++)
                {
                    int u = 0;
                    int v = 0;
                    if (vertices.get(i).index < vertices.get(j).index)
                    {
                        u = vertices.get(i).index;
                        v = vertices.get(j).index;
                    }
                    else
                    {
                        u = vertices.get(j).index;
                        v = vertices.get(i).index;
                    }
                    fw.write("\tt" + u + "_" + v + "_" + k + " 0\n");
                }
        }
        fw.write("ENDATA");
        fw.close();
    }
    void createMSTFileQP(String addr, int n) throws IOException
    {
        FileWriter fw = new FileWriter(addr);
        ArrayList<Vertex> vertices = new ArrayList();
        for (ArrayList<Vertex> ar : testSet)
            for (Vertex v : ar)
                if (! vertices.contains(v))                    
                    vertices.add(v);
        
       ArrayList<Integer> hs = new ArrayList();
        for (int i = 1; i <= n; i++)
            hs.add(i);
        for (Vertex v : vertices)
            if (hs.contains(v.index))
                hs.remove((Integer) v.index);
        if (hs.size() > 0)
            vertices.add(new Vertex(hs.get(0)));
        
        
        fw.write("NAME\n");
        for (int i = 1; i <= testSet.size(); i++)
            fw.write("\ty" + i + " 1\n");
        
        for (int k = 1; k <= testSet.size(); k++)
        {
            ArrayList<Vertex> ar  = testSet.get(k-1);
            for (Vertex v : vertices)
            {
                if (ar.contains(v))
                    fw.write("\tz" + v.index + "_" + k + " 1\n");
                else
                    fw.write("\tz" + v.index + "_" + k + " 0\n");
            }
        }
        
        for (int k = 1; k <= testSet.size(); k++)
        {
            for (int i = 0; i < vertices.size(); i++)
                for (int j = i+1; j < vertices.size(); j++)
                {
                    int u = 0;
                    int v = 0;
                    if (vertices.get(i).index < vertices.get(j).index)
                    {
                        u = vertices.get(i).index;
                        v = vertices.get(j).index;
                    }
                    else
                    {
                        u = vertices.get(j).index;
                        v = vertices.get(i).index;
                    }
                    if (this.separates(k-1,vertices.get(i),vertices.get(j)))
                        fw.write("\tt" + u + "_" + v + "_" + k + " 1\n");
                    else
                        fw.write("\tt" + u + "_" + v + "_" + k + " 0\n");
                }
        }
        fw.write("ENDATA");
        fw.close();
    }
    void createMSTFileQCQP(String addr, int n) throws IOException
    {
        FileWriter fw = new FileWriter(addr);
        ArrayList<Vertex> vertices = new ArrayList();
        for (ArrayList<Vertex> ar : testSet)
            for (Vertex v : ar)
                if (! vertices.contains(v))                    
                    vertices.add(v);
        
       ArrayList<Integer> hs = new ArrayList();
        for (int i = 1; i <= n; i++)
            hs.add(i);
        for (Vertex v : vertices)
            if (hs.contains(v.index))
                hs.remove((Integer) v.index);
        if (hs.size() > 0)
            vertices.add(new Vertex(hs.get(0)));
        
        
        fw.write("NAME\n");
        for (int i = 1; i <= testSet.size(); i++)
            fw.write("\ty" + i + " 1\n");
        
        for (int k = 1; k <= testSet.size(); k++)
        {
            ArrayList<Vertex> ar  = testSet.get(k-1);
            for (Vertex v : vertices)
            {
                if (ar.contains(v))
                    fw.write("\tz" + v.index + "_" + k + " 1\n");
                else
                    fw.write("\tz" + v.index + "_" + k + " 0\n");
            }
        }
        fw.write("ENDATA");
        fw.close();
    }
    ArrayList<ArrayList<Integer>> getArrayList()
    {
        ArrayList<ArrayList<Integer>> partitions = new ArrayList();
        for (ArrayList<Vertex> alv : testSet)
        {
            ArrayList<Integer> ar = new ArrayList();
            for (Vertex v : alv)
                ar.add(v.index);
            partitions.add(ar);
        }
        return partitions;
    }
    void complete(int n)
    {
        HashSet<Integer> hs = new HashSet();
        for (int i = 1; i <= n; i++)
            hs.add(i);
        for (ArrayList<Vertex> ar : this.testSet)
            for (Vertex v : ar)
                if (hs.contains(v.index))
                    hs.remove(v.index);
        if (hs.size() > 0)
            for (Integer i : hs)
            {
                ArrayList<Vertex> ar = new ArrayList();
                ar.add(new Vertex(i));
                this.testSet.add(ar);
            }
        
    }
}
