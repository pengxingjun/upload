package com.web.exception;

public class ParamException extends RuntimeException {

	/**
	 * 参数异常
	 */
	private static final long serialVersionUID = 6569659539035100584L;

	public ParamException(String msg){
		super(msg);
	}
}
