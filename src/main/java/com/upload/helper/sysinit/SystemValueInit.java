package com.upload.helper.sysinit;

import com.upload.helper.SystemValue;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * 
 * @author Dean
 *
 *在Spring容器将所有的Bean都初始化完成之后执行，
 *
 */
public class SystemValueInit implements ApplicationListener<ContextRefreshedEvent>{
	@Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() != null){
	        //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
			SystemValue.DEBUG_MODE = Boolean.parseBoolean(SystemValue.getProperties("debugMode"));
			SystemValue.RESOURCE_PATH = SystemValue.getProperties("resourcePath");
			SystemValue.THUMB = SystemValue.getProperties("thumb");
	    }
	}
}
