drop table if exists t_user;
create table t_user
(
	id int AUTO_INCREMENT primary key,
	member_id int not null COMMENT '会员号',
	user_name varchar(30) not null comment '用户名',
	timeCreated datetime default CURRENT_TIMESTAMP,
	timeModified datetime default current_timestamp on update current_timestamp,
	unique key uk_member_id (member_id)
);
