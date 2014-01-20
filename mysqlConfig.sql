-- MySQL script to create the default user accounts

-- Test database
CREATE USER 'imstest'@'localhost' identified by 'imstest';
CREATE DATABASE imstest;
GRANT ALTER, CREATE, DROP, DELETE, INDEX, SELECT, INSERT, UPDATE on imstest.* TO 'imstest'@'localhost';

-- Default IMS database
CREATE USER 'ims'@'localhost' identified by 'ims';
CREATE DATABASE ims;
GRANT ALTER, CREATE, DROP, DELETE, INDEX, SELECT, INSERT, UPDATE, ALTER on ims.* TO 'ims'@'localhost';
