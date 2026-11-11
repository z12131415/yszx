package com.yunshu.system.domain;

import java.time.LocalDateTime;

public class SysMenu {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String permissionCode;
    private Integer sortOrder;
    private Integer menuType;
    private Integer visible;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
