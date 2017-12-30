package com.assist4j.sequence.base;


import java.util.Map;


/**
 * @author yuwei
 **/
public interface SequenceBeanHolder {
	/**
	 * 返回beanName和seqName的键值对，如果seqName为空，就以beanName作为seqName
	 */
	Map<String, String> getBeanSeqNameMap();
}
