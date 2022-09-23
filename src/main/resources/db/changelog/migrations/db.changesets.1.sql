--liquibase formatted sql

--changeset Stephania:database-initial-setup
create
extension if not exists "uuid-ossp";

create table cooking_machine
(
    id   bigserial primary key,
    code text not null unique
);

create table cook_rank
(
    id    bigserial primary key,
    value int not null unique
);

create table cook_proficiency
(
    id    bigserial primary key,
    value int not null unique
);

create table dish
(
    id                  bigserial primary key,
    code                text                               not null unique,
    preparation_time    numeric                            not null,
    cook_proficiency_id bigint references cook_proficiency not null,
    cooking_machine_id  bigint references cooking_machine
);

create table cook
(
    id                  bigserial primary key,
    code                text                               not null unique,
    given_name          text                               not null,
    last_name           text                               not null,
    catch_phrase        text,
    cook_rank_id        bigint references cook_rank        not null,
    cook_proficiency_id bigint references cook_proficiency not null,
    dish_lock_id        bigint references dish             not null
);

create table customer_order
(
    id           bigint primary key,
    waiter_id    bigint      not null,
    table_id     bigint      not null,
    priority     numeric     not null,
    max_wait     numeric     not null,
    pick_up_time timestamptz not null,
    distributed  boolean     not null default false
);

create table customer_order_dish
(
    customer_order_id bigint references customer_order not null,
    dish_id           bigint references dish           not null
);

insert into cook_proficiency(value)
values (1),
       (2),
       (3),
       (4);

insert into cooking_machine(code)
values ('OVEN'),
       ('STOVE');

insert into dish(code, preparation_time, cook_proficiency_id, cooking_machine_id)
values ('PIZZA', 20, 2, 1),
       ('SALAD', 10, 1, null),
       ('ZEAMA', 7, 1, 2),
       ('SCALLOP_SASHIMI_MEYER_LEMON', 32, 3, null),
       ('ISLAND_DUCK_MUSTARD', 35, 3, 1),
       ('WAFFLES', 10, 1, 2),
       ('AUBERGINE', 20, 2, 1),
       ('LASAGNA', 30, 2, 1),
       ('BURGER', 15, 1, 2),
       ('GYROS', 15, 1, null),
       ('KEBAB', 15, 1, null),
       ('UNAGI_MAKI', 20, 2, null),
       ('TOBACCO_CHICKEN', 30, 2, 1);
