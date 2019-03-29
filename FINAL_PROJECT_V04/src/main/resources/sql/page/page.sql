CREATE TABLE PAGES (
    
    PAGE_NO  INT
    ,PAGE_NAME VARCHAR2(20) NOT NULL
    ,PAGE_CONTENT CLOB DEFAULT EMPTY_CLOB()
    ,CONSTRAINT PAGES_PK PRIMARY KEY(PAGE_NO)
);

CREATE TABLE PAGE_CREATE (
    
    PAGE_CREATE_NO INT
    ,USER_NO INT
    ,PAGE_NO INT
    ,CONSTRAINT PAGE_CREATE_PK PRIMARY KEY(PAGE_CREATE_NO)
    ,CONSTRAINT USER_NO_FK FOREIGN KEY(USER_NO) REFERENCES USERS(USER_NO),
    CONSTRAINT PAGE_NO_FK FOREIGN KEY(PAGE_NO) REFERENCES PAGES(PAGE_NO));
)

DROP SEQUENCE PAGE_NO_SEQ;
CREATE SEQUENCE PAGE_NO_SEQ;

DROP SEQUENCE PAGE_CREATE_NO_SEQ;
CREATE SEQUENCE PAGE_CREATE_NO_SEQ;