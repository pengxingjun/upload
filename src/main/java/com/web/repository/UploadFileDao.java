package com.web.repository;

import com.web.entities.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Dean on 2017-7-27.
 */
public interface UploadFileDao extends JpaRepository<UploadFile, Integer>,JpaSpecificationExecutor<UploadFile> {
    UploadFile getDatabase();
    UploadFile getRoot(Integer userId, Integer databaseId);
    List<UploadFile> getFileList(Integer userId);
    int deleteById(Integer id);
    UploadFile getByName(Integer parent_id, String name);
    List<UploadFile> getByParent(Integer parent);
    int getCountByParent(Integer parent);
    long getDirSize(String parent_ids);
}
