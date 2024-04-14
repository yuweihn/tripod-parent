# 分片配置示例

For example:
------------------------------------------------------------------------------------------------------------------
    tripod:
      boot:
        sharding:
          enabled: true
      sharding:
        tables:
          sys_user:   #对应物理表：sys_user_0000, sys_user_0001
            suffixLength: 4
            shardingSize: 2
          sys_role:   #对应物理表：sys_role_0000, sys_role_0001, sys_role_0002
            shardingSize: 3
------------------------------------------------------------------------------------------------------------------
