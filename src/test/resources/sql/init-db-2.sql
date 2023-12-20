CREATE TABLE "dictionary"
(
    id int NOT NULL,
    "name" varchar(255)  NOT NULL,
    primary key (id),
    unique ("name")
);

CREATE TABLE dictionary_value
(
    id int NOT NULL,
    code          varchar(255)  NOT NULL,
    value         varchar(255)  NOT NULL,
    dictionary_id int NULL references "dictionary" (id),
    primary key (id)
);