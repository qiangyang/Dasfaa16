package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * <br>[模板方法模式]</b>抽象相似度类
 * 
 * @author JiangJun
 */
public abstract class AbstractSimilarityFunction implements SimilarityFunction  {
	/**
	 * 当前相似度的阈值
	 */
	public static double SIMILARITY_THRESHOLD;
	/**
	 * 相似度
	 */
	protected double similarity = 0;
	/**
	 * 日志
	 */
	protected static Logger logger = LogManager.getLogger();
	/**
	 * 第一个列的属性名
	 */
	protected String str1 = "";
	/**
	 * 第二列的属性名
	 */
	protected String str2 = "";
	
	@Override
	public double getSimilarity(){
		return similarity;
	}
	
	@Override
	public boolean isSimilar(String str1, String str2) {
		if (similarity(str1, str2) >= SIMILARITY_THRESHOLD) {
			return true;
		}
		return false;
	}
}
