create table if not exists TRANSACTION (
  ID			bigint not null auto_increment,
  TYPE			enum('PMT', 'EXP') not null,
  TRAN_DATE     date,
  DESCRIPTION	varchar(255),
  ANN_PAID 	    decimal(6,2),
  BOB_PAID 	    decimal(6,2),
  ARCHIVED      tinyint(1) default 0,
  MODIFIED_BY   varchar(20),
  MODIFIED_AT	timestamp not null,
  constraint PK_TRAN primary key(ID)
);

create index IDX_TYPE on TRANSACTION(TYPE);

create trigger TG_TRAN_INSERT before insert on TRANSACTION for each row set new.MODIFIED_BY = user();

create trigger TG_TRAN_UPDATE before update on TRANSACTION for each row set new.MODIFIED_BY = user();

