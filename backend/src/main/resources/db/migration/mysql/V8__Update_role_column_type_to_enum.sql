ALTER TABLE user
    MODIFY  `role` VARCHAR(20) DEFAULT '1' NULL;

UPDATE user SET role = 'ADMIN' WHERE role = '0';
UPDATE user SET role = 'USER' WHERE role = '1';