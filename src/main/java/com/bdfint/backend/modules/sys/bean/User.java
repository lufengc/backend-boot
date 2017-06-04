/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.bdfint.backend.modules.sys.bean;

import com.bdfint.backend.framework.common.DataEntity;
import com.bdfint.backend.framework.common.Global;
import com.bdfint.backend.framework.util.Collections3;
import com.bdfint.backend.framework.util.excel.ExcelField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 用户Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "sys_user")
public class User extends DataEntity<User> {

    private static final long serialVersionUID = 1L;
    private String companyId;    // 归属公司
    private String officeId;    // 归属部门
    private String loginName;// 登录名
    private String password;// 密码
    private String no;        // 工号
    private String name;    // 姓名
    private String email;    // 邮箱
    private String phone;    // 电话
    private String mobile;    // 手机
    private String userType;// 用户类型
    private String loginIp;    // 最后登陆IP
    private Date loginDate;    // 最后登陆日期
    private String loginFlag;    // 是否允许登陆
    private String photo;    // 头像

    @Transient
    private String oldLoginName;// 原登录名
    @Transient
    private String newPassword;    // 新密码
    @Transient
    private String oldLoginIp;    // 上次登陆IP
    @Transient
    private Date oldLoginDate;    // 上次登陆日期

    @Transient
    private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

    @Transient
    private String roleName;
    @Transient
    private String officeName;
    @Transient
    private String companyName;

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";
    public static final String DEL_FLAG_LOCK = "3";

    public User() {
        super();
        this.loginFlag = Global.YES;
    }

    public User(String id) {
        super(id);
    }

    public User(String id, String loginName) {
        super(id);
        this.loginName = loginName;
    }

    public User(Role role) {
        super();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    @ExcelField(title = "ID", type = 1, align = 2, sort = 1)
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    @Length(min = 1, max = 100, message = "登录名长度必须介于 1 和 100 之间")
    @ExcelField(title = "登录名", align = 2, sort = 30)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @JsonIgnore
    @Length(min = 1, max = 100, message = "密码长度必须介于 1 和 100 之间")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Length(min = 1, max = 100, message = "姓名长度必须介于 1 和 100 之间")
    @ExcelField(title = "姓名", align = 2, sort = 40)
    public String getName() {
        return name;
    }

    @Length(min = 1, max = 100, message = "工号长度必须介于 1 和 100 之间")
    @ExcelField(title = "工号", align = 2, sort = 45)
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Email(message = "邮箱格式不正确")
    @Length(min = 0, max = 200, message = "邮箱长度必须介于 1 和 200 之间")
    @ExcelField(title = "邮箱", align = 1, sort = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(min = 0, max = 200, message = "电话长度必须介于 1 和 200 之间")
    @ExcelField(title = "电话", align = 2, sort = 60)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(min = 0, max = 200, message = "手机长度必须介于 1 和 200 之间")
    @ExcelField(title = "手机", align = 2, sort = 70)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @ExcelField(title = "备注", align = 1, sort = 900)
    public String getRemarks() {
        return remarks;
    }

    @Length(min = 0, max = 100, message = "用户类型长度必须介于 1 和 100 之间")
    @ExcelField(title = "用户类型", align = 2, sort = 80, dictType = "sys_user_type")
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @ExcelField(title = "创建时间", type = 0, align = 1, sort = 90)
    public Date getCreateDate() {
        return createDate;
    }

    @ExcelField(title = "最后登录IP", type = 1, align = 1, sort = 100)
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelField(title = "最后登录日期", type = 1, align = 1, sort = 110)
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getOldLoginName() {
        return oldLoginName;
    }

    public void setOldLoginName(String oldLoginName) {
        this.oldLoginName = oldLoginName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldLoginIp() {
        if (oldLoginIp == null) {
            return loginIp;
        }
        return oldLoginIp;
    }

    public void setOldLoginIp(String oldLoginIp) {
        this.oldLoginIp = oldLoginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOldLoginDate() {
        if (oldLoginDate == null) {
            return loginDate;
        }
        return oldLoginDate;
    }

    public void setOldLoginDate(Date oldLoginDate) {
        this.oldLoginDate = oldLoginDate;
    }

    @JsonIgnore
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonIgnore
    public List<String> getRoleIdList() {
        List<String> roleIdList = Lists.newArrayList();
        for (Role role : roleList) {
            roleIdList.add(role.getId());
        }
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        roleList = Lists.newArrayList();
        for (String roleId : roleIdList) {
            Role role = new Role();
            role.setId(roleId);
            roleList.add(role);
        }
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    public String getRoleNames() {
        return Collections3.extractToString(roleList, "name", ",");
    }

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(String id) {
        return id != null && "1".equals(id);
    }

    @Override
    public String toString() {
        return id;
    }
}