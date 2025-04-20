-- 1. Kích hoạt việc ghi nhật ký hệ thống
-- Chạy những lệnh này trong CDB với quyền SYSDBA
-- ALTER SYSTEM SET audit_trail=DB, EXTENDED SCOPE=SPFILE;
-- SHUTDOWN IMMEDIATE;
-- STARTUP;

-- 2. Thực hiện ghi nhật ký hệ thống dùng Standard audit: theo dõi hành vi của những user
-- cụ thể trên những đối tượng cụ thể của cơ sở dữ liệu gồm table, view, stored procedure,
-- function, có thiết lập theo dõi các hành vi hiện thành công hay không thành công. Sinh
-- viên tự đề nghị ngữ cảnh để kiểm tra việc thiết lập ghi nhật ký.

-- Ghi nhật ký các hoạt động đăng nhập và đăng xuất của tất cả người dùng
AUDIT SESSION;
-- Ghi nhật ký các thao tác SELECT, INSERT, UPDATE, DELETE trên các bảng quan trọng
-- có thông tin nhạy cảm như điểm hay lương
AUDIT SELECT, INSERT, UPDATE, DELETE ON QLDAIHOC.DANGKY BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON QLDAIHOC.NHANVIEN BY ACCESS;

-- 3. Sinh viên có thể dùng Fine-grained Audit hoặc Unified Audit để thực hiện ghi nhật ký
-- các tình huống sau và tạo tình huống để ghi nhật ký được (có dữ liệu ghi nhật ký) các
-- hành vi sau:

ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- 3.a. Hành vi cập nhật quan hệ ĐANGKY tại các trường liên quan đến điểm số 
-- nhưng người đó không thuộc vai trò "NV PKT".
CREATE OR REPLACE FUNCTION UDF_FGA_DANGKY_DIEM_UPD
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_cnt NUMBER;
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Kiểm tra xem người dùng có phải là NV PKT hay không
    SELECT COUNT(*) INTO v_cnt
    FROM QLDAIHOC.NHANVIEN
    WHERE MANV = v_user AND VAITRO = 'NV_PKT';

    IF v_cnt = 1 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
/

BEGIN
    -- Nếu đã có policy thì xóa đi
    DBMS_FGA.DROP_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_DIEM_UPD'
    );
EXCEPTION WHEN OTHERS THEN 
    NULL; 
END;
/
BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_DIEM_UPD',
        audit_column => 'DIEMTH, DIEMQT, DIEMTHI, DIEMTK',
        audit_condition => 'UDF_FGA_DANGKY_DIEM_UPD() = 0',
        statement_types => 'UPDATE'
    );
END;
/

-- 3.b. Hành vi của người dùng (không thuộc vai trò "NV TCHC") có thể đọc trên
-- trường LUONG, PHUCAP của người khác hoặc cập nhật ở quan hệ NHANVIEN.
CREATE OR REPLACE FUNCTION UDF_FGA_NHANVIEN_LUONG_SEL
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_cnt NUMBER;
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Kiểm tra xem người dùng có phải là NV TCHC hay không
    SELECT COUNT(*) INTO v_cnt
    FROM QLDAIHOC.NHANVIEN
    WHERE MANV = v_user AND VAITRO = 'NV_TCHC';

    IF v_cnt = 1 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
/
BEGIN
    -- Nếu đã có policy thì xóa đi
    DBMS_FGA.DROP_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'NHANVIEN',
        policy_name => 'FGA_NHANVIEN_LUONG_SEL'
    );
EXCEPTION WHEN OTHERS THEN 
    NULL; 
END;
/
BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'NHANVIEN',
        policy_name => 'FGA_NHANVIEN_LUONG_SEL',
        audit_column => 'LUONG,PHUCAP',
        audit_condition => 'UDF_FGA_NHANVIEN_LUONG_SEL() = 0',
        statement_types => 'SELECT'
    );
END;
/

-- 3.c. Hành vi thêm, xóa, sửa trên quan hệ DANGKY của sinh viên nhưng trên dòng
-- dữ liệu của sinh viên khác hoặc thực hiện hiệu chỉnh đăng ký học phần ngoài
-- thời gian cho phép hiệu chỉnh đăng ký học phần.

-- Hành vi sinh viên tác động lên dữ liệu đăng ký của sinh viên khác
BEGIN
    -- Nếu đã có policy thì xóa đi
    DBMS_FGA.DROP_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_HSKHAC'
    );
EXCEPTION WHEN OTHERS THEN 
    NULL; 
END;
/
BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_HSKHAC',
        -- Điều kiện: người dùng là sinh viên (SV%) nhưng tác động lên dữ liệu SV khác
        audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') != MASV',
        statement_types => 'INSERT, UPDATE, DELETE'
    );
END;
/
-- Hành vi hiệu chỉnh đăng ký ngoài thời gian quy định
CREATE OR REPLACE FUNCTION QLDAIHOC.UDF_FGA_DANGKY_TRE (
    p_MAMM IN NUMBER
)
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_bd_hocky DATE;
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Kiểm tra xem người dùng có phải là sinh viên hay không
    IF v_user NOT LIKE 'SV%' THEN
        RETURN 1;
    END IF;

    -- Lấy ngày bắt đầu học kỳ của mở môn
    SELECT CASE
        WHEN mm.HK = 1 THEN TO_DATE(TO_CHAR(mm.NAM) || '-09-01', 'YYYY-MM-DD') + 14
        WHEN mm.HK = 2 THEN TO_DATE(TO_CHAR(mm.NAM) || '-01-01', 'YYYY-MM-DD') + 14
        WHEN mm.HK = 3 THEN TO_DATE(TO_CHAR(mm.NAM) || '-05-01', 'YYYY-MM-DD') + 14
        END INTO v_bd_hocky
    FROM QLDAIHOC.MOMON mm
    WHERE mm.MAMM = p_MAMM;
    
    IF SYSDATE < v_bd_hocky THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
/
BEGIN
    -- Nếu đã có policy thì xóa đi
    DBMS_FGA.DROP_POLICY(
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY',
        policy_name => 'AUDIT_DANGKY_TRE'
    );
EXCEPTION WHEN OTHERS THEN 
    NULL; 
END;
/
BEGIN 
    DBMS_FGA.add_policy (
        object_schema => 'QLDAIHOC',
        object_name => 'DANGKY', 
        policy_name => 'AUDIT_DANGKY_TRE', 
        audit_condition => 'QLDAIHOC.UDF_FGA_DANGKY_TRE(MAMM) = 0',
        statement_types => 'INSERT, UPDATE, DELETE'
);
END; 
/

-- 4. Đọc xuất dữ liệu nhật ký hệ thống.
-- Standard Audit
SELECT * FROM SYS.AUD$;
SELECT * FROM DBA_AUDIT_TRAIL;

-- Fine-Grained Audit
SELECT * FROM DBA_FGA_AUDIT_TRAIL;

-- ============================================================
-- Tạo các tình huống vi phạm để ghi nhật ký
-- ============================================================
-- Xóa dữ liệu trong DBA_FGA_AUDIT_TRAIL để dễ theo dõi
DELETE FROM DBA_FGA_AUDIT_TRAIL;

-- 3.a. Cập nhật điểm bởi người không phải NV PKT
CONNECT QLDAIHOC/Nhom01
UPDATE QLDAIHOC.DANGKY SET DIEMTH = 10 WHERE MASV = 'SV00000001' AND MAMM = 1;

-- 3.b. Xem thông tin lương bởi người không phải NV TCHC
CONNECT QLDAIHOC/Nhom01
SELECT * FROM QLDAIHOC.NHANVIEN;

-- 3.c. Sinh viên thử cập nhật đăng ký của sinh viên khác
CONNECT SV00000001/SV00000001
UPDATE QLDAIHOC.DANGKY SET DIEMTH = 10 WHERE MASV = 'SV00000002' AND MAMM = 2;

-- 3.c Hiệu chỉnh đăng ký ngoài thời gian quy định
CONNECT QLDAIHOC/Nhom01
UPDATE QLDAIHOC.DANGKY SET DIEMTH = 10 WHERE MASV = 'SV00000001' AND MAMM = 1;

-- Kiểm tra kết quả ghi nhật ký
SELECT DB_USER, SQL_TEXT, POLICY_NAME FROM DBA_FGA_AUDIT_TRAIL WHERE OBJECT_SCHEMA = 'QLDAIHOC';