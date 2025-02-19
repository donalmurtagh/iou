create table if not exists transaction (
  id			bigint not null auto_increment,
  type			enum('PMT', 'EXP') not null,
  tran_date     date,
  description	varchar(255),
  ann_paid 	    decimal(6,2),
  bob_paid 	    decimal(6,2),
  archived      tinyint(1) default 0,
  modified_by   varchar(20),
  modified_at	timestamp not null default current_timestamp,
  primary key(id),
  index type_index (type)
);

create trigger if not exists transaction_insert
    before insert on transaction for each row set new.modified_by = user();

create trigger if not exists transaction_update
    before update on transaction for each row set new.modified_by = user();

