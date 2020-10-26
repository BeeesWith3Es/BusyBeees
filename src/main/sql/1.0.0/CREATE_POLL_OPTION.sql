create table polling.poll_option
(
    id serial not null
        constraint poll_option_pk
            primary key,
    poll_id text not null
        constraint poll_option_poll_message_id_fk
            references polling.poll
            on delete cascade,
    emote text not null,
    option text not null
);


