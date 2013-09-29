create table if not exists TRANSACTION (
  ID			bigint not null auto_increment,
  TYPE			enum('PMT', 'EXP') not null,
  TRAN_DATE     date,
  DESCRIPTION	varchar(255),
  MAUDE_PAID 	decimal(6,2),
  DONAL_PAID 	decimal(6,2),
  ARCHIVED      tinyint(1) default 0,
  MODIFIED_BY   varchar(20),
  MODIFIED_AT	timestamp not null,
  constraint PK_TRAN primary key(ID)
);


-- this might help performance, but it might hinder it too
create index IDX_TYPE on TRANSACTION(TYPE);


create trigger TG_TRAN_INSERT before insert on TRANSACTION
for each row set new.MODIFIED_BY = user();

create trigger TG_TRAN_UPDATE before update on TRANSACTION
for each row set new.MODIFIED_BY = user();

-- create some test data
-- insert into TRANSACTION (type, tran_date, description, maude_paid, donal_paid, archived)
	-- values ("PMT", now(), "Paid to Donal", 20, 0, 0);
-- insert into TRANSACTION (type, tran_date, description, maude_paid, donal_paid, archived)
	-- values ("PMT", now(), "Paid to Maude", 0, 40, 0);
-- insert into TRANSACTION (type, tran_date, description, maude_paid, donal_paid, archived)
	-- values ("EXP", now(), "Paid by Donal", 0, 100, 0);
-- insert into TRANSACTION (type, tran_date, description, maude_paid, donal_paid, archived)
	-- values ("EXP", now(), "Paid by Maude", 80, 0, 0);

