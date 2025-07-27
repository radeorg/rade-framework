package org.dows.rade.aac;

import java.util.List;


public interface AacUser {

    // 账号ID
    Long getAccountId();

    // 用户ID
    Long getUserId();
    // 所在组织根节点ID
    Long getOrgRootId();
    // 所在组织节点ID
    Long getOrgTreeId();
    // 应用ID
    String getAppId();
    // 组织空间名称
    String getNameSpace();
    // 角色集ID
    List<Long> getRoleIds();
    //void setRoleIds(List<Long> roleIds);

    // 账号类型
    List<Integer> getAccountTypes();
    //void setAccountTypes(List<Integer> accountTypes);

    // 超级账号
    boolean isSuperAccount();

    //账号名
    String getNickname();

    //用户名
    String getUsername();

    // 头像
    String getAvatar();

    //手机
    String getTelephone();
    //void setPhone(String phone);

    // 邮箱
    String getEmail();
    //void setEmail(String email);
    // 手机号绑定
    Integer getState();

    // 获取账号标识类型[weixin, qq, weibo, email...]
    Integer getIdentifierType();
}
