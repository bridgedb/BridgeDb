-- CREATE Tables for a Derby Database for PathVisio
-- Run this .sql file in ij to create the tables 
-- Read PathVisio_README for detailed build process
-- A.Pico 20070817


-- Create 'datanode'
DROP TABLE datanode;
CREATE TABLE datanode (
	id VARCHAR(50) NOT NULL, 
	code VARCHAR(50) NOT NULL, 
	PRIMARY KEY (id, code)
	);

-- Create 'link'
DROP TABLE link;
CREATE TABLE link (
	idLeft VARCHAR(50) NOT NULL, 
	codeLeft VARCHAR(50) NOT NULL, 
	idRight VARCHAR(50) NOT NULL, 
	codeRight VARCHAR(50) NOT NULL, 
	PRIMARY KEY (idLeft, codeLeft, idRight, codeRight)
	);

-- Create 'attribute'
DROP TABLE attribute;
CREATE TABLE attribute (
	id VARCHAR(50) NOT NULL, 
	code VARCHAR(50) NOT NULL, 
	attrName VARCHAR(50) NOT NULL,
        attrValue VARCHAR(255) NOT NULL
	);

-- Create 'info' 
DROP TABLE info;
CREATE TABLE info (
  buildDate INTEGER,
  schemaVersion INTEGER
  sourceName VARCHAR(31) NOT NULL,
  sourceVersion VARCHAR(31) NOT NULL,
  species VARCHAR(31) NOT NULL,
  dataType VARCHAR(31) NOT NULL
);


