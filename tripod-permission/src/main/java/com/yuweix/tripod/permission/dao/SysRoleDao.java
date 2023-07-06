package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.Dao;
import com.yuweix.tripod.permission.model.SysRole;


/**
 * @author yuwei
 */
public interface SysRoleDao extends Dao<SysRole, Long> {
    SysRole queryRoleByNo(String roleNo);
    void deleteRoleByNoFromCache(String roleNo);
}
