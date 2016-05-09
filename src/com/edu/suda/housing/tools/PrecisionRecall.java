package com.edu.suda.housing.tools;

public class PrecisionRecall {
   public void printPrecisionRecall(double p, double r){
	   System.out.println(p*r*2/(p+r));
   }
   public static void main(String[] args) {
	PrecisionRecall pr = new PrecisionRecall();
	pr.printPrecisionRecall(0.8847, 0.8526);
}  
}
