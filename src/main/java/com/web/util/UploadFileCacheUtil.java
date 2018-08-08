package com.web.util;

import com.upload.util.SpringContextHolder;
import com.web.entities.UploadFile;
import com.web.service.UploadFileService;

import java.util.List;

/**
 * Created by Dean on 2017-8-4.
 */
public class UploadFileCacheUtil {

    private static UploadFileService uploadFileService = SpringContextHolder.getBean("uploadFileService");

    public static final String FILE_CACHE_USER_ = "user_";
    public static final String FILE_CACHE_ID_ = "id_";
    public static final String FILE_CACHE_PARENT_ = "parent_";
    public static final String FILE_CACHE_TREE_ = "tree_";
    public static final String FILE_CACHE_DATABASE_ = "database_";
    public static final String FILE_CACHE_USER_ROOT_ = "root_";

    /**
     * 根据ID查找缓存中的文件信息
     *
     * @param id
     * @return
     */
    public static UploadFile get(Integer id) {
        UploadFile uploadFile = (UploadFile) CacheUtils.get(FILE_CACHE_ID_ + id);
        if (uploadFile == null) {
            uploadFile = uploadFileService.findOne(id);
            if (uploadFile != null) {
                CacheUtils.put(FILE_CACHE_ID_ + id, uploadFile);
            }
        }
        return uploadFile;
    }

    /**
     * 根据用户ID查询缓存中的文件信息
     *
     * @param userId
     * @return
     */
    public static List<UploadFile> getFromUser(Integer userId) {
        List<UploadFile> uploadFiles = (List<UploadFile>) CacheUtils.get(FILE_CACHE_USER_ + userId);
        if (uploadFiles == null || uploadFiles.size() <= 0) {
            uploadFiles = uploadFileService.getByUserId(userId);
            if (uploadFiles != null && uploadFiles.size() > 0) {
                CacheUtils.put(FILE_CACHE_USER_ + userId, uploadFiles);
            }
        }
        return uploadFiles;
    }

    /**
     * 获取树形文件列表
     *
     * @param userId
     * @param parentId
     * @return
     */
    public static List<UploadFile> getTreeBean(Integer userId, Integer parentId) {
        List<UploadFile> uploadFiles = (List<UploadFile>) CacheUtils.get(FILE_CACHE_TREE_ + userId + "_" + parentId);
        if (uploadFiles == null || uploadFiles.size() <= 0) {
            uploadFiles = getFromUser(userId);
            if (uploadFiles != null && uploadFiles.size() > 0) {
                uploadFiles = uploadFileService.treeBean(uploadFiles, parentId);
                CacheUtils.put(FILE_CACHE_TREE_ + userId + "_" + parentId, uploadFiles);
            }
        }
        return uploadFiles;
    }

    /**
     * 从缓存中获取子文件（夹）信息
     *
     * @param parent_id
     * @return
     */
    public static List<UploadFile> getFromParent(Integer parent_id) {
        List<UploadFile> uploadFiles = (List<UploadFile>) CacheUtils.get(FILE_CACHE_PARENT_ + parent_id);
        if (uploadFiles == null || uploadFiles.size() <= 0) {
            uploadFiles = uploadFileService.getByParentId(parent_id);
            if (uploadFiles != null && uploadFiles.size() > 0) {
                CacheUtils.put(FILE_CACHE_PARENT_ + parent_id, uploadFiles);
            }
        }
        return uploadFiles;
    }

    /**
     * 根据父ID和名称获取文件信息
     *
     * @param parent_id
     * @param name
     * @return
     */
    public static UploadFile getByParentAndName(Integer parent_id, String name) {
        UploadFile uploadFile = null;
        List<UploadFile> uploadFiles = getFromParent(parent_id);
        if (uploadFiles != null && uploadFiles.size() > 0) {
            for (UploadFile file : uploadFiles) {
                if (name.equals(file.getName())) {
                    uploadFile = file;
                    break;
                }
            }
        }
        return uploadFile;
    }

    /**
     * 获取database文件夹
     *
     * @return
     */
    public static UploadFile getDatabase() {
        UploadFile database = (UploadFile) CacheUtils.get(FILE_CACHE_DATABASE_);
        if (database == null) {
            database = uploadFileService.getDatabase();
            if (database == null) {
                database = uploadFileService.addRoot();
            }
            CacheUtils.put(FILE_CACHE_DATABASE_, database);
        }
        return database;
    }

    public static UploadFile getUserRoot(Integer userId) {
        UploadFile rootFile = (UploadFile) CacheUtils.get(FILE_CACHE_USER_ROOT_ + userId);
        if (rootFile == null) {
            rootFile = uploadFileService.getRoot(userId, getDatabase().getId());
            if (rootFile == null) {
                rootFile = uploadFileService.addUserRoot(userId);
            }
            CacheUtils.put(FILE_CACHE_USER_ROOT_ + userId, rootFile);
        }
        return rootFile;
    }

    /**
     * 移除缓存
     *
     * @param uploadFile
     */
    public static void removeCache(UploadFile uploadFile) {
        if(uploadFile != null){
            CacheUtils.remove(FILE_CACHE_ID_ + uploadFile.getId());
            CacheUtils.remove(FILE_CACHE_USER_ + uploadFile.getCreator());
            CacheUtils.remove(FILE_CACHE_PARENT_ + uploadFile.getParentId());
            CacheUtils.remove(FILE_CACHE_TREE_ + uploadFile.getCreator() + "_" + uploadFile.getParentId());
        }
    }

}
