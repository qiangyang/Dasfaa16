package com.edu.suda.housing.tools;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCompute {

	public static void getResultNum(ResultSet rs){
		int num = 0;
		try {
			rs.last();
			num = rs.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		System.out.println("记录的条数为："+num);
	}
}
