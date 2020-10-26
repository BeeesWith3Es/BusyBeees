create table polling.poll
(
    message_id text not null
        constraint poll_pk
            primary key,
    pollster_member_id text not null,
    end_time timestamp with time zone not null,
    vote_limit integer,
    closed boolean default false not null,
    created_at timestamp with time zone not null
);