package com.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 类名： SpringContextHolder
 * 
 * <br>
 * 描述：以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext
 * 
 * 
 * @author haplant
 * 
 * @date 2015年4月23日 下午1:19:16
 * 
 * @version 1.0
 * 
 * <br>
 *          Copyright by LANDI.
 * 
 * <br>
 *          Program Name:
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static final transient Log logger = LogFactory.getLog(SpringContextHolder.class);

	private static ApplicationContext applicationContext = null;

	/**
	 * 功能描述：实现ApplicationContextAware接口, 注入Context到静态变量中
	 * 
	 * @param applicationContext
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:19:58
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("注入ApplicationContext到SpringContextHolder:" + applicationContext);

		if (SpringContextHolder.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextHolder.applicationContext);
		}

		SpringContextHolder.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * 功能描述：实现DisposableBean接口,在Context关闭时清理静态变量
	 * 
	 * @throws Exception
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:20:09
	 */
	public void destroy() throws Exception {
		SpringContextHolder.clear();
	}

	/**
	 * 功能描述：取得存储在静态变量中的ApplicationContext
	 * 
	 * @return
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:20:21
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 功能描述：从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型
	 * 
	 * @param name
	 * @return
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:20:31
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 功能描述：从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型
	 * 
	 * @param requiredType
	 * @return
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:20:43
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 功能描述：从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型
	 * 
	 * @param name
	 * @param requiredType
	 * @return
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:20:53
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(name, requiredType);
	}

	/**
	 * 功能描述：清除SpringContextHolder中的ApplicationContext为Null
	 * 
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:21:04
	 */
	public static void clear() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * 功能描述：检查ApplicationContext不为空
	 * 
	 * 
	 * @author haplant
	 * 
	 * @date: 2015年4月23日 下午1:21:13
	 */
	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}
}
