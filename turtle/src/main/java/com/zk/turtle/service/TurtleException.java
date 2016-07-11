package com.zk.turtle.service;

/**
 * @author yupeng.jia@renren-inc.com
 */
public class TurtleException extends RuntimeException {
	public TurtleException() {
		super();
	}

	public TurtleException(String msg) {
		super(msg);
	}

	public TurtleException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
