package com.assist4j.sequence.base;


/**
 * @author yuwei
 */
public interface Sequence {
	void init();
	void destroy();
	
	long nextValue();
}
