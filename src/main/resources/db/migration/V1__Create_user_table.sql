CREATE TABLE USER
(
  id           INTEGER AUTO_INCREMENT PRIMARY KEY,
  account_id  VARCHAR(100),
  NAME         VARCHAR(50),
  token        VARCHAR(36),
  gmt_create   BIGINT,
  gmt_modified BIGINT
);