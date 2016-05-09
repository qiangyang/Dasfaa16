package com.edu.suda.housing.graph;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int n=5; //5个结点
        int e=5; //5条边
        MyAdjGraphic g = new MyAdjGraphic(n);
        String[] vertices = new String[]{new String("A"),new String("B"),new String("C"),new String("D"),new String("E")};
        Weight[] weights = new Weight[]{new Weight(0,1,10),new Weight(0,4,20),new Weight(2,1,40),new Weight(1,3,30),new Weight(3,2,50)};
        
        try
        {
           Weight.createAdjGraphic(g, vertices, n, weights, e);
           System.out.println("--------该临街矩阵如下---------");
           g.print();
           System.out.println("结点的个数："+g.getNumOfVertice());
           System.out.println("边的个数："+g.getNumOfEdges());
           g.deleteEdges(0, 4);
           System.out.println("--------删除之后---------");
           g.print();
           System.out.println("结点的个数："+g.getNumOfVertice());
           System.out.println("边的个数："+g.getNumOfEdges());
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
	}

}
