create table hive.bot_info
(
	access_token text,
	username text not null,
	client_id text not null
);

create unique index bot_info_access_token_uindex
	on hive.bot_info (access_token);

create unique index bot_info_client_id_uindex
	on hive.bot_info (client_id);

alter table hive.bot_info
	add constraint bot_info_pk
		primary key (access_token);
