PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE PROGRAMCODES
(
  programcodes_key VARCHAR(100),
  programcodes_values VARCHAR(100)
);
INSERT INTO PROGRAMCODES VALUES('DATABASEVERSION','103');
INSERT INTO PROGRAMCODES VALUES('web_apps','on');
INSERT INTO PROGRAMCODES VALUES('service_tag','servicexd');
INSERT INTO PROGRAMCODES VALUES('2fa_system','enable');
CREATE TABLE WHO_TABLE
(
  whotable_id INTEGER PRIMARY KEY AUTOINCREMENT,
  whotable_mac VARCHAR(50),
  whotable_ip VARCHAR(50),
  whotable_time TIME
);
CREATE TABLE PROGRAM_LOG
(
  program_log_id INTEGER PRIMARY KEY AUTOINCREMENT,
  program_log_code VARCHAR(30),
  program_log_desc VARCHAR(300),
  program_log_session_token VARCHAR(10),
  program_log_time TIMESTAMP
);
CREATE TABLE USER_DATA
(
  user_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_name VARCHAR(150),
  user_surname VARCHAR(200),
  user_email VARCHAR(200),
  user_login VARCHAR(25),
  user_password VARCHAR(50),
  user_category VARCHAR(100)
);
INSERT INTO USER_DATA VALUES(1,'admin','admin','none','admin','21232f297a57a5a743894a0e4a801fc3','ADMIN');
CREATE TABLE TWO_FACTOR_ENABLED
(
    user_id INT,
    fa_email VARCHAR(200),
    fa_confirmed INT,

    CONSTRAINT fk_twofactorenabled1 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO TWO_FACTOR_ENABLED VALUES(1000,'kubawawak@gmail.com',0);
CREATE TABLE TWO_FACTOR_CODES
(
    user_id INT,
    fa_code INT,

    CONSTRAINT fk_twofactorcodes FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE USER_GRAVEYARD
(
    user_id INT,
    graveyard_date TIMESTAMP,

    CONSTRAINT fk_usergraveyard1 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE ROOM
(
    room_id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_name VARCHAR(50),
    room_desc VARCHAR(250),
    room_password VARCHAR(10),
    room_code VARCHAR(150)
);
CREATE TABLE ROOM_MEMBER
(
    room_member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INT,
    user_id INT,
    role INT,

    CONSTRAINT fk_roommember1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
    CONSTRAINT fk_roommember2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE ROOM_MESSAGE
(
    room_message_id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_message_content TEXT,
    room_time TIMESTAMP,
    room_id INT,
    user_id INT,
    ping_id INT,
    content_id INT,

    CONSTRAINT fk_roommessage1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
    CONSTRAINT fk_roommessage2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE TODO
(
    todo_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    todo_title VARCHAR(100),
    todo_desc VARCHAR(350),
    todo_impor INT,  
    todo_colour INT, 
    todo_state INT,  

    CONSTRAINT fk_todo FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO TODO VALUES(1,1,'Szybki temat','XD',1,1,0);
CREATE TABLE OBJECT_HISTORY
(
history_id INTEGER PRIMARY KEY AUTOINCREMENT,
user_id INT,
history_category VARCHAR(50),
history_object_id INT,
history_desc VARCHAR(250),
history_object_time TIMESTAMP,

constraint  fk_objecthistory FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE TOKEN
(
  token_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  token_value VARCHAR(100),
  
  CONSTRAINT fk_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO TOKEN VALUES(1,1,'testtoken');
CREATE TABLE SESSION_TOKEN
(
  session_token_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  session_token VARCHAR(20),
  session_token_time TEXT,
  
  CONSTRAINT fk_session_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO SESSION_TOKEN VALUES(27,1,'aqvjwbnevj','2022-04-17T20:59:38.845623900');
CREATE TABLE SESSION_WHITETABLE
(
    session_whitetable_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    session_token VARCHAR(70),
    session_token_time TIMESTAMP,

    CONSTRAINT fk_session_whitetable FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE SESSION_TOKEN_ARCH
(
    session_token_archive_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    session_token VARCHAR(20),
    session_token_time TIMESTAMP,

    CONSTRAINT fk_session_token_arch FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO SESSION_TOKEN_ARCH VALUES(1,1,'uwdyizihmh','2022-04-15T22:24:21.227747');
INSERT INTO SESSION_TOKEN_ARCH VALUES(2,1,'elhgrhowzz','2022-04-15T22:31:27.688924');
INSERT INTO SESSION_TOKEN_ARCH VALUES(3,1,'hbiwzqqbsc','2022-04-15T22:35:46.614312');
INSERT INTO SESSION_TOKEN_ARCH VALUES(4,1,'eshzwaaezu','2022-04-15T22:38:15.887481');
INSERT INTO SESSION_TOKEN_ARCH VALUES(5,1,'nqedpoaxxn','2022-04-15T22:40:42.125761');
INSERT INTO SESSION_TOKEN_ARCH VALUES(6,1,'cxbeqyobex','2022-04-15T22:42:05.839783');
INSERT INTO SESSION_TOKEN_ARCH VALUES(7,1,'qruqudqbvd','2022-04-15T22:42:41.464497');
INSERT INTO SESSION_TOKEN_ARCH VALUES(8,1,'teqhootqii','2022-04-15T22:44:23.412051');
INSERT INTO SESSION_TOKEN_ARCH VALUES(9,1,'qyudgjcyir','2022-04-15T22:46:09.946434');
INSERT INTO SESSION_TOKEN_ARCH VALUES(10,1,'clpclcobxh','2022-04-15T22:47:05.142827');
INSERT INTO SESSION_TOKEN_ARCH VALUES(11,1,'lkqbjtbqss','2022-04-15T22:47:55.539252');
INSERT INTO SESSION_TOKEN_ARCH VALUES(12,1,'xilsuwnrhb','2022-04-15T22:49:17.329231');
INSERT INTO SESSION_TOKEN_ARCH VALUES(13,1,'rxtjcmziva','2022-04-16T10:31:59.452518');
INSERT INTO SESSION_TOKEN_ARCH VALUES(14,1,'ingeiqsyhf','2022-04-16T10:34:06.068086');
INSERT INTO SESSION_TOKEN_ARCH VALUES(15,1,'mglrvicnfu','2022-04-16T11:19:58.384115200');
INSERT INTO SESSION_TOKEN_ARCH VALUES(16,1,'ielgpfwton','2022-04-17T17:09:40.990683900');
INSERT INTO SESSION_TOKEN_ARCH VALUES(17,1,'nnitiqvayd','2022-04-17T17:14:41.577656800');
INSERT INTO SESSION_TOKEN_ARCH VALUES(18,1,'miueobsyri','2022-04-17T17:22:17.001784600');
INSERT INTO SESSION_TOKEN_ARCH VALUES(19,1,'rvvjvafbaj','2022-04-17T17:48:56.858818400');
INSERT INTO SESSION_TOKEN_ARCH VALUES(20,1,'mbomdlnmen','2022-04-17T17:50:22.021889300');
INSERT INTO SESSION_TOKEN_ARCH VALUES(21,1,'bdexrsubrt','2022-04-17T17:52:53.681104900');
INSERT INTO SESSION_TOKEN_ARCH VALUES(22,1,'elbyqijvsj','2022-04-17T17:54:31.814350700');
INSERT INTO SESSION_TOKEN_ARCH VALUES(23,1,'ymlyahmejn','2022-04-17T17:57:52.839755200');
INSERT INTO SESSION_TOKEN_ARCH VALUES(24,1,'ccntxgddet','2022-04-17T20:17:06.791880400');
INSERT INTO SESSION_TOKEN_ARCH VALUES(25,1,'jezrabmqcb','2022-04-17T20:24:42.421413500');
INSERT INTO SESSION_TOKEN_ARCH VALUES(26,1,'orvvhqrtsc','2022-04-17T20:35:11.778879');
INSERT INTO SESSION_TOKEN_ARCH VALUES(27,1,'aqvjwbnevj','2022-04-17T20:44:38.876867400');
CREATE TABLE CONNECTION_LOG
(
    connection_log_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    session_token VARCHAR(20),
    connection_time TIMESTAMP,
    connection_request TEXT,
    connection_answer TEXT,

    CONSTRAINT fk_connectionlog FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO CONNECTION_LOG VALUES(1,1,'elbyqijvsj','2022-04-17T17:54:53.275208400','Trying to set project (Test)','Failed - project add error');
INSERT INTO CONNECTION_LOG VALUES(2,1,'ymlyahmejn','2022-04-17T17:58:04.498626700','Trying to set project (Test)','Failed - project add error');
INSERT INTO CONNECTION_LOG VALUES(3,1,'ccntxgddet','2022-04-17T20:17:19.803439700','Trying to set project (Test)','Failed - project add error');
INSERT INTO CONNECTION_LOG VALUES(4,1,'orvvhqrtsc','2022-04-17T20:35:24.048685100','Trying to set project (Test)','Failed - project add error');
CREATE TABLE PRIVILAGES
(
  privilages_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  privilages_project_list VARCHAR(100),

  CONSTRAINT fk_privilages FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE LOG_HISTORY
(
  log_history_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  log_history_code VARCHAR(100),
  log_history_desc VARCHAR(300),

  CONSTRAINT fk_log_history FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE PROJECT_MEMBERS
(
    project_id INT,
    user_id INT,
    CONSTRAINT fk_projectmembers1 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id),
    CONSTRAINT fk_projectmembers2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE SHARED_ELEMENTS
(
    user_id INT,
    project_id INT,

    constraint fk_sharedelements FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
    constraint fk_sharedelements2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
CREATE TABLE TASK
(
  task_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  project_id INT,
  task_name VARCHAR(200),
  task_desc TEXT,
  task_priority INT, 
  task_state VARCHAR(100), 

  CONSTRAINT fk_task FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
  CONSTRAINT fk_task2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
CREATE TABLE TASK_COMMENT
(
    task_comment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    task_id INT,
    task_comment_content TEXT,

    CONSTRAINT fk_task_comment FOREIGN KEY(user_id) REFERENCES USER_DATA(user_id),
    CONSTRAINT fk_task_comment2 FOREIGN KEY(task_id) REFERENCES TASK(task_id)
);
CREATE TABLE ISSUE
(
  issue_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INT,
  project_id INT,
  issue_name VARCHAR(200),
  issue_desc TEXT,
  issue_priority INT, 
  issue_group INT, 
  issue_state VARCHAR(100), 
  issue_time_creation TIMESTAMP,
  issue_time_due TIMESTAMP,

  CONSTRAINT fk_issue FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
  CONSTRAINT fk_issue2 FOREIGN KEY (project_id) REFERENCES PROJECT(project_id)
);
CREATE TABLE BOARD
(
    board_id INTEGER PRIMARY KEY AUTOINCREMENT,
    board_name VARCHAR(100),
    user_id INT,
    board_desc VARCHAR(200),
    board_time TIMESTAMP,

    CONSTRAINT fk_board FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE BOARD_ELEMENT
(
    board_element_id INTEGER PRIMARY KEY AUTOINCREMENT,
    board_list_object VARCHAR(10),
    object_id INT,
    board_id INT,

    CONSTRAINT  fk_boardelement FOREIGN KEY (board_id) REFERENCES BOARD(board_id)
);
CREATE TABLE USER_SNIPPET
(
    user_snippet_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_snippet_time TIMESTAMP,
    user_id INT,
    user_snippet_title VARCHAR(250),
    user_snippet_content TEXT,

    CONSTRAINT fk_usersnippet FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
);
CREATE TABLE USER_CONFIGURATION
(
    user_configuration_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT,
    config1 VARCHAR(10),
    config2 VARCHAR(20),
    config3 VARCHAR(30),

    CONSTRAINT  fk_userconfiguration FOREIGN KEY(user_id) REFERENCES USER_DATA(user_id)
);
INSERT INTO USER_CONFIGURATION VALUES(1,1,'DARK','','');
CREATE TABLE IF NOT EXISTS "PROJECT" (
	"project_id"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"user_id"	INT,
	"project_name"	TEXT,
	"project_desc"	TEXT,
	"project_creation_date"	TEXT,
	"project_state"	VARCHAR(100),
	CONSTRAINT "fk_project" FOREIGN KEY("user_id") REFERENCES "USER_DATA"("user_id")
);
DELETE FROM sqlite_sequence;
INSERT INTO sqlite_sequence VALUES('USER_DATA',1);
INSERT INTO sqlite_sequence VALUES('TOKEN',1);
INSERT INTO sqlite_sequence VALUES('USER_CONFIGURATION',1);
INSERT INTO sqlite_sequence VALUES('PROGRAM_LOG',1535);
INSERT INTO sqlite_sequence VALUES('SESSION_TOKEN',27);
INSERT INTO sqlite_sequence VALUES('SESSION_TOKEN_ARCH',27);
INSERT INTO sqlite_sequence VALUES('CONNECTION_LOG',4);
INSERT INTO sqlite_sequence VALUES('TODO',1);
INSERT INTO sqlite_sequence VALUES('PROJECT',0);
COMMIT;
