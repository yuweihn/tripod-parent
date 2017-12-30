package com.assist4j.sequence.base;


/**
 * @author yuwei
 */
public interface Sequence {
	default void init() {}
	default void destroy() {}
	
	long nextValue();
}
