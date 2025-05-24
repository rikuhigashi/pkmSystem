ALTER TABLE sidedata
    ADD expired_at datetime NULL;

ALTER TABLE sidedata
    ADD status VARCHAR(20) DEFAULT 'PENDING' NULL;