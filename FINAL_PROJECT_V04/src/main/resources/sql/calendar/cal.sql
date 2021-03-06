
DROP TABLE CALBOARD;
DROP SEQUENCE CALBOARDSEQ;

CREATE SEQUENCE CALBOARDSEQ;

CREATE TABLE CALBOARD(
	SEQ VARCHAR2(1000) PRIMARY KEY,
	ID VARCHAR2(1000) NOT NULL,
	STARTDATE VARCHAR2(1000) NOT NULL,
	ENDDATE VARCHAR2(1000) NOT NULL,
	TITLE VARCHAR2(1000) NOT NULL,
	CONTENT VARCHAR2(4000)
);

INSERT INTO CALBOARD VALUES(CALBOARDSEQ.NEXTVAL,'kh','2019-03-20','2019-03-20','test제목','test내용');
INSERT INTO CALBOARD VALUES(CALBOARDSEQ.NEXTVAL,'kh','2019-03-22','2019-03-22','test제목','test내용');

INSERT INTO CALBOARD VALUES(CALBOARDSEQ.NEXTVAL,'jh','2019-03-28','2019-03-28','test제목','test내용');
INSERT INTO CALBOARD VALUES(CALBOARDSEQ.NEXTVAL,'jh','2019-03-29','2019-03-29','test제목','test내용');
INSERT INTO CALBOARD VALUES(CALBOARDSEQ.NEXTVAL,'jh','2019-03-30','2019-03-30','test제목','test내용');

SELECT * FROM CALBOARD;

INSERT INTO CALBOARD
VALUES(CALBOARDSEQ.NEXTVAL,'id','title','content','201903041100',SYSDATE);

SELECT SEQ,ID,TITLE,CONTENT,MDATE,REGDATE
FROM CALBOARD
WHERE ID='jh' AND SUBSTR(MDATE,1,8)='20190301';

UPDATE CALBOARD
SET TITLE='제목',CONTENT='내용'
WHERE SEQ=42;

SELECT *
FROM CALBOARD
WHERE SEQ=42;


COMMIT;





















