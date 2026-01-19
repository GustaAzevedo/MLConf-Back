-- Schema inicial do domínio de conferência

create table conference_session (
    id uuid primary key,
    status varchar(20) not null,
    created_at timestamp not null,
    finalized_at timestamp null
);

create table conference_item (
    id bigserial primary key,
    session_id uuid not null,
    package_id varchar(80) not null,
    state varchar(20) not null,
    issue_category varchar(50),
    constraint fk_conference_item_session foreign key (session_id) references conference_session(id),
    constraint uq_conference_item_session_package unique (session_id, package_id)
);

create index idx_conference_item_session on conference_item(session_id);
