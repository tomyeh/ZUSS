/* BuiltinResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 12 00:15:28 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.out;

import java.util.concurrent.Callable;

import org.zkoss.zuss.Resolver;
import org.zkoss.zuss.ZussException;

/**
 * The resolver providing the built-in variables and functions.
 * @author tomyeh
 */
public class BuiltinResolver implements Resolver {


	@Override
	public Object getVariable(String name) {
		return null;
	}
	
	@Override
	public Callable<Object> getMethod(String name, final Object[] args) {
	    if ("eval".equals(name)) {
        	    if (args.length != 1) {
        	        throw new ZussException("invalid count of arguments to method eval");
        	    }
        	    return new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        return args[0];
                    }};
	    }
	    return null;
	}
	
}
