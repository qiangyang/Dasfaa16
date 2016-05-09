package test;

/**
 * 字符串相似度接口 
 *
 * @author JiangJun
 */
public interface SimilarityFunction {
	/**
	 * 获得相似度
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public double similarity(String str1, String str2);
	
	/**
	 * 直接获得相似度
	 * 
	 * @return
	 */
	public double getSimilarity();
	
	/**
	 * 检查两个字符串是否相同
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean isSimilar(String str1, String str2);
}
