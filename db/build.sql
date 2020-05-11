use RPCdb;

drop table users;

create table users
(
    u_name     nvarchar(40),
    u_password nvarchar(40),
	cash int,
	failed int,
    primary key (u_name)
);

insert into users
values ('A', 'aa', 100000,0);
insert into users
values ('B', 'bb', 5,0);

create table logs
(
	t nvarchar(100),
	u_name nvarchar(40),
	act nvarchar(100),
    primary key (t,u_name,act)
)

update users set failed=0 where u_name='A';
select * from users;
select * from logs;