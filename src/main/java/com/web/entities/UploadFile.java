package com.web.entities;

import com.elfinder.localfs.MysqlFsVolume;
import com.elfinder.services.FsVolume;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件实体
 * Created by Dean on 2017-7-18.
 */
@Entity
@Table(name = "t_upload_file")
public class UploadFile implements Serializable {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer parentId; //父id
    private String parentIds; //父id串
    private String name; //文件名称
    private String mime; //文件类型
    private String path; //文件物理路径
    private Long size; //文件大小（字节）
    private Integer isRead; //是否可读
    private Integer isWrite; //是否可写
    private Integer isLock; //是否锁定
    private Integer useTimes; //使用次数
    private Date createTime; //创建时间
    private Integer creator; //创建人
    private Date updateTime; //更新时间
    private Integer updater; //更新人

    /**
     * 文件夹路径类型
     * 1 REAL_PATH 真实物理路径
     * 2 VIRTUAL_PATH 数据库逻辑路径
     */
    public static class DirType{
        public static final int REAL_PATH = 1;
        public static final int VIRTUAL_PATH = 2;
    }

    public UploadFile() {
        this.isRead = 1;
        this.isWrite = 1;
        this.isLock = 0;
        this.useTimes = 0;
        this.size = 0L;
    }

    @Transient
    private List<UploadFile> children;

    public static FsVolume transVolume(UploadFile file){
        MysqlFsVolume fsVolume = new MysqlFsVolume();
        fsVolume.setName(file.getName());
        fsVolume.setRootDir(file);
        return fsVolume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getUpdater() {
        return updater;
    }

    public void setUpdater(Integer updater) {
        this.updater = updater;
    }

    public List<UploadFile> getChildren() {
        return children;
    }

    public void setChildren(List<UploadFile> children) {
        this.children = children;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(Integer isWrite) {
        this.isWrite = isWrite;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Integer getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(Integer useTimes) {
        this.useTimes = useTimes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
