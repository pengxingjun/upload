package com.elfinder.controller.executors;

import com.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.elfinder.controller.executor.CommandExecutor;
import com.elfinder.controller.executor.FsItemEx;
import com.elfinder.services.FsService;
import com.elfinder.services.FsVolume;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpenCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor {

    @Override
    public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
            throws Exception {
        boolean init = request.getParameter("init") != null;
        boolean tree = request.getParameter("tree") != null;
        String target = request.getParameter("target");

        Map<String, FsItemEx> files = new LinkedHashMap<String, FsItemEx>();
        if (init) {
            json.put("api", 2.1);
            json.put("netDrivers", new Object[0]);
        }

        FsItemEx cwd = findCwd(fsService, target);
        String[] onlyMimes = request.getParameterValues("mimes[]");
        if (tree) {
            for (FsVolume v : fsService.getVolumes()) {
                FsItemEx root = new FsItemEx(v.getRoot(), fsService);
                files.put(root.getHash(), root);
                addSubfolders(files, root);
            }
        }
        files.put(cwd.getHash(), cwd);
        addChildren(files, cwd, onlyMimes);

        json.put("files", files2JsonArray(request, files.values()));
        json.put("cwd", getFsItemInfo(request, cwd));
        json.put("options", getOptions(request, cwd));
    }
}
