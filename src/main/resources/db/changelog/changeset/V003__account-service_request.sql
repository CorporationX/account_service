create TABLE request (
   id UUID NOT NULL,
   context VARCHAR(255),
   request_status SMALLINT,
   scheduled_at TIMESTAMP WITHOUT TIME ZONE,
   request_task_id UUID NOT NULL,
   account_id BIGINT NOT NULL,
   balance_id BIGINT NOT NULL,
   CONSTRAINT pk_request PRIMARY KEY (id)
);

alter table request add CONSTRAINT FK_REQUEST_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

--alter table request add CONSTRAINT FK_REQUEST_ON_BALANCE FOREIGN KEY (balance_id) REFERENCES balance (id);
