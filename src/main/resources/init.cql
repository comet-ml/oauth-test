CREATE KEYSPACE IF NOT EXISTS DB-NAME
WITH REPLICATION = {
  'class':'SimpleStrategy',
  'replication_factor':3
};

USE DB-NAME;

CREATE TABLE IF NOT EXISTS test_connection
(
  foo TEXT,
  bar TEXT,
  PRIMARY KEY (foo)
);

TRUNCATE test_connection;