
drop table if exists sys_permission;
create table sys_permission(
	id                        bigint(20) unsigned   not null,
	perm_no                   varchar(150)          not null     comment '权限编码',
	title                     varchar(150)          null         comment '权限标题',
    parent_id                 bigint(20)            null         comment '上级权限ID(关联sys_permission.id)',
    order_num                 int(11)               not null     comment '显示顺序',
    path                      varchar(255)          null         comment '路由地址',
    component                 varchar(255)          null         comment '组件路径',
    if_ext                    tinyint(1)            not null     comment '是否为外链',
    perm_type                 char(1)               null         comment '菜单类型(D目录; M菜单; B按钮)',
    visible                   tinyint(1)            not null     comment '是否可见',
    icon                      varchar(100)          null         comment '菜单图标',
	descr                     text                  null         comment '描述',

	version                   int(11)               not null   default 0  comment '版本号',
	creator                   varchar(50)           not null   default 'system'  comment '创建人',
	create_time               datetime              not null     comment '创建时间',
	modifier                  varchar(50)           null         comment '修改人',
	modify_time               datetime              null         comment '修改时间',

	primary     key(id),
	unique idx_perm_no(perm_no),
	index idx_parent_id(parent_id),
	index idx_perm_type(perm_type)
)engine=innodb default charset=utf8mb4 comment='权限表';


drop table if exists sys_role;
create table sys_role(
	id                        bigint(20) unsigned   not null,
	role_no                   varchar(100)          not null     comment '角色编码',
	role_name                 varchar(100)          not null     comment '角色名',

	version                   int(11)               not null   default 0  comment '版本号',
	creator                   varchar(50)           not null   default 'system'  comment '创建人',
	create_time               datetime              not null     comment '创建时间',
	modifier                  varchar(50)           null         comment '修改人',
	modify_time               datetime              null         comment '修改时间',

	primary     key(id),
	unique idx_role_no(role_no)
)engine=innodb default charset=utf8mb4 comment='角色表';

drop table if exists sys_admin;
create table sys_admin(
	id                     bigint(20) unsigned  not null,
	account_no             varchar(100)         not null     comment '账号',
	password               varchar(100)         not null     comment '密码',
	real_name              varchar(100)         null         comment '真实姓名',
	gender                 tinyint(2)           null         comment '性别(1男，2女)',
	avatar                 varchar(150)         null         comment '头像',
	last_login_time        datetime             null         comment '最近登录时间',
	last_login_ip          varchar(50)          null         comment '最近登录IP',

	version                int(11)              not null  default 0  comment '版本号',
	creator                varchar(50)          not null  default 'system'   comment '创建人',
	create_time            datetime             not null             comment '创建时间',
	modifier               varchar(50)          null                 comment '修改人',
	modify_time            datetime             null                 comment '修改时间',

	primary     key(id),
	unique idx_account_no(account_no)
)engine=innodb default charset=utf8mb4 comment='管理员账号表';

drop table if exists sys_role_permission_rel;
create table sys_role_permission_rel(
	id                        bigint(20) unsigned   not null,
	role_id                   bigint(20)            not null     comment '角色id(关联sys_role.id)',
	perm_id                   bigint(20)            not null     comment '权限id(关联sys_permission.id)',

	version                   int(11)               not null   default 0  comment '版本号',
	creator                   varchar(50)           not null   default 'system'  comment '创建人',
	create_time               datetime              not null     comment '创建时间',
	modifier                  varchar(50)           null         comment '修改人',
	modify_time               datetime              null         comment '修改时间',

	primary     key(id),
	unique idx_role_perm(role_id, perm_id),
	index idx_role_id(role_id),
	index idx_perm_id(perm_id)
)engine=innodb default charset=utf8mb4 comment='角色和权限的对应关系表';

drop table if exists sys_admin_role_rel;
create table sys_admin_role_rel(
	id                        bigint(20) unsigned   not null,
	admin_id                  bigint(20)            not null     comment '管理员账号id(关联sys_admin.id)',
	role_id                   bigint(20)            not null     comment '角色id(关联sys_role.id)',

	version                   int(11)               not null   default 0  comment '版本号',
	creator                   varchar(50)           not null   default 'system'  comment '创建人',
	create_time               datetime              not null     comment '创建时间',
	modifier                  varchar(50)           null         comment '修改人',
	modify_time               datetime              null         comment '修改时间',

	primary     key(id),
	unique idx_admin_role(admin_id, role_id),
	index idx_admin_id(admin_id),
	index idx_role_id(role_id)
)engine=innodb default charset=utf8mb4 comment='管理员-角色关系表';



truncate table sys_permission;
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(1, 'sys', '账号管理', null, 1, null, null, false, 'D', true, 'people', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(2, 'permission', '权限', 1, 1, '/sys/permission', 'admin/Permission', false, 'M', true, 'lock', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(3, 'role', '角色', 1, 2, '/sys/role', 'admin/Role', false, 'M', true, 'peoples', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(4, 'admin', '管理员', 1, 3, '/sys/admin', 'admin/Admin', false, 'M', true, 'user', null, 'system', now());

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(5, 'sys.permission.list', '查询权限列表', 2, 1, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(6, 'sys.permission.create', '添加权限', 2, 2, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(7, 'sys.permission.update', '修改权限', 2, 3, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(8, 'sys.permission.delete', '删除权限', 2, 4, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(9, 'sys.permission.export', '导出权限', 2, 5, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(10, 'sys.permission.import', '导入权限', 2, 6, null, null, false, 'B', true, '#', null, 'system', now());

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(11, 'sys.role.list', '查询角色列表', 3, 1, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(12, 'sys.role.info', '查询角色详情', 3, 2, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(13, 'sys.role.create', '添加角色', 3, 3, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(14, 'sys.role.update', '修改角色', 3, 4, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(15, 'sys.role.delete', '删除角色', 3, 5, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(16, 'sys.role.permission.list', '查询指定角色的权限集合', 3, 6, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(17, 'sys.role.permission.save', '保存角色权限', 3, 7, null, null, false, 'B', true, '#', null, 'system', now());

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(18, 'sys.admin.list', '查询管理员列表', 4, 1, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(19, 'sys.admin.info', '查询管理员详情', 4, 2, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(20, 'sys.admin.create', '添加管理员', 4, 3, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(21, 'sys.admin.update', '更新管理员', 4, 4, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(22, 'sys.admin.change.password', '设置密码', 4, 5, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(23, 'sys.admin.gender.drop.down.list', '性别列表(下拉选择)', 4, 6, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(24, 'sys.admin.delete', '删除管理员', 4, 7, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(25, 'sys.admin.role.list', '管理员角色列表', 4, 8, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(26, 'sys.admin.role.info', '查询指定的管理员角色', 4, 9, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(27, 'sys.admin.role.create', '增加管理员角色', 4, 10, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(28, 'sys.admin.role.update', '修改管理员角色', 4, 11, null, null, false, 'B', true, '#', null, 'system', now());
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(29, 'sys.admin.role.delete', '删除管理员角色', 4, 12, null, null, false, 'B', true, '#', null, 'system', now());


truncate table sys_role;
insert into sys_role(id, role_no, role_name, creator, create_time) values(1, 'administrator', '系统管理员', 'system', now());


truncate table sys_role_permission_rel;
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(1, 1, 1, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(2, 1, 2, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(3, 1, 3, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(4, 1, 4, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(5, 1, 5, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(6, 1, 6, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(7, 1, 7, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(8, 1, 8, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(9, 1, 9, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(10, 1, 10, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(11, 1, 11, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(12, 1, 12, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(13, 1, 13, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(14, 1, 14, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(15, 1, 15, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(16, 1, 16, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(17, 1, 17, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(18, 1, 18, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(19, 1, 19, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(20, 1, 20, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(21, 1, 21, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(22, 1, 22, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(23, 1, 23, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(24, 1, 24, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(25, 1, 25, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(26, 1, 26, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(27, 1, 27, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(28, 1, 28, 'system', now());
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(29, 1, 29, 'system', now());

/************************************************************************************************************
*********明文**12345*****密文**827ccb0eea8a706c4c34a16891f84e7b*************
 ************************************************************************************************************/
truncate table sys_admin;
insert into sys_admin(id, account_no, password, real_name, gender, create_time) values(1, 'yuwei', '827ccb0eea8a706c4c34a16891f84e7b', '超级管理员', 1, now());


truncate table sys_admin_role_rel;
insert into sys_admin_role_rel(id, admin_id, role_id, creator, create_time) values(1, 1, 1, 'system', now());


