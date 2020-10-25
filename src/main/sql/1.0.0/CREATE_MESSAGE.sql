create table hive.message
(
    id text not null
        constraint message_pk
            primary key,
    channel_id text not null,
    guild_id text not null,
    member_id text not null,
    has_embed boolean default false not null,
    timestamp timestamp with time zone not null
);

