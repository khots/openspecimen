drop TABLE CATISSUE_TABLE_RELATION;
CREATE TABLE CATISSUE_TABLE_RELATION
(
      PARENT_TABLE_ID bigint,      
      
      CHILD_TABLE_ID bigint
);

drop table CATISSUE_QUERY_INTERFACE_TABLE_DATA;
CREATE TABLE CATISSUE_QUERY_INTERFACE_TABLE_DATA
(
      TABLE_ID bigint not null auto_increment, 
	  
      TABLE_NAME varchar(50),

      DISPLAY_NAME varchar(50),
	
      ALIAS_NAME varchar(50),
      
      primary key (TABLE_ID)
);


drop table CATISSUE_QUERY_INTERFACE_COLUMN_DATA;
CREATE TABLE CATISSUE_QUERY_INTERFACE_COLUMN_DATA
(
	  IDENTIFIER bigint not null auto_increment,

      TABLE_ID bigint not null,

      COLUMN_NAME varchar(50),

      DISPLAY_NAME varchar(50),
      
      ATTRIBUTE_TYPE varchar(30),
      
	  primary key (IDENTIFIER)
);

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (1,'CATISSUE_STORAGE_TYPE','Storage Type','StorageType');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'IDENTIFIER' , 'Identifier' , 'bigint' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'TYPE' , 'Type' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'DEFAULT_TEMPERATURE_IN_CENTIGRADE' , 'Default Temperature In Centigrade' , 'double');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'DEFAULT_TEMPERATURE_IN_CENTIGRADE' , 'Default Temperature In Centigrade' , 'double');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'ONE_DIMENSION_LABEL' , 'One Dimension Label' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'TWO_DIMENSION_LABEL' , 'Two Dimension Label' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 1, 'STORAGE_CONTAINER_CAPACITY_ID' , 'Storage Container Capacity Id' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID,TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (2, 'CATISSUE_STORAGE_CONTAINER_CAPACITY','Storage Container Capacity','StorageContainerCapacity');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 2, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 2, 'ONE_DIMENSION_CAPACITY' , 'One Dimension Capacity' , 'integer');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 2, 'TWO_DIMENSION_CAPACITY' , 'Two Dimension Capacity' , 'integer');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID,TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (3,'CATISSUE SITE','Site','Site');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'NAME' , 'Name' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'TYPE' , 'Type' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'EMAIL_ADDRESS' , 'Email Address' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'USER_ID' , 'User Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'ACTIVITY_STATUS' , 'Activity Status' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 3, 'ADDRESS_ID' , 'Address Id' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID,TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (4,'CATISSUE_ADDRESS','Address','Address');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'STREET' , 'Street' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'CITY' , 'City' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'STATE' , 'State' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'COUNTRY' , 'Country' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'ZIPCODE' , 'Zipcode' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'PHONE_NUMBER' , 'Phone Number' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 4, 'FAX_NUMBER' , 'Fax Number' , 'varchar' );

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID,TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (5,'CATISSUE_DEPARTMENT','Department','Department');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 5, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 5, 'NAME' , 'Name' , 'varchar');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID,TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (6, 'CATISSUE_INSTITUTION','Institution','Institution');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 6, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 6, 'NAME' , 'Name' , 'varchar');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (7, 'CATISSUE_CANCER_RESEARCH GROUP','Cancer Research Group','CancerResearchGroup');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 7, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 7, 'NAME' , 'Name' , 'varchar');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (8,'CATISSUE_BIOHAZARD','Biohazard','BioHazard');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 8, 'IDENTIFIER' , 'Identifier' , 'bigint' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 8, 'NAME' , 'Name' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 8, 'COMMENTS' , 'Comments' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 8, 'TYPE' , 'Type' , 'varchar' );

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (9,'CATISSUE_SPECIMEN_PROTOCOL','Specimen Protocol','SpecimenProtocol');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'PRINCIPAL_INVESTIGATOR_ID' , 'Principal Investigator Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'TITLE' , 'Title' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'SHORT_TITLE' , 'Short Title' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'IRB_IDENTIFIER' , 'Irb Identifier' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'START_DATE' , 'Start Date' , 'date');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'END_DATE' , 'End Date' , 'date');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'ENROLLMENT' , 'Enrollment' , 'integer');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'DESCRIPTION_URL' , 'Description Url' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 9, 'ACTIVITY_STATUS' , 'Activity Status' , 'varchar');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (10,'CATISSUE_COLLECTION_PROTOCOL','Collection Protocol','CollectionProtocol');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 10, 'IDENTIFIER' , 'Identifier' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (11,'CATISSUE COLLECTION PROTOCOL EVENT','Collection Protocol Event','CollectionProtocolEvent');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 11, 'CLINICAL_STATUS' , 'Clinical Status' , 'varchar' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 11, 'STUDY_CALENDAR_EVENT_POINT' , 'Study Calendar Event Point' , 'double' );
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 11, 'COLLECTION_PROTOCOL_ID' , 'Collection Protocol Id' , 'bigint' );

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (12,'CATISSUE_SPECIMEN_REQUIREMENT','Specimen Requirement','SpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 12, 'SPECIMEN_TYPE' , 'Specimen Type' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 12, 'TISSUE_SITE' , 'Tissue Site' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 12, 'PATHOLOGY_STATUS' , 'Pathology Status' , 'varchar');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (13,'CATISSUE_CELL_SPECIMEN_REQUIREMENT','Cell Specimen Requirement','CellSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 13, 'QUANTITY_IN_CELL_COUNT' , 'Quantity In Cell Count' , 'integer');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (14,'CATISSUE_MOLECULAR_SPECIMEN_REQUIREMENT','Molecular Specimen Requirement','MolecularSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 14, 'QUANTITY_IN_MICRO_GRAM' , 'Quantity In Micro Gram' , 'double');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (15,'CATISSUE_TISSUE_SPECIMEN_REQUIREMENT','Tissue Specimen Requirement','TissueSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 15, 'QUANTITY_IN_GRAM' , 'Quantity In Gram' , 'double');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (16,'CATISSUE_FLUID_SPECIMEN_REQUIREMENT','Fluid Specimen Requirement','FluidSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 16, 'QUANTITY_IN_MILILITER' , 'Quantity In Mililiter' , 'double');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (17,'CATISSUE_COLLECTION_COORDINATORS','Collection Coordinators','CollectionCoordinators');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 17, 'COLLECTION_PROTOCOL_ID' , 'Collection Protocol Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 17, 'USER_ID' , 'User Id' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (18,'CATISSUE_COLLECTION_SPECIMEN_REQUIREMENT','Collection Specimen Requirement','CollectionSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 18, 'COLLECTION_PROTOCOL_EVENT_ID' , 'Collection Protocol Event Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 18, 'SPECIMEN_REQUIREMENT_ID' , 'Specimen Requirement Id' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (19,'CATISSUE_DISTRIBUTION_PROTOCOL','Distribution Protocol','DistributionProtocol');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 19, 'IDENTIFIER' , 'Identifier' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (20,'CATISSUE_DISTRIBUTION_SPECIMEN_REQUIREMENT','Distribution Specimen Requirement','DistributionSpecimenRequirement');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 20, 'DISTRIBUTION_PROTOCOL_ID' , 'Distribution Protocol Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 20, 'SPECIMEN_REQUIREMENT_ID' , 'Specimen Requirement Id' , 'bigint');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (21,'CATISSUE_STORAGE_CONTAINER','Storage Container','StorageContainer');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'CONTAINER_NUMBER' , 'Container Number' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'TEMPERATURE' , 'Temperature' , 'double');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'IS_CONTAINER_FULL' , 'Is Container Full' , 'bit');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'BARCODE' , 'Barcode' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'ACTIVITY_STATUS' , 'Activity Status' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'STORAGE_TYPE_ID' , 'Storage Type Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'SITE_ID' , 'Site Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'PARENT_CONTAINER_ID' , 'Parent Container Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'STORAGE_CONTAINER_CAPACITY_ID' , 'Storage Container Capacity Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'POSITION_DIMENSION_ONE' , 'Position Dimension One' , 'integer');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 21, 'POSITION_DIMENSION_TWO' , 'Position Dimension Two' , 'integer');

insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (22,'CATISSUE_STORAGE_CONTAINER_DETAILS','Storage Container Details','StorageContainerDetails');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 22, 'PARAMETER_NAME' , 'Parameter Name' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 22, 'PARAMETER_VALUE' , 'Parameter Value' , 'varchar');


insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (23,'CATISSUE_USER','User','User');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'IDENTIFIER' , 'Identifier' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'ACTIVITY_STATUS' , 'Activity Status' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'DEPARTMENT_ID' , 'Department Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'CANCER_RESEARCH_GROUP_ID' , 'Cancer Research Group Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'INSTITUTION_ID' , 'Institution Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'ADDRESS_ID' , 'Address Id' , 'bigint');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 23, 'STATUS_COMMENT' , 'Status Comment' , 'varchar');


insert into CATISSUE_QUERY_INTERFACE_TABLE_DATA  ( TABLE_ID, TABLE_NAME,DISPLAY_NAME,ALIAS_NAME) values (24,'CSM_USER','CSM User','CsmUser');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 24, 'LOGIN_NAME' , 'Login Name' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 24, 'FIRST_NAME' , 'First Name' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 24, 'LAST_NAME' , 'Last Name' , 'varchar');
insert into CATISSUE_QUERY_INTERFACE_COLUMN_DATA ( TABLE_ID, COLUMN_NAME , DISPLAY_NAME , ATTRIBUTE_TYPE ) values ( 24, 'EMAIL_ID' , 'Email Id' , 'varchar');




insert into CATISSUE_TABLE_RELATION values ( 1 , 1 );
insert into CATISSUE_TABLE_RELATION values ( 1 , 2 );
insert into CATISSUE_TABLE_RELATION values ( 6 , 6 );
insert into CATISSUE_TABLE_RELATION values ( 5 , 5 );
insert into CATISSUE_TABLE_RELATION values ( 7 , 7 );
insert into CATISSUE_TABLE_RELATION values ( 8 , 8 );
insert into CATISSUE_TABLE_RELATION values ( 3 , 3 );
insert into CATISSUE_TABLE_RELATION values ( 3 , 4 );
insert into CATISSUE_TABLE_RELATION values (21 , 21);
insert into CATISSUE_TABLE_RELATION values (21 , 2 );
insert into CATISSUE_TABLE_RELATION values (21 , 1 );
insert into CATISSUE_TABLE_RELATION values (21 , 3 );
insert into CATISSUE_TABLE_RELATION values (10 , 10);
insert into CATISSUE_TABLE_RELATION values (10 , 9 );
insert into CATISSUE_TABLE_RELATION values (10 , 11);
insert into CATISSUE_TABLE_RELATION values (10 , 12);
insert into CATISSUE_TABLE_RELATION values (10 , 18);
insert into CATISSUE_TABLE_RELATION values (19 , 19);
insert into CATISSUE_TABLE_RELATION values (19 , 9 );
insert into CATISSUE_TABLE_RELATION values (19 , 20);
insert into CATISSUE_TABLE_RELATION values (19 , 12);




commit;

insert into CATISSUE_INSTITUTION VALUES (1,'Washington University');

insert into CATISSUE_DEPARTMENT VALUES (1,'Cardiology');
insert into CATISSUE_DEPARTMENT VALUES (2,'Pathology');

insert into CATISSUE_CANCER_RESEARCH_GROUP VALUES (1,'Siteman Cancer Center');
insert into CATISSUE_CANCER_RESEARCH_GROUP VALUES (2,'Washington University');

insert into CATISSUE_ADDRESS values (1,'abc','abc','asd','abc','abc','asdas','asdas');
insert into CATISSUE_SITE VALUES (1,'SITE1',"LAB","as@b.cn",47,'Active',1);
insert into CATISSUE_STORAGE_container_capacity VALUES (1,5,5,'abc','abc');
insert into CATISSUE_STORAGE_TYPE VALUES (1,'Box',50,1);
insert into CATISSUE_STORAGE_container VALUES (1,'name1',50,1,'abc','Active',1,1,null,1,0,0);
insert into CATISSUE_STORAGE_CONTAINER values(2,'name2',50,false,'acb','Active',1,null,1,1,0,1)

