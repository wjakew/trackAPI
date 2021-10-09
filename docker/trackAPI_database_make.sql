-- makefile for mysql database for trackAPI
-- by Jakub Wawak 2021
-- kubawawak@gmail.com
-- all rights reserved
USE trackapi_database;
-- database drop and reload
drop table if exists PROGRAMCODES;
drop table if exists PROGRAM_LOG;

drop table if exists ISSUE;
drop table if exists TASK;
drop table if exists SHARED_ELEMENTS;
drop table if exists PROJECT;
drop table if exists LOG_HISTORY;
drop table if exists PRIVILAGES;
drop table if exists SESSION_TOKEN;
drop table if exists TOKEN;
drop table if exists OBJECT_HISTORY;
drop table if exists BOARD_ELEMENT;
drop table if exists BOARD;
drop table if exists USER_DATA;


-- maintanance tables
-- table for storing program data (versions, variables etc)
CREATE TABLE PROGRAMCODES
(
  programcodes_key VARCHAR(100),
  programcodes_values VARCHAR(100)
);
-- table for storing program log (errors etc)
CREATE TABLE PROGRAM_LOG
(
  program_log_id INT AUTO_INCREMENT PRIMARY KEY,
  program_log_code VARCHAR(30),
  program_log_desc VARCHAR(300)
);
-- data tables and structure

-- table for storing user data
CREATE TABLE USER_DATA
(
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(150),
  user_surname VARCHAR(200),
  user_email VARCHAR(200),
  user_login VARCHAR(25),
  user_password VARCHAR(50),
  user_category VARCHAR(100) -- CODES: ADMIN,DEVELOPER,CLIENT
);
-- table for storing object story
CREATE TABLE OBJECT_HISTORY
(
history_id INT PRIMARY KEY AUTO_INCREMENT,
user_id INT,
history_category VARCHAR(50), -- ISSUE,TASK
history_object_id INT,
history_desc VARCHAR(250),
history_object_time TIMESTAMP,

constraint  fk_objecthistory FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing auth token
CREATE TABLE TOKEN
(
  token_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  token_value VARCHAR(100),
  
  CONSTRAINT fk_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing session tokens
CREATE TABLE SESSION_TOKEN
(
  session_token_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  session_token VARCHAR(20),
  session_token_time TIMESTAMP,
  
  CONSTRAINT fk_session_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing user privilages
CREATE TABLE PRIVILAGES
(
  privilages_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  privilages_project_list VARCHAR(100),

  CONSTRAINT fk_privilages FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);

-- table for storing log data ( user moves on data and server )
CREATE TABLE LOG_HISTORY
(
  log_history_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  log_history_code VARCHAR(100),
  log_history_desc VARCHAR(300),

  CONSTRAINT fk_log_history FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing project data
CREATE TABLE PROJECT
(
  project_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  project_name VARCHAR(250),
  project_desc TEXT,
  project_creation_date TIMESTAMP,
  project_state VARCHAR(100), -- CODES: active, unactive, date ( time to finish )

  CONSTRAINT fk_project FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing shared projects
CREATE TABLE SHARED_ELEMENTS
(
    user_id INT,
    project_id INT,

    constraint fk_sharedelements FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
    constraint fk_sharedelements2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
-- table for storing task data
CREATE TABLE TASK
(
  task_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  project_id INT,
  task_name VARCHAR(200),
  task_desc TEXT,
  task_priority INT, -- VALUES FROM 1 TO 5
  task_state VARCHAR(100), -- CODES: UNDONE, DONE, date ( time to finish )

  CONSTRAINT fk_task FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
  CONSTRAINT fk_task2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
-- table for storing issue data
CREATE TABLE ISSUE
(
  issue_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  project_id INT,
  issue_name VARCHAR(200),
  issue_desc TEXT,
  issue_priority INT, -- VALUES FROM 1 TO 5
  issue_group INT, -- field for setting states as UNTOUCHED 0 PLANNED 1 IN WORK 2 DONE 3 VALUES FROM 0 TO 3
  issue_state VARCHAR(100), -- CODES: DONE, UNDONE, date ( time to finish )
  issue_time_creation TIMESTAMP,
  issue_time_due TIMESTAMP,

  CONSTRAINT fk_issue FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
  CONSTRAINT fk_issue2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
-- table for storing board data
CREATE TABLE BOARD
(
    board_id INT PRIMARY KEY AUTO_INCREMENT,
    board_name VARCHAR(100),
    user_id INT,
    board_desc VARCHAR(200),
    board_time TIMESTAMP,

    CONSTRAINT fk_board FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing board elements
CREATE TABLE BOARD_ELEMENT
(
    board_element_id INT PRIMARY KEY AUTO_INCREMENT,
    board_list_object VARCHAR(10),
    object_id INT,
    board_id INT,

    CONSTRAINT  fk_boardelement FOREIGN KEY (board_id) REFERENCES BOARD(board_id)
);
-- startup inserts
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_values)
VALUES
('DATABASEVERSION','100');

INSERT INTO USER_DATA
(user_name,user_surname,user_email,user_login,user_password,user_category)
VALUES
('admin','admin','none','admin','21232f297a57a5a743894a0e4a801fc3','ADMIN');
-- with every user creation blank project gonna be created
INSERT INTO TOKEN
(user_id,token_value)
VALUES
(1,'testtoken');
INSERT INTO PROGRAMCODES
(programcodes_key, programcodes_values)
VALUES
('service_tag','servicexd');