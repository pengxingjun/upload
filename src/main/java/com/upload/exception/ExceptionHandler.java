package com.upload.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
/**
 * 异常集中处理类
 * @author Dean
 *
 */
public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		Map<String, Object> model = new HashMap<String, Object>(); 
		model.put("ex", ex);
		if(ex instanceof ParamException){
			return new ModelAndView("/error/param",model);
		}else{
			return new ModelAndView("/error/unknow",model);
		}
	}

}
