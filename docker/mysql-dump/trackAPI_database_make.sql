-- makefile for mysql database for trackAPI
-- by Jakub Wawak 2022
-- kubawawak@gmail.com
-- all rights reserved
CREATE DATABASE IF NOT EXISTS trackapi_database;
USE trackapi_database;
SET SQL_MODE='ALLOW_INVALID_DATES';
-- database drop and reload
drop table if exists PROGRAMCODES;
drop table if exists PROGRAM_LOG;
drop table if exists WHO_TABLE;
drop table if exists ISSUE;
drop table if exists TASK_COMMENT;
drop table if exists TASK;
drop table if exists SHARED_ELEMENTS;
drop table if exists PROJECT_MEMBERS;
drop table if exists PROJECT;
drop table if exists LOG_HISTORY;
drop table if exists PRIVILAGES;
drop table if exists CONNECTION_LOG;
drop table if exists SESSION_WHITETABLE;
drop table if exists SESSION_TOKEN;
drop table if exists SESSION_TOKEN_ARCH;
drop table if exists TOKEN;
drop table if exists OBJECT_HISTORY;
drop table if exists BOARD_ELEMENT;
drop table if exists BOARD;
drop table if exists USER_SNIPPET;
drop table if exists USER_CONFIGURATION;
drop table if exists TODO;
drop table if exists USER_GRAVEYARD;
drop table if exists ROOM_MESSAGE;
drop table if exists ROOM_MEMBER;
drop table if exists ROOM;
drop table if exists TWO_FACTOR_ENABLED;
drop table if exists TWO_FACTOR_CODES;
drop table if exists USER_DATA;


-- maintanance tables
-- table for storing program data (versions, variables etc)
CREATE TABLE PROGRAMCODES
(
  programcodes_key VARCHAR(100),
  programcodes_values VARCHAR(100)
);
-- table for saving connection data
CREATE TABLE WHO_TABLE
(
  whotable_id INT AUTO_INCREMENT PRIMARY KEY,
  whotable_mac VARCHAR(50),
  whotable_ip VARCHAR(50),
  whotable_time TIMESTAMP
)AUTO_INCREMENT = 1000000;
-- table for storing program log (errors etc)
CREATE TABLE PROGRAM_LOG
(
  program_log_id INT AUTO_INCREMENT PRIMARY KEY,
  program_log_code VARCHAR(30),
  program_log_desc VARCHAR(300),
  program_log_session_token VARCHAR(10),
  program_log_time TIMESTAMP
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
) AUTO_INCREMENT = 1000;
-- table for storing 2fa data
CREATE TABLE TWO_FACTOR_ENABLED
(
    user_id INT,
    fa_email VARCHAR(200),
    fa_confirmed INT,

    CONSTRAINT fk_twofactorenabled1 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing 2fa codes
CREATE TABLE TWO_FACTOR_CODES
(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    fa_code INT,

    CONSTRAINT fk_twofactorcodes FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing user graveyard data
CREATE TABLE USER_GRAVEYARD
(
    user_id INT,
    graveyard_date TIMESTAMP,

    CONSTRAINT fk_usergraveyard1 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for creating rooms
CREATE TABLE ROOM
(
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(50),
    room_desc VARCHAR(250),
    room_password VARCHAR(10),
    room_code VARCHAR(150)
);
-- table for storing room members
CREATE TABLE ROOM_MEMBER
(
    room_member_id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT,
    user_id INT,
    role INT, -- 1 - admin, 2 - user, 3 - moderator, 4 - spectator

    CONSTRAINT fk_roommember1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
    CONSTRAINT fk_roommember2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing room messages
CREATE TABLE ROOM_MESSAGE
(
    room_message_id INT PRIMARY KEY AUTO_INCREMENT,
    room_message_content TEXT,
    room_time TIMESTAMP,
    room_id INT,
    user_id INT,
    ping_id INT,
    content_id INT,

    CONSTRAINT fk_roommessage1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
    CONSTRAINT fk_roommessage2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing to-dos
CREATE TABLE TODO
(
    todo_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    todo_title VARCHAR(100),
    todo_desc VARCHAR(350),
    todo_impor INT,  -- 0 - normal, 1- important
    todo_colour INT, -- 1 - red, 2 - yellow, 3 - green, 4 - blue
    todo_state INT,  -- 0 - not done, 1 - in work, 2 - done

    CONSTRAINT fk_todo FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
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
-- table for storing session whitetables (for track_web development)
CREATE TABLE SESSION_WHITETABLE
(
    session_whitetable_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_token VARCHAR(70),
    session_token_time TIMESTAMP,

    CONSTRAINT fk_session_whitetable FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing archived token data
CREATE TABLE SESSION_TOKEN_ARCH
(
    session_token_archive_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_token VARCHAR(20),
    session_token_time TIMESTAMP,

    CONSTRAINT fk_session_token_arch FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- table for storing connection log
CREATE TABLE CONNECTION_LOG
(
    connection_log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_token VARCHAR(20),
    connection_time TIMESTAMP,
    connection_request TEXT,
    connection_answer TEXT,

    CONSTRAINT fk_connectionlog FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
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
-- table for storing shared as members projects
CREATE TABLE PROJECT_MEMBERS
(
    project_id INT,
    user_id INT,
    CONSTRAINT fk_projectmembers1 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id),
    CONSTRAINT fk_projectmembers2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
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
-- table for storing comments for tasks
CREATE TABLE TASK_COMMENT
(
    task_comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    task_id INT,
    task_comment_content TEXT,

    CONSTRAINT fk_task_comment FOREIGN KEY(user_id) REFERENCES USER_DATA(user_id),
    CONSTRAINT fk_task_comment2 FOREIGN KEY(task_id) REFERENCES TASK(task_id)
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
--  for storing snippets
CREATE TABLE USER_SNIPPET
(
    user_snippet_id INT PRIMARY KEY AUTO_INCREMENT,
    user_snippet_time TIMESTAMP,
    user_id INT,
    user_snippet_title VARCHAR(250),
    user_snippet_content TEXT,

    CONSTRAINT fk_usersnippet FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
-- for storing user configuration
CREATE TABLE USER_CONFIGURATION
(
    user_configuration_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    config1 VARCHAR(10),
    config2 VARCHAR(20),
    config3 VARCHAR(30),

    CONSTRAINT  fk_userconfiguration FOREIGN KEY(user_id) REFERENCES USER_DATA(user_id)
);
-- startup inserts
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_values)
VALUES
('DATABASEVERSION','103');
INSERT INTO PROGRAMCODES
(programcodes_key, programcodes_values)
VALUES
('web_apps','on');
INSERT INTO USER_DATA
(user_name,user_surname,user_email,user_login,user_password,user_category)
VALUES
('admin','admin','none','admin','21232f297a57a5a743894a0e4a801fc3','ADMIN');
-- with every user creation blank project gonna be created
INSERT INTO TOKEN
(user_id,token_value)
VALUES
(1000,'testtoken');
INSERT INTO PROGRAMCODES
(programcodes_key, programcodes_values)
VALUES
('service_tag','servicexd');
INSERT INTO PROGRAMCODES
(programcodes_key, programcodes_values)
VALUES
('2fa_system','disabled');
INSERT INTO USER_CONFIGURATION
(user_id,config1,config2,config3)
VALUES
(1000,'DARK','','');