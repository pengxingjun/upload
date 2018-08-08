package com.elfinder.localfs;

import com.elfinder.services.FsItem;
import com.elfinder.services.FsVolume;
import com.elfinder.utils.MimeTypesUtils;
import com.web.entities.UploadFile;
import com.upload.helper.SystemValue;
import com.web.service.UploadFileService;
import com.upload.util.DateUtil;
import com.upload.util.SpringContextHolder;
import com.web.util.UploadFileCacheUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MysqlFsVolume implements FsVolume {
    UploadFileService uploadFileService = SpringContextHolder.getBean("uploadFileService");

    String _name;

    UploadFile _rootFile;

    private UploadFile asFile(FsItem fsi) {
        return ((MysqlFsItem) fsi).getFile();
    }

    @Override
    public void createFile(FsItem fsi) throws IOException {
        //还需数据库添加文件记录
        UploadFile uploadFile = asFile(fsi);
        UploadFile ori = UploadFileCacheUtil.getByParentAndName(uploadFile.getParentId(),uploadFile.getName());
        if(ori == null){
            String[] name = uploadFile.getName().split("\\.");
            uploadFile.setMime(MimeTypesUtils.getMimeType(name[1]));
            String path = getCreatePath(uploadFile);
            uploadFile.setPath(path + System.currentTimeMillis() + "." + name[1]);
            File parentPath = new File(path);
            if (!parentPath.exists()) {
                parentPath.mkdirs();
            }
            File file = new File(uploadFile.getPath());
            file.createNewFile();
            uploadFileService.save(uploadFile);
        }
    }

    @Override
    public void createNetFile(FsItem fsi, String url) throws IOException {
        //添加网络文件
        UploadFile uploadFile = asFile(fsi);
        UploadFile ori = UploadFileCacheUtil.getByParentAndName(uploadFile.getParentId(),uploadFile.getName());
        if(ori == null){
            String[] name = uploadFile.getName().split("\\.");
            uploadFile.setMime(MimeTypesUtils.getMimeType(name[1]));
            uploadFile.setPath(url);
            uploadFileService.save(uploadFile);
        }
    }

    @Override
    public void createFolder(FsItem fsi) throws IOException {
        //数据库添加逻辑文件夹
        UploadFile uploadFile = asFile(fsi);
        UploadFile ori = UploadFileCacheUtil.getByParentAndName(uploadFile.getParentId(),uploadFile.getName());
        if(ori == null){
            uploadFile.setMime("directory");
            if (SystemValue.DIR_TYPE == UploadFile.DirType.REAL_PATH) {
                uploadFile.setPath(getCreatePath(uploadFile) + uploadFile.getName());
            } else {
                uploadFile.setPath("");
            }
            uploadFile.setSize(0L);
            uploadFileService.save(uploadFile);
        }
        //new File(asFile(fsi).getPath()).mkdirs();
    }

    @Override
    public void deleteFile(FsItem fsi) throws IOException {
        //数据库删除文件及判断
        File file = new File(asFile(fsi).getPath());
        if (!"directory".equals(asFile(fsi).getMime()) && !file.isDirectory()) {
            uploadFileService.deleteFile(asFile(fsi).getId());
            file.delete();
        }
    }

    @Override
    public void deleteFolder(FsItem fsi) throws IOException {
        //数据库删除文件夹
        /*File file = asFile(fsi);
        if (file.isDirectory())
		{
			FileUtils.deleteDirectory(file);
		}*/
        if ("directory".equals(asFile(fsi).getMime())) {
            //有子级不允许删除
            uploadFileService.deleteDir(asFile(fsi).getId());
            //FileUtils.deleteDirectory(new File(asFile(fsi).getPath()));
        }
    }

    @Override
    public boolean exists(FsItem newFile) {
        //考虑文件夹的情况
        UploadFile uploadFile = asFile(newFile);
        UploadFile ori = UploadFileCacheUtil.getByParentAndName(uploadFile.getParentId(),uploadFile.getName());
        if(ori != null){
            return true;
        }
        return false;
    }

    private MysqlFsItem fromFile(UploadFile file) {
        /*if (!file.getAbsolutePath().startsWith(_rootDir.getAbsolutePath()))
        {
			String message = String.format(
					"Item (%s) can't be outside the root directory (%s)",
					file.getAbsolutePath(), _rootDir.getAbsolutePath());
			throw new IllegalArgumentException(message);
		}*/
        return new MysqlFsItem(this, file);
    }

    @Override
    public FsItem fromPath(String relativePath) {
        String name = "";
        Integer parent_id = null;
        if (relativePath.indexOf("/") < 0) {
            name = relativePath;
            parent_id = _rootFile.getId();
        } else {
            String[] path = relativePath.split("/");
            name = path[2];
            parent_id = Integer.valueOf(path[1]);
        }
        //UploadFile uploadFile = uploadFileService.getByName(parent_id, name);
        UploadFile uploadFile = UploadFileCacheUtil.getByParentAndName(parent_id, name);
        if (uploadFile == null) {
            uploadFile = new UploadFile();
            uploadFile.setName(name);
            uploadFile.setParentId(parent_id);
            //uploadFile.setParent_ids(uploadFileService.findOne(parent_id).getParent_ids() + parent_id + ",");
            uploadFile.setParentIds(UploadFileCacheUtil.get(parent_id).getParentIds() + parent_id + ",");
            uploadFile.setCreator(_rootFile.getCreator());
            uploadFile.setUpdater(_rootFile.getCreator());
            uploadFile.setCreateTime(new Date());
            uploadFile.setUpdateTime(new Date());
        }
        return fromFile(uploadFile);
    }

    @Override
    public String getDimensions(FsItem fsi) {
        return null;
    }

    @Override
    public long getLastModified(FsItem fsi) {
        //考虑文件夹的情况
        UploadFile uploadFile = asFile(fsi);
        return uploadFile.getUpdateTime().getTime();
    }

    @Override
    public String getMimeType(FsItem fsi) {
        /*File file = asFile(fsi);
        if ("directory".equals(file.getPath()))
            return "directory";

        String ext = FilenameUtils.getExtension(file.getName());
        if (ext != null && !ext.isEmpty()) {
            String mimeType = MimeTypesUtils.getMimeType(ext);
            return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE
                    : mimeType;
        }

        return MimeTypesUtils.UNKNOWN_MIME_TYPE;*/
        return asFile(fsi).getMime();
    }

    public String getName() {
        return _name;
    }

    @Override
    public String getName(FsItem fsi) {
        return asFile(fsi).getName();
    }

    @Override
    public FsItem getParent(FsItem fsi) {
        return fromFile(UploadFileCacheUtil.get(asFile(fsi).getParentId()));
    }

    @Override
    public String getPath(FsItem fsi) throws IOException {
		/*String fullPath = asFile(fsi).getCanonicalPath();
		String rootPath = _rootDir.getCanonicalPath();
		String relativePath = fullPath.substring(rootPath.length());
		return relativePath.replace('\\', '/');*/
        UploadFile file = asFile(fsi);
        if (isRoot(fsi)) {
            return "";
        } else {
            return file.getId() + "/" + file.getParentId() + "/" + file.getName();
        }
    }

    @Override
    public FsItem getRoot() {
        return fromFile(_rootFile);
    }

    public UploadFile getRootDir() {
        return _rootFile;
    }

    @Override
    public long getSize(FsItem fsi) throws IOException {
        if (isFolder(fsi)) {
            // This recursively walks down the tree
            return uploadFileService.getDirSize(asFile(fsi).getParentIds() + asFile(fsi).getId() + ",");
        } else {
            return asFile(fsi).getSize();
        }
    }

    @Override
    public String getThumbnailFileName(FsItem fsi) {
        return null;
    }

    @Override
    public String getURL(FsItem f) {
        // 获取文件真实路径
        if(asFile(f).getPath().startsWith("http://") || asFile(f).getPath().startsWith("https://")){
            return asFile(f).getPath();
        }
        return null;
    }

    @Override
    public void filterOptions(FsItem f, Map<String, Object> map) {
        // Don't do anything
    }

    @Override
    public boolean hasChildFolder(FsItem fsi) {
        UploadFile file = asFile(fsi);
        if ("directory".equals(file.getMime()) && file.getChildren() != null && file.getChildren().size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFolder(FsItem fsi) {
        return "directory".equals(asFile(fsi).getMime());
    }

    @Override
    public boolean isRoot(FsItem fsi) {
        return (_rootFile.getId()).equals(asFile(fsi).getId());
    }

    @Override
    public FsItem[] listChildren(FsItem fsi) {
        List<FsItem> list = new ArrayList<FsItem>();
        UploadFile uploadFile = asFile(fsi);
        //List<UploadFile> children = uploadFileService.getFileList(uploadFile.getCreator(), uploadFile.getId());
        List<UploadFile> children = UploadFileCacheUtil.getTreeBean(uploadFile.getCreator(), uploadFile.getId());
        if (children == null || children.size() <= 0) {
            return new FsItem[0];
        }
        uploadFile.setChildren(children);
        for (UploadFile file : children) {
            if(file.getName().equals(UploadFileCacheUtil.get(file.getParentId()).getName() + "_" + SystemValue.THUMB)){
                continue;
            }
            list.add(fromFile(file));
        }
        return list.toArray(new FsItem[0]);
    }

    @Override
    public InputStream openInputStream(FsItem fsi) throws IOException {
        return new FileInputStream(new File(asFile(fsi).getPath()));
    }

    @Override
    public void rename(FsItem src, FsItem dst) throws IOException {
        UploadFile srcF = asFile(src);
        UploadFile dstF = asFile(dst);
        dstF.setId(srcF.getId());
        //dstF.setPath(srcF.getPath().replace(new File(srcF.getPath()).getName(),"") + System.currentTimeMillis() + "." + dstF.getName().split("\\.")[1]);dstF.setPath(srcF.getPath().replace(new File(srcF.getPath()).getName(),"") + System.currentTimeMillis() + "." + dstF.getName().split("\\.")[1]);
        dstF.setPath(srcF.getPath());
        dstF.setMime(srcF.getMime());
        dstF.setSize(srcF.getSize());
        uploadFileService.update(dstF);
        /*if (!"directory".equals(srcF.getMime())) {
            new File(srcF.getPath()).renameTo(new File(dstF.getPath()));
        }*/
    }

    public void setName(String name) {
        _name = name;
    }

    public void setRootDir(UploadFile rootFile) {
        _rootFile = rootFile;
    }

    @Override
    public String toString() {
        return "MysqlFsVolume [" + _rootFile.getParentId() + "_" + _rootFile.getName() + "]";
    }

    @Override
    public void writeStream(FsItem fsi, InputStream is) throws IOException {
        OutputStream os = null;
        try {
            File file = new File(asFile(fsi).getPath());
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);
            asFile(fsi).setSize(file.length());
            uploadFileService.update(asFile(fsi));
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    private String getCreatePath(UploadFile file) {
        String path = "";
        if (SystemValue.DIR_TYPE == UploadFile.DirType.VIRTUAL_PATH) {
            if ("directory".equals(file.getMime())) {
                return path;
            }
            path = SystemValue.RESOURCE_PATH + File.separator + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
        } else {
            path = uploadFileService.findOne(file.getParentId()).getPath() + File.separator;
        }
        return path;
    }
}
