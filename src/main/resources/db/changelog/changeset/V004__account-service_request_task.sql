CREATE TABLE request_task (
  id UUID NOT NULL,
   handler VARCHAR(1024),
   created_at TIMESTAMP WITHOUT TIME ZONE,
   request_id UUID,
   CONSTRAINT pk_request_task PRIMARY KEY (id)
);

ALTER TABLE request_task ADD CONSTRAINT FK_REQUEST_TASK_ON_REQUEST FOREIGN KEY (request_id) REFERENCES request (id);
