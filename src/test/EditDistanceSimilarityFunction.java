package test;




/**
 * 基于编辑距离的字符串相似度
 * 
 * @author JiangJun
 */
public class EditDistanceSimilarityFunction extends AbstractSimilarityFunction {
	static{
		SIMILARITY_THRESHOLD = 0.75;//0.75
	}
	@Override
	public double similarity(String str1, String str2) {
		if (null == str1 || str1.isEmpty() || null == str2 || str2.isEmpty()){
			return 0;
		}
		
		if (this.str1.equals(str1) && this.str2.equals(str2)) {
			return similarity;
		}
		
		// 两个字符串的长度
		int len1 = str1.length(),len2 = str2.length();
		int len  = len1 > len2 ? len1 : len2;
		int[][] edit = new int[len2+1][len1+1];
		
		// 初始化
		for(int i = 0;i <= len1;i++){
			edit[0][i] = i;
		}
		for(int j = 0;j <= len2;j++){
			edit[j][0] = j;
		}
		
		int cost = 0;
		for(int i = 1;i <= len1;i++){
			char c1 = str1.charAt(i-1);
			for(int j = 1;j <= len2; j++){
				char c2 = str2.charAt(j-1);
				if(c1 != c2){
					cost = 1;
				} else {
					cost= 0;
				}
				edit[j][i] = min(edit[j-1][i]+1,edit[j][i-1]+1,edit[j-1][i-1]+cost);
			}
		}
		similarity = 1- edit[len2][len1] * 1.0 / len;
		
		this.str1 = str1;
		this.str2 = str2;
		return similarity;
	}
	/**
	 * 获得最小值
	 * 
	 * @param args
	 * @return
	 */
	private int min(int...args){
		int result = Integer.MAX_VALUE;
		if (null == args){
			return result;
		}
		for (int i = 0; i < args.length ; i++) {
			if (args[i] < result){
				result = args[i];
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		SimilarityFunction similarityFunction = new EditDistanceSimilarityFunction();
		System.out.println(similarityFunction.similarity("中央处理器", "处理器频率"));
		System.out.println(similarityFunction.similarity("3G手机", "智能手机"));
		System.out.println(similarityFunction.similarity("", ""));
		System.out.println(similarityFunction.isSimilar("3G手机", "智能手机,双核手机,3G手机"));
	}
}
