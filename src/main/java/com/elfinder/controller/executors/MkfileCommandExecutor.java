package com.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.elfinder.controller.executor.CommandExecutor;
import com.elfinder.controller.executor.FsItemEx;
import com.elfinder.services.FsItemFilter;
import com.elfinder.services.FsService;

public class MkfileCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		//创建文件文件，暂时没用到，先不改
		String target = request.getParameter("target");
		String name = request.getParameter("name");

		FsItemEx fsi = super.findItem(fsService, target);
		FsItemEx dir = new FsItemEx(fsi, name);
		dir.createFile();

		// if the new file is allowed to be display?
		FsItemFilter filter = getRequestedFilter(request);
		json.put(
				"added",
				filter.accepts(dir) ? new Object[] { getFsItemInfo(request, dir) }
						: new Object[0]);
	}
}
