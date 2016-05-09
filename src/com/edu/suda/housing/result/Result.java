package com.edu.suda.housing.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Result {
	//groundtruth_example, groundtruth_bj, groundtruth_cd, groundtruth_su, groundtruth_sz, groundtruth_tj
	public static String GROUND_TRUTH_PATH = "groundtruth_example.txt";
	//bj_example, bj_result, cd_result, su_result, sz_result, tj_result
	public static String EXAMPLE_TRUTH_PATH = "bj_example.txt";
	
	static class HouseIdPair{
		int id1;
		int id2;
		public int getId1() {
			return id1;
		}
		public void setId1(int id1) {
			this.id1 = id1;
		}
		public int getId2() {
			return id2;
		}
		public void setId2(int id2) {
			this.id2 = id2;
		}
	}
	
	public static LinkedList<HouseIdPair> getGroundTruth() {
		LinkedList<HouseIdPair> mapping = new LinkedList<HouseIdPair>();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(new File(GROUND_TRUTH_PATH)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line = null;
			try {
				while (null != (line = br.readLine())) {
					HouseIdPair hip = new HouseIdPair();
					String[] temp = line.split(" ");
					if (2 == temp.length) {
						  hip.setId1(Integer.parseInt(temp[0]));
						  hip.setId2(Integer.parseInt(temp[1]));
					}
					mapping.add(hip);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			System.out.println(mapping.size());
		return mapping;
	}
	

	public static LinkedList<HouseIdPair> getResult() {
		LinkedList<HouseIdPair> mapping = new LinkedList<HouseIdPair>();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(new File(EXAMPLE_TRUTH_PATH)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line = null;
			try {
				while (null != (line = br.readLine())) {
					HouseIdPair hip = new HouseIdPair();
					String[] temp = line.split(" ");
					if (2 == temp.length) {
						  hip.setId1(Integer.parseInt(temp[0]));
						  hip.setId2(Integer.parseInt(temp[1]));
					}
					mapping.add(hip);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			System.out.println(mapping.size());
		return mapping;
	}
	
	
	// 获得实验结果
		public static void getPrecesionAndRecall() {
			LinkedList<HouseIdPair> records1 = getGroundTruth();
			LinkedList<HouseIdPair> records2 = getResult();
			int right = 0;
			for(HouseIdPair h2: records2){
				for(HouseIdPair h1: records1){
					if((h1.id1 == h2.id1 && h1.id2 == h2.id2)||(h1.id2 == h2.id1 && h1.id1 == h2.id2)){
						right++;
					}
				}
			}
			System.out.println(right);
			double precision = right * 1.0 / records2.size();
			double recall = right * 1.0 / records1.size();
			System.out.println("准确率： "+precision+" 召回率： "+recall);
		}
		public static void main(String[] args) {
			Result.getPrecesionAndRecall();
		}
}
