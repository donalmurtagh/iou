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

-- create some test data
-- insert into TRANSACTION (type, tran_date, description, ANN_PAID, BOB_PAID, archived)
	-- values ("PMT", now(), "Paid to Bob", 20, 0, 0);
-- insert into TRANSACTION (type, tran_date, description, ANN_PAID, BOB_PAID, archived)
	-- values ("PMT", now(), "Paid to Ann", 0, 40, 0);
-- insert into TRANSACTION (type, tran_date, description, ANN_PAID, BOB_PAID, archived)
	-- values ("EXP", now(), "Paid by Bob", 0, 100, 0);
-- insert into TRANSACTION (type, tran_date, description, ANN_PAID, BOB_PAID, archived)
	-- values ("EXP", now(), "Paid by Ann", 80, 0, 0);