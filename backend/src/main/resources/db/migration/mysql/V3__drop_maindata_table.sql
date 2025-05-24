ALTER TABLE maindata_copy1
    DROP FOREIGN KEY maindata_copy1_ibfk_1;

ALTER TABLE sidedata_copy1
    DROP FOREIGN KEY sidedata_copy1_ibfk_1;

DROP TABLE maindata_copy1;

DROP TABLE sidedata_copy1;