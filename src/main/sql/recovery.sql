-- Phần này chạy trên sqlplus chứ đừng chạy nguyên file này

-- Kết nối với RMAN (Recovery Manager)
rman TARGET /

-- Hiển thị tất cả cài đặt có thể cấu hình
SHOW ALL;

-- Tự động sao lưu controlfile
CONFIGURE CONTROLFILE AUTOBACKUP ON;

-- Backup toàn bộ database
-- Tắt database và mount database
SHUTDOWN IMMEDIATE;
STARTUP FORCE DBA;
SHUTDOWN IMMEDIATE;
STARTUP MOUNT;
-- Backup database
BACKUP AS COPY DATABASE PLUS ARCHIVELOG;

-- Incremental backup
-- Level 0: Sao lưu toàn bộ database
BACKUP INCREMENTAL LEVEL 0 DATABASE;
-- Level 1 cumulative: Sao lưu các block đã thay đổi kể từ lần sao lưu level 0 gần nhất
BACKUP INCREMENTAL LEVEL 1 CUMULATIVE DATABASE;
-- Level 1 differential: Sao lưu các block đã thay đổi kể từ lần sao lưu level 1 gần nhất
BACKUP INCREMENTAL LEVEL 1 DATABASE;


-- Mở lại database
ALTER DATABASE OPEN;
-- Kiểm tra trạng thái backup
LIST BACKUP;
-- Kiểm tra trạng thái archive log
LIST ARCHIVELOG ALL;
-- Kiểm tra trạng thái backup
LIST BACKUP SUMMARY;
-- Kiểm tra trạng thái backup controlfile
LIST BACKUP OF CONTROLFILE;


-- Khôi phục database từ backup
-- Tắt database và mount database
SHUTDOWN IMMEDIATE;
STARTUP FORCE DBA;
SHUTDOWN IMMEDIATE;
STARTUP MOUNT;

-- Khôi phục toàn bộ database từ backup
RESTORE DATABASE;
RECOVER DATABASE;
ALTER DATABASE OPEN;

-- Xóa sao lưu
DELETE BACKUPSET COMPLETED AFTER 'SYSDATE - (1/24)';
DELETE COPY COMPLETED AFTER 'SYSDATE - (1/24)';
-- Tắt autobackup
CONFIGURE CONTROLFILE AUTOBACKUP OFF;
