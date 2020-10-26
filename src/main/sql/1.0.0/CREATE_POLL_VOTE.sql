create table polling.poll_vote
(
    id serial not null
        constraint poll_vote_pk
            primary key,
    voter_member_id text not null,
    poll_option_id integer not null
        constraint poll_vote_poll_option_id_fk
            references polling.poll_option
            on delete cascade
);


