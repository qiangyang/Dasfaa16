package com.edu.suda.housing.tools;
import java.math.BigInteger;

public class Cnk
{
	
 public static int cnk(int n, int k){
	  int fenzi = 1;
	  int fenmu = 1;
	  for (int i = n - k + 1; i <= n; i++){
	   String s = Integer.toString(i);
	   BigInteger stobig = new BigInteger(s);
	   // fenzi = fenzi.multiply(stobig);
	   fenzi *= i;
	  }
	  for (int j = 1; j <= k; j++){
	   String ss = Integer.toString(j);
	   BigInteger stobig2 = new BigInteger(ss);
	   // fenmu = fenmu.multiply(stobig2);
	   fenmu *= j;
	  }
	  // BigInteger result = fenzi.divide(fenmu);
	  int result = fenzi / fenmu;
	  return result;
    }
 public static void main(String[] args) {
	System.out.println(Cnk.cnk(4, 2)); 
  }
 }