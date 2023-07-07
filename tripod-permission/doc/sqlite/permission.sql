
drop table if exists sys_permission;
create table sys_permission(
	id                        bigint(20)            not null,
	perm_no                   varchar(150)          not null,     -- comment '权限编码'
	title                     varchar(150)          null,         -- comment '权限标题'
    parent_id                 bigint(20)            null,         -- comment '上级权限ID(关联sys_permission.id)'
    order_num                 int(11)               not null,     -- comment '显示顺序'
    path                      varchar(255)          null,         -- comment '路由地址'
    component                 varchar(255)          null,         -- comment '组件路径'
    if_ext                    tinyint(1)            not null,     -- comment '是否为外链'
    perm_type                 char(1)               null,         -- comment '菜单类型(D目录; M菜单; B按钮)'
    visible                   tinyint(1)            not null,     -- comment '是否可见'
    icon                      varchar(100)          null,         -- comment '菜单图标'
	descr                     text                  null,         -- comment '描述'

	version                   int(11)               not null  default 0, -- comment '版本号'
	creator                   varchar(50)           not null  default 'system',   -- comment '创建人
	create_time               datetime              not null,             -- comment '创建时间'
	modifier                  varchar(50)           null,                 -- comment '修改人'
	modify_time               datetime              null,                 -- comment '修改时间'

	primary     key(id)
); --权限表
create unique index idx_perm_no on sys_permission(perm_no);
create index idx_parent_id on sys_permission(parent_id);
create index idx_perm_type on sys_permission(perm_type);

drop table if exists sys_role;
create table sys_role(
	id                     bigint(20)            not null,
	role_no                varchar(100)          not null,     -- comment '角色编码'
	role_name              varchar(100)          not null,     -- comment '角色名'

	version                int(11)              not null  default 0, -- comment '版本号'
	creator                varchar(50)          not null  default 'system',   -- comment '创建人
	create_time            datetime             not null,             -- comment '创建时间'
	modifier               varchar(50)          null,                 -- comment '修改人'
	modify_time            datetime             null,                 -- comment '修改时间'

	primary     key(id)
); --角色表
create unique index idx_role_no on sys_role(role_no);

drop table if exists sys_admin;
create table sys_admin(
	id                     bigint(20)           not null,
	account_no             varchar(100)         not null,     -- comment '账号'
	password               varchar(100)         not null,     -- comment '密码'
	real_name              varchar(100)         null,         -- comment '真实姓名'
	gender                 tinyint(2)           null,         -- comment '性别(1男，2女)'
	avatar                 varchar(150)         null,         -- comment '头像'
	last_login_time        datetime             null,         -- comment '最近登录时间'
	last_login_ip          varchar(50)          null,         -- comment '最近登录IP'

	version                int(11)              not null  default 0, -- comment '版本号'
	creator                varchar(50)          not null  default 'system',   -- comment '创建人
	create_time            datetime             not null,             -- comment '创建时间'
	modifier               varchar(50)          null,                 -- comment '修改人'
	modify_time            datetime             null,                 -- comment '修改时间'

	primary     key(id)
); --管理员账号表
create unique index idx_account_no on sys_admin(account_no);

drop table if exists sys_role_permission_rel;
create table sys_role_permission_rel(
	id                        bigint(20)            not null,
	role_id                   bigint(20)            not null,     -- comment '角色id(关联sys_role.id)'
	perm_id                   bigint(20)            not null,     -- comment '权限id(关联sys_permission.id)'

	version                int(11)              not null  default 0, -- comment '版本号'
	creator                varchar(50)          not null  default 'system',   -- comment '创建人
	create_time            datetime             not null,             -- comment '创建时间'
	modifier               varchar(50)          null,                 -- comment '修改人'
	modify_time            datetime             null,                 -- comment '修改时间'

	primary     key(id)
); --角色和权限的对应关系表
create unique index idx_role_perm on sys_role_permission_rel(role_id, perm_id);
create index idx_role_id on sys_role_permission_rel(role_id);
create index idx_perm_id on sys_role_permission_rel(perm_id);

drop table if exists sys_admin_role_rel;
create table sys_admin_role_rel(
	id                        bigint(20)            not null,
	admin_id                  bigint(20)            not null,     -- comment '管理员账号id(关联sys_admin.id)'
	role_id                   bigint(20)            not null,     -- comment '角色id(关联sys_role.id)'
	
	version                int(11)              not null  default 0, -- comment '版本号'
	creator                varchar(50)          not null  default 'system',   -- comment '创建人
	create_time            datetime             not null,             -- comment '创建时间'
	modifier               varchar(50)          null,                 -- comment '修改人'
	modify_time            datetime             null,                 -- comment '修改时间'

	primary     key(id)
); --管理员-角色关系表
create unique index idx_admin_role on sys_admin_role_rel(admin_id, role_id);
create index idx_admin_id on sys_admin_role_rel(admin_id);
create index idx_role_id2 on sys_admin_role_rel(role_id);



delete from sys_permission;
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(1, 'sys', '账号管理', null, 1, null, null, false, 'D', true, 'people', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(2, 'permission', '权限', 1, 1, '/sys/permission', 'admin/Permission', false, 'M', true, 'lock', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(3, 'role', '角色', 1, 2, '/sys/role', 'admin/Role', false, 'M', true, 'peoples', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(4, 'admin', '管理员', 1, 3, '/sys/admin', 'admin/Admin', false, 'M', true, 'user', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(5, 'sys.permission.list', '查询权限列表', 2, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(6, 'sys.permission.create', '添加权限', 2, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(7, 'sys.permission.update', '修改权限', 2, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(8, 'sys.permission.delete', '删除权限', 2, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(9, 'sys.role.list', '查询角色列表', 3, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(10, 'sys.role.info', '查询角色详情', 3, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(11, 'sys.role.create', '添加角色', 3, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(12, 'sys.role.update', '修改角色', 3, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(13, 'sys.role.delete', '删除角色', 3, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(14, 'sys.role.permission.list', '查询指定角色的权限集合', 3, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(15, 'sys.role.permission.save', '保存角色权限', 3, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(16, 'sys.admin.list', '查询管理员列表', 4, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(17, 'sys.admin.info', '查询管理员详情', 4, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(18, 'sys.admin.create', '添加管理员', 4, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(19, 'sys.admin.update', '更新管理员', 4, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(20, 'sys.admin.change.password', '设置密码', 4, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(21, 'sys.admin.gender.drop.down.list', '性别列表(下拉选择)', 4, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(22, 'sys.admin.delete', '删除管理员', 4, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(23, 'sys.admin.role.list', '管理员角色列表', 4, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(24, 'sys.admin.role.info', '查询指定的管理员角色', 4, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(25, 'sys.admin.role.create', '增加管理员角色', 4, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(26, 'sys.admin.role.update', '修改管理员角色', 4, 11, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(27, 'sys.admin.role.delete', '删除管理员角色', 4, 12, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));


insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(28, 'local', '本地配置', null, 2, null, null, false, 'D', true, 'system', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(29, 'cert', 'SSL证书', 28, 1, '/cert', 'local/cert/Cert', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(30, 'cert.list', '证书列表', 29, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(31, 'cert.dir.create', '增加证书子目录', 29, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(32, 'cert.upload', '上传证书', 29, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(33, 'cert.download', '下载证书', 29, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(34, 'cert.delete', '删除证书', 29, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(35, 'basic', '基础参数', 28, 2, '/basic', 'local/basic/Basic', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(36, 'basic.list', '基础参数列表', 35, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(37, 'basic.create', '添加基础参数', 35, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(38, 'basic.update', '修改基础参数', 35, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(39, 'basic.move.up', '上移基础参数', 35, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(40, 'basic.move.down', '下移基础参数', 35, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(41, 'basic.delete', '删除基础参数', 35, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(42, 'http', 'Http参数', 28, 3, '/http', 'local/http/Http', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(43, 'http.list', 'Http参数列表', 42, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(44, 'http.create', '添加Http参数', 42, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(45, 'http.update', '修改Http参数', 42, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(46, 'http.move.up', '上移Http参数', 42, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(47, 'http.move.down', '下移Http参数', 42, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(48, 'http.delete', '删除Http参数', 42, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(49, 'stream', 'Stream参数', 28, 4, '/stream', 'local/stream/Stream', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(50, 'stream.list', 'Stream参数列表', 49, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(51, 'stream.create', '添加Stream参数', 49, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(52, 'stream.update', '修改Stream参数', 49, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(53, 'stream.move.up', '上移Stream参数', 49, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(54, 'stream.move.down', '下移Stream参数', 49, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(55, 'stream.delete', '删除Stream参数', 49, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(56, 'template', '参数模板', 28, 5, '/template', 'local/template/Template', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(57, 'template.list', '参数模板列表', 56, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(58, 'template.drop.down.list', '参数模板列表(下拉选择)', 56, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(59, 'template.create', '添加参数模板', 56, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(60, 'template.update', '修改参数模板', 56, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(61, 'template.delete', '删除参数模板', 56, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(62, 'template.rec.list', '参数模板明细列表', 56, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(63, 'template.rec.create', '添加参数模板明细', 56, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(64, 'template.rec.update', '修改参数模板明细', 56, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(65, 'template.rec.move.up', '上移参数模板明细', 56, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(66, 'template.rec.move.down', '下移参数模板明细', 56, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(67, 'template.rec.delete', '删除参数模板明细', 56, 11, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(68, 'upstream', '负载均衡(Upstream)', 28, 6, '/upstream', 'local/upstream/Upstream', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(69, 'upstream.list', '负载均衡列表', 68, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(70, 'upstream.drop.down.list', '负载均衡列表(下拉选择)', 68, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(71, 'upstream.create', '添加负载均衡', 68, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(72, 'upstream.update', '修改负载均衡', 68, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(73, 'upstream.move.up', '上移负载均衡', 68, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(74, 'upstream.move.down', '下移负载均衡', 68, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(75, 'upstream.delete', '删除负载均衡', 68, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(76, 'upstream.proxy.type.drop.down.list', '代理类型下拉列表', 68, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(77, 'upstream.load.policy.drop.down.list', '负载策略下拉列表', 68, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(78, 'upstream.server.list', '负载节点列表', 68, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(79, 'upstream.server.create', '添加负载节点', 68, 11, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(80, 'upstream.server.update', '修改负载节点', 68, 12, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(81, 'upstream.server.status.post', '启用/停用负载节点', 68, 13, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(82, 'upstream.server.delete', '删除负载节点', 68, 14, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(83, 'upstream.param.list', '负载均衡参数列表', 68, 15, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(84, 'upstream.param.create', '添加负载均衡参数', 68, 16, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(85, 'upstream.param.batch.create', '批量添加负载均衡参数', 68, 17, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(86, 'upstream.param.update', '修改负载均衡参数', 68, 18, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(87, 'upstream.param.move.up', '上移负载均衡参数', 68, 19, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(88, 'upstream.param.move.down', '下移负载均衡参数', 68, 20, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(89, 'upstream.param.delete', '删除负载均衡参数', 68, 21, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(90, 'upstream.param.template.list', '负载均衡参数模板列表', 68, 22, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(91, 'upstream.param.template.create', '添加负载均衡参数模板', 68, 23, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(92, 'upstream.param.template.update', '修改负载均衡参数模板', 68, 24, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(93, 'upstream.param.template.move.up', '上移负载均衡参数模板', 68, 25, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(94, 'upstream.param.template.move.down', '下移负载均衡参数模板', 68, 26, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(95, 'upstream.param.template.delete', '删除负载均衡参数模板', 68, 27, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(96, 'server', '反向代理(Server)', 28, 7, '/server', 'local/server/Server', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(97, 'server.list', '反向代理列表', 96, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(98, 'server.create', '添加反向代理', 96, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(99, 'server.update', '修改反向代理', 96, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(100, 'server.if.redirect.https.post', '强制跳转HTTPS/不强制跳转HTTPS', 96, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(101, 'server.status.post', '启用/停用反向代理', 96, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(102, 'server.delete', '删除反向代理', 96, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(103, 'server.listen.list', '反向代理监听的端口列表', 96, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(104, 'server.listen.create', '添加反向代理监听的端口', 96, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(105, 'server.listen.update', '修改反向代理监听的端口', 96, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(106, 'server.listen.delete', '删除反向代理监听的端口', 96, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(107, 'server.location.list', '反向代理目标列表', 96, 11, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(108, 'server.location.create', '添加反向代理目标', 96, 12, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(109, 'server.location.update', '修改反向代理目标', 96, 13, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(110, 'server.location.delete', '删除反向代理目标', 96, 14, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(111, 'server.location.type.drop.down.list', '代理目标类型下拉列表', 96, 15, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(112, 'server.location.static.type.drop.down.list', '静态代理模式下拉列表', 96, 16, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(113, 'server.param.list', '反向代理参数列表', 96, 17, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(114, 'server.param.create', '添加反向代理参数', 96, 18, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(115, 'server.param.batch.create', '批量添加反向代理参数', 96, 19, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(116, 'server.param.update', '修改反向代理参数', 96, 20, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(117, 'server.param.move.up', '上移反向代理参数', 96, 21, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(118, 'server.param.move.down', '下移反向代理参数', 96, 22, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(119, 'server.param.delete', '删除反向代理参数', 96, 23, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(120, 'server.param.template.list', '反向代理参数模板列表', 96, 24, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(121, 'server.param.template.create', '添加反向代理参数模板', 96, 25, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(122, 'server.param.template.update', '修改反向代理参数模板', 96, 26, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(123, 'server.param.template.move.up', '上移反向代理参数模板', 96, 27, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(124, 'server.param.template.move.down', '下移反向代理参数模板', 96, 28, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(125, 'server.param.template.delete', '删除反向代理参数模板', 96, 29, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(126, 'server.location.param.list', '反向代理目标参数列表', 96, 30, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(127, 'server.location.param.create', '反向代理目标参数列表', 96, 31, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(128, 'server.location.param.batch.create', '批量添加反向代理目标参数', 96, 32, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(129, 'server.location.param.update', '修改反向代理目标参数', 96, 33, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(130, 'server.location.param.move.up', '上移反向代理目标参数', 96, 34, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(131, 'server.location.param.move.down', '下移反向代理目标参数', 96, 35, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(132, 'server.location.param.delete', '删除反向代理目标参数', 96, 36, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(133, 'server.location.param.template.list', '反向代理目标参数模板列表', 96, 37, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(134, 'server.location.param.template.create', '添加反向代理目标参数模板', 96, 38, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(135, 'server.location.param.template.update', '修改反向代理目标参数模板', 96, 39, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(136, 'server.location.param.template.move.up', '上移反向代理目标参数模板', 96, 40, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(137, 'server.location.param.template.move.down', '下移反向代理目标参数模板', 96, 41, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(138, 'server.location.param.template.delete', '删除反向代理目标参数模板', 96, 42, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(139, 'conf', '生成Conf', 28, 8, '/conf', 'local/conf/Conf', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(140, 'conf.setting', '获取Conf', 139, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(141, 'conf.content.source', '获取SourceConf内容', 139, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(142, 'conf.content.target', '获取TargetConf内容', 139, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(143, 'conf.setting.nginxexe', '保存NginxExe设置', 139, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(144, 'conf.setting.decompose', '保存Decompose设置', 139, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(145, 'conf.setting.delete', '删除Conf相关设置', 139, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(146, 'conf.replace', '替换目标文件', 139, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(147, 'conf.check', '检查Conf', 139, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(148, 'conf.reload', '重新载入Conf', 139, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(149, 'conf.exe.cmd.info', '获取Conf执行命令', 139, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(150, 'conf.nginx.status', '检查Nginx运行状态', 139, 11, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(151, 'conf.cmd.execute', '执行Nginx命令', 139, 12, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));

insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(152, 'bak', '系统备份', 28, 9, '/bak', 'local/bak/Bak', false, 'M', true, null, null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(153, 'back.list', '获取Conf备份列表', 152, 1, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(154, 'back.do', '备份', 152, 2, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(155, 'back.content', '获取Conf备份内容', 152, 3, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(156, 'back.download', '下载Conf备份内容', 152, 4, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(157, 'back.restore', '还原Conf设置', 152, 5, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(158, 'back.delete', '删除Conf备份', 152, 6, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(159, 'back.all.delete', '删除全部Conf备份', 152, 7, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(160, 'back.restart', '重启应用', 152, 8, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(161, 'back.import.db', '导入数据库', 152, 9, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));
insert into sys_permission(id, perm_no, title, parent_id, order_num, path, component, if_ext, perm_type, visible, icon, descr, creator, create_time)
    values(162, 'back.resume.setting', '恢复出厂设置', 152, 10, null, null, false, 'B', true, '#', null, 'system', datetime('now', 'localtime'));


delete from sys_role;
insert into sys_role(id, role_no, role_name, creator, create_time) values(1, 'administrator', '系统管理员', 'system', datetime('now', 'localtime'));


delete from sys_role_permission_rel;
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(1, 1, 1, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(2, 1, 2, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(3, 1, 3, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(4, 1, 4, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(5, 1, 5, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(6, 1, 6, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(7, 1, 7, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(8, 1, 8, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(9, 1, 9, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(10, 1, 10, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(11, 1, 11, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(12, 1, 12, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(13, 1, 13, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(14, 1, 14, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(15, 1, 15, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(16, 1, 16, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(17, 1, 17, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(18, 1, 18, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(19, 1, 19, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(20, 1, 20, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(21, 1, 21, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(22, 1, 22, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(23, 1, 23, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(24, 1, 24, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(25, 1, 25, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(26, 1, 26, 'system', datetime('now', 'localtime'));
insert into sys_role_permission_rel(id, role_id, perm_id, creator, create_time) values(27, 1, 27, 'system', datetime('now', 'localtime'));

/************************************************************************************************************
*********明文**12345*****密文**827ccb0eea8a706c4c34a16891f84e7b*************
 ************************************************************************************************************/
delete from sys_admin;
insert into sys_admin(id, account_no, password, real_name, gender, create_time) values(1, 'yuwei', '827ccb0eea8a706c4c34a16891f84e7b', '超级管理员', 1, datetime('now', 'localtime'));


delete from sys_admin_role_rel;
insert into sys_admin_role_rel(id, admin_id, role_id, creator, create_time) values(1, 1, 1, 'system', datetime('now', 'localtime'));


