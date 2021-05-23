create table file_metadata
(
    id int auto_increment,
    last_update_time datetime not null,
    name varchar(255) not null,
    size int not null,
    extension varchar(255) null,
    constraint file_metadata_pk
        primary key (id)
);

create unique index file_metadata_name_uindex
    on file_metadata (name);

alter table file_metadata modify name varchar(255) not null;

create unique index file_metadata_name_uindex
	on file_metadata (name);


