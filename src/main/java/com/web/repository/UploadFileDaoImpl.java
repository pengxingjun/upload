package com.web.repository;

import com.web.entities.UploadFile;
import com.web.base.repository.impl.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2017-7-27.
 */
@Repository
public class UploadFileDaoImpl extends BaseDaoImpl<UploadFile> {

    /**
     * 获得根目录
     *
     * @return
     */
    public UploadFile getDatabase() {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select * from t_upload_file where parent_id = 0 ");
        return findOne(sb.toString(), arrList);
    }

    /**
     * 获得根目录
     *
     * @param userId
     * @return
     */
    public UploadFile getRoot(Integer userId, Integer databaseId) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        if (userId != null) {
            sb.append("select * from t_upload_file where creator = ? and parent_id = ? ");
            arrList.add(userId);
            arrList.add(databaseId);
        } else {
            sb.append("select * from t_upload_file where parent_id = 0 ");
        }
        return findOne(sb.toString(), arrList);
    }

    /**
     * 获取用户下所有文件
     *
     * @param userId
     * @return
     */
    public List<UploadFile> getFileList(Integer userId) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select * from t_upload_file where creator = ? ");
        arrList.add(userId);
        return findList(sb.toString(), arrList);
    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("delete from t_upload_file where id = ? ");
        arrList.add(id);
        return updateQuery(sb.toString(), arrList);
    }

    /**
     * 根据父级id和名称获取文件
     *
     * @param parent_id
     * @param name
     * @return
     */
    public UploadFile getByName(Integer parent_id, String name) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select * from t_upload_file where parent_id = ? and name = ? ");
        arrList.add(parent_id);
        arrList.add(name);
        return findOne(sb.toString(), arrList);
    }

    /**
     * 获取父级文件夹下所有文件（夹）
     *
     * @param parent_id
     * @return
     */
    public List<UploadFile> getByParent(Integer parent_id) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select * from t_upload_file where parent_id = ? ");
        arrList.add(parent_id);
        return findList(sb.toString(), arrList);
    }

    /**
     * 获取父级文件夹下所有文件（夹）的数量
     *
     * @param parent_id
     * @return
     */
    public int getCountByParent(Integer parent_id) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select * from t_upload_file where parent_id = ? ");
        arrList.add(parent_id);
        return findListSize(sb.toString(), arrList);
    }

    /**
     * 获取文件夹及其内容的大小
     *
     * @param parent_ids
     * @return
     */
    public long getDirSize(String parent_ids) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrList = new ArrayList<Object>();
        sb.append("select IFNULL(sum(size),0) from t_upload_file where parent_ids like concat(?,'%') ");
        arrList.add(parent_ids);
        return Long.parseLong(findCount(sb.toString(), arrList).toString());
    }
}
