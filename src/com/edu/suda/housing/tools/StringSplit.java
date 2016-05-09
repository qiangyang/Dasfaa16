package com.edu.suda.housing.tools;

public class StringSplit {

	public String str;
	public StringSplit() {
		// TODO Auto-generated constructor stub
	}

	//分割字符串
	public void split(String str){
		String[] s = str.split("-");
		for(int i=0; i<s.length; i++){
			System.out.print(s[i]+" ");
		}
	}
	public String[] splitStr(String str){
		String[] s = str.split("-");
		return s;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        StringSplit ss = new StringSplit();
        String str = "1894-030202-120-2328";
        ss.split(str);
        String s[] = ss.splitStr(str);
        System.out.println();
        for(int i=0; i<s.length; i++){
			System.out.print(s[i]+" ");
		}
	}

}
