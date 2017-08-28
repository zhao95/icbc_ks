package com.rh.core.plug.search;


/**
 * index message transform
 * @author liwei
 */
public interface IndexTransformer {

	/**
	 * index message transform 
	 * @param indexMsg index message
	 */
	void transform(ARhIndex indexMsg);

}
