-- rename old column in  DYEXTN_CONTAINER
ALTER TABLE DYEXTN_CONTAINER  RENAME COLUMN ENTITY_ID to ABSTRACT_ENTITY_ID;

--Add new column to  DYEXTN_CONTAINER
ALTER TABLE DYEXTN_CONTAINER ADD (ADD_CAPTION NUMBER(1,0));

--Update column set all value to 1
UPDATE  DYEXTN_CONTAINER SET ADD_CAPTION = 1;

-- Insert data in  new table  DYEXTN_ABSTRACT_ENTITY
INSERT ALL   INTO DYEXTN_ABSTRACT_ENTITY VALUES(ID)   SELECT identifier ID FROM DYEXTN_ENTITY;


-- Insert data in  new table DYEXTN_USERDEF_DE_VALUE_REL
INSERT ALL   INTO DYEXTN_USERDEF_DE_VALUE_REL VALUES(USER_DEF_DE_ID,PERMISSIBLE_VALUE_ID)   SELECT USER_DEF_DE_ID ,IDENTIFIER  PERMISSIBLE_VALUE_ID FROM 
DYEXTN_PERMISSIBLE_VALUE where USER_DEF_DE_ID IS NOT NULL AND IDENTIFIER  IS NOT NULL ;


-- Drop this column as  per new model
ALTER TABLE DYEXTN_PERMISSIBLE_VALUE DROP COLUMN USER_DEF_DE_ID;

-- Add column as per new model
ALTER TABLE DYEXTN_PERMISSIBLE_VALUE ADD (CATEGORY_ATTRIBUTE_ID  NUMBER(19,0));

-- Add column as per new model
ALTER TABLE DYEXTN_ENTITY  ADD (ENTITY_GROUP_ID NUMBER(19,0));

-- populate DYEXTN_ENTITY table with entity_group_id record as per table DYEXTN_ENTITY_GROUP_REL
UPDATE DYEXTN_ENTITY  SET ENTITY_GROUP_ID = (  SELECT ENTITY_GROUP_ID    FROM DYEXTN_ENTITY_GROUP_REL   WHERE DYEXTN_ENTITY_GROUP_REL.ENTITY_ID = DYEXTN_ENTITY.IDENTIFIER ) ;

-- drop unnecessary table DYEXTN_ENTITY_GROUP_REL as its not in new model.
DROP TABLE DYEXTN_ENTITY_GROUP_REL; 

-- ALTER TABLE ADD TWO COLUMN
ALTER TABLE DYEXTN_COLUMN_PROPERTIES  ADD (CATEGORY_ATTRIBUTE_ID NUMBER(19,0),CONSTRAINT_NAME VARCHAR2(255 BYTE) ); 

-- ALTER TABLE ADD THREE COLUMN
ALTER TABLE DYEXTN_CONSTRAINT_PROPERTIES   ADD  (SRC_CONSTRAINT_NAME VARCHAR2(255 BYTE),TARGET_CONSTRAINT_NAME VARCHAR2(255 BYTE),CATEGORY_ASSOCIATION_ID NUMBER(19,0)); 

-- INSERT ALL ID FROM ATTRIBUTE TABLE TO BASE_ABSTRACT_ATTRIBUTE TABLE
INSERT ALL   INTO DYEXTN_BASE_ABSTRACT_ATTRIBUTE VALUES(ID)   SELECT identifier ID FROM DYEXTN_ATTRIBUTE;


-- Rename column as per new model
ALTER TABLE DYEXTN_CONTROL  RENAME COLUMN ABSTRACT_ATTRIBUTE_ID to BASE_ABST_ATR_ID;

-- ADD COLUMN TO DYEXTN_DATA_ELEMENT
ALTER TABLE  DYEXTN_DATA_ELEMENT  ADD (CATEGORY_ATTRIBUTE_ID NUMBER(19,0)); 

-- ALTER COLUMN RENAME IT
ALTER TABLE DYEXTN_TABLE_PROPERTIES RENAME COLUMN ENTITY_ID to ABSTRACT_ENTITY_ID;

-- ADD COLUMN TO DYEXTN_TABLE_PROPERTIES
ALTER TABLE DYEXTN_TABLE_PROPERTIES  ADD (CONSTRAINT_NAME VARCHAR2(255 BYTE)); 


-- Insert data from DYEXTN_CONTAINMENT_CONTROL to DYEXTN_ABSTR_CONTAIN_CTR as per new model
INSERT ALL   INTO  DYEXTN_ABSTR_CONTAIN_CTR VALUES(IDENTIFIER,CONTAINER_ID)   SELECT IDENTIFIER ,DISPLAY_CONTAINER_ID CONTAINER_ID FROM 
DYEXTN_CONTAINMENT_CONTROL where IDENTIFIER IS NOT NULL AND DISPLAY_CONTAINER_ID  IS NOT NULL ;

-- DROP UNREQUIRED COLUMN FROM  DYEXTN_CONTAINMENT_CONTROL
ALTER TABLE DYEXTN_CONTAINMENT_CONTROL DROP  COLUMN DISPLAY_CONTAINER_ID;