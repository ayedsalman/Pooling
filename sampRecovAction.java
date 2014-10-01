package Pooling;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class sampRecovAction {
    int i;
    int j;
    String oper;
    sampRecovAction(int u, int v, String s)
    {
        i = u;
        j = v;
        oper = s;
    }
    void print()
    {
        System.out.println(i + " " + j + " " + oper);
    }
    
}
