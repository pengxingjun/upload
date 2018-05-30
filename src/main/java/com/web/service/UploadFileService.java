package com.web.service;

import com.web.entities.UploadFile;
import com.web.exception.ParamException;
import com.web.helper.SystemValue;
import com.web.repository.UploadFileDao;
import com.web.util.UploadFileCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dean on 2017-7-27.
 */
@Service
@Transactional(readOnly = true)
public class UploadFileService {

    @Resource
    private UploadFileDao uploadFileDao;

    /**
     * 获取数据库根目录
     *
     * @return
     */
    public UploadFile getDatabase() {
        return uploadFileDao.getDatabase();
    }

    /**
     * 获取用户根目录
     *
     * @param userId
     * @return
     */
    public UploadFile getRoot(Integer userId, Integer databaseId) {
        return uploadFileDao.getRoot(userId, databaseId);
    }

    /**
     * 新增database文件夹
     *
     * @return
     */
    public UploadFile addRoot() {
        UploadFile database = new UploadFile();
        database.setName("DATABASE");
        database.setParentId(0);
        database.setParentIds("0,");
        if (SystemValue.DIR_TYPE == UploadFile.DirType.REAL_PATH) {
            database.setPath(SystemValue.RESOURCE_PATH);
        } else {
            database.setPath("");
        }
        database.setMime("directory");
        database.setSize(0L);
        database.setCreator(0);
        database.setCreateTime(new Date());
        database.setUpdater(0);
        database.setUpdateTime(new Date());
        UploadFileCacheUtil.removeCache(database);
        return uploadFileDao.save(database);
    }

    public UploadFile addUserRoot(Integer userId){
        UploadFile rootFile = new UploadFile();
        UploadFile database = UploadFileCacheUtil.getDatabase();
        rootFile.setName(userId + "");
        rootFile.setParentId(database.getId());
        rootFile.setParentIds(database.getParentIds() + rootFile.getParentId() + ",");
        if(SystemValue.DIR_TYPE == UploadFile.DirType.REAL_PATH){
            rootFile.setPath(database.getPath() + rootFile.getName() + File.separator);
        }else{
            rootFile.setPath("");
        }
        rootFile.setMime("directory");
        rootFile.setSize(0L);
        rootFile.setCreator(userId);
        rootFile.setCreateTime(new Date());
        rootFile.setUpdater(userId);
        rootFile.setUpdateTime(new Date());
        UploadFileCacheUtil.removeCache(rootFile);
        return uploadFileDao.save(rootFile);
    }

    /**
     * 获取文件夹目录列表（树形）
     *
     * @param userId
     * @param parent_id
     * @return
     */
    public List<UploadFile> getFileList(Integer userId, Integer parent_id) {
        return treeBean(uploadFileDao.getFileList(userId), parent_id);
    }

    /**
     * 获取文件夹目录列表(非树形)
     *
     * @param userId
     * @return
     */
    public List<UploadFile> getByUserId(Integer userId) {
        return uploadFileDao.getFileList(userId);
    }

    /**
     * 保存
     *
     * @param uploadFile
     * @return
     */
    @Transactional(readOnly = false)
    public UploadFile save(UploadFile uploadFile) {
        uploadFile = uploadFileDao.save(uploadFile);
        UploadFileCacheUtil.removeCache(uploadFile);
        return uploadFile;
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public UploadFile findOne(Integer id) {
        return uploadFileDao.findOne(id);
    }

    /**
     * 更新
     *
     * @param uploadFile
     */
    @Transactional(readOnly = false)
    public void update(UploadFile uploadFile) {
        UploadFile ori = UploadFileCacheUtil.get(uploadFile.getId());
        ori.setName(uploadFile.getName());
        ori.setParentId(uploadFile.getParentId());
        ori.setParentIds(UploadFileCacheUtil.get(uploadFile.getParentId()).getParentIds() + uploadFile.getParentId() + ",");
        ori.setUpdateTime(new Date());
        ori.setUpdater(uploadFile.getUpdater());
        if (SystemValue.DIR_TYPE == UploadFile.DirType.VIRTUAL_PATH && !"directory".equals(uploadFile.getMime())
                && !StringUtils.isEmpty(uploadFile.getPath())) {
            ori.setPath(uploadFile.getPath());
        }
        ori.setSize(uploadFile.getSize());
        UploadFileCacheUtil.removeCache(uploadFile);
        uploadFileDao.save(ori);
    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public int deleteFile(Integer id) {
        UploadFileCacheUtil.removeCache(UploadFileCacheUtil.get(id));
        int num = uploadFileDao.deleteById(id);
        return num;
    }

    @Transactional(readOnly = false)
    public int deleteDir(Integer id) {
        int num = uploadFileDao.getCountByParent(id);
        if (num > 0) {
            throw new ParamException("请先删除子文件及子文件夹");
        }
        UploadFileCacheUtil.removeCache(UploadFileCacheUtil.get(id));
        num = uploadFileDao.deleteById(id);
        return num;
    }

    /**
     * 根据名称和父id找到文件
     *
     * @param parent_id
     * @param name
     * @return
     */
    public UploadFile getByName(Integer parent_id, String name) {
        return uploadFileDao.getByName(parent_id, name);
    }

    /**
     * 获取文件夹大小（包含子文件（夹））
     * @param parent_ids
     * @return
     */
    public long getDirSize(String parent_ids){
        return uploadFileDao.getDirSize(parent_ids);
    }

    /**
     * 获取子文件（夹），不递归
     * @param parent_id
     * @return
     */
    public List<UploadFile> getByParentId(Integer parent_id){
        return uploadFileDao.getByParent(parent_id);
    }

    /**
     * 递归获取树形结构
     *
     * @param list
     * @param parent_id
     * @return
     */
    public List<UploadFile> treeBean(List<UploadFile> list, int parent_id) {
        List<UploadFile> nodeList = new ArrayList<>();
        for (UploadFile file : list) {
            Integer pid = file.getParentId();
            Integer id = file.getId();
            if (parent_id == pid) {
                List<UploadFile> c_node = treeBean(list, id);
                if (c_node != null && c_node.size() > 0) {
                    file.setChildren(c_node);
                }
                nodeList.add(file);
            }
        }
        return nodeList;
    }

}
