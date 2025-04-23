--ALTER SESSION SET "_ORACLE_SCRIPT"=true;
ALTER SESSION SET CURRENT_SCHEMA = "QLDAIHOC";

-- Xóa các cấu trúc dữ liệu nếu tồn tại
BEGIN 
    -- Xóa các role
    FOR rle IN (SELECT * FROM DBA_ROLES WHERE ROLE LIKE 'RL_%') LOOP
        EXECUTE IMMEDIATE 'DROP ROLE ' || rle.ROLE;
    END LOOP;
END;
/

-- Tạo các role
CREATE ROLE RL_NVCB;
CREATE ROLE RL_GV;
CREATE ROLE RL_NV_PDT;
CREATE ROLE RL_NV_PKT;
CREATE ROLE RL_NV_TCHC;
CREATE ROLE RL_NV_CTSV;
CREATE ROLE RL_TRGDV;
CREATE ROLE RL_SV;

-- Câu 1: Em hãy ép thỏa các chính sách bảo mật trên quan hệ NHANVIEN dùng RBAC
-- theo mô tả bên dưới
----- Người dùng có VAITRO là "NVCB" có quyền xem dòng dữ liệu của chính mình trong quan hệ NHANVIEN, 
----- có thể chỉnh sửa số điện thoại (ĐT) của chính mình (nếu số điện thoại có thay đổi).
CREATE OR REPLACE VIEW UV_NVCB_NHANVIEN AS
    SELECT * 
    FROM NHANVIEN 
    WHERE MANV = SYS_CONTEXT('USERENV', 'SESSION_USER');

GRANT SELECT, UPDATE(DT) ON UV_NVCB_NHANVIEN TO RL_NVCB;

----- Tất cả nhân viên thuộc các vai trò còn lại đều có quyền của vai trò NVCB
GRANT RL_NVCB TO RL_GV;
GRANT RL_NVCB TO RL_NV_PDT;
GRANT RL_NVCB TO RL_NV_PKT;
GRANT RL_NVCB TO RL_NV_TCHC;
GRANT RL_NVCB TO RL_NV_CTSV;
GRANT RL_NVCB TO RL_TRGDV;

-- Tất cả nhân viên và sinh viên dùng đều có quyền xem bảng DONVI
GRANT SELECT ON QLDAIHOC.DONVI TO RL_SV, RL_NVCB;

----- Người dùng có VAITRO là "TRGDV" có quyền xem các dòng dữ liệu liên quan đến 
----- các nhân viên thuộc đơn vị mình làm trưởng, trừ các thuộc tính LUONG và PHUCAP. 
CREATE OR REPLACE VIEW UV_TRGDV_NHANVIEN AS
    SELECT MANV, HOTEN, PHAI, NGSINH, DT, VAITRO, MADV
    FROM NHANVIEN
    WHERE MADV = (
        SELECT MADV 
        FROM DONVI 
        WHERE TRGDV = SYS_CONTEXT('USERENV', 'SESSION_USER'));

GRANT SELECT ON UV_TRGDV_NHANVIEN TO RL_TRGDV;

----- Người dùng có VAITRO là "NV TCHC" có quyền xem, thêm, cập nhật, xóa trên quan hệ NHANVIEN
GRANT SELECT, INSERT, UPDATE, DELETE ON NHANVIEN TO RL_NV_TCHC;

-- Câu 2: Em hãy ép thỏa các chính sách bảo mật trên quan hệ MOMON dùng cơ chế RBAC theo mô tả bên dưới
----- Người dùng có vai trò "GV" được quyền xem các dòng phân công giảng dạy liên quan đến chính giảng viên đó. 
CREATE OR REPLACE VIEW UV_GV_MOMON AS
    SELECT * 
    FROM MOMON 
    WHERE MAGV = SYS_CONTEXT('USERENV', 'SESSION_USER');

GRANT SELECT ON UV_GV_MOMON TO RL_GV;

----- Người dùng có vai trò "NV PĐT" có quyền xem, thêm, cập nhật, xóa dòng trong bảng MOMON
----- liên quan đến học kỳ hiện tại của năm học đang diễn ra. 
CREATE OR REPLACE VIEW UV_NVPDT_MOMON AS
    SELECT * 
    FROM MOMON 
    WHERE 
        HK = (SELECT MAX(HK) FROM MOMON) AND 
        NAM = (SELECT MAX(NAM) FROM MOMON)
    WITH CHECK OPTION;

GRANT SELECT, INSERT, UPDATE, DELETE ON UV_NVPDT_MOMON TO RL_NV_PDT;

----- Người dùng có vai trò "TRGĐV" có quyền xem các dòng phân công giảng dạy 
----- của các giảng viên thuộc đơn vị mình làm trưởng. 
CREATE OR REPLACE VIEW UV_TRGDV_MOMON AS
    SELECT mm.*
    FROM MOMON mm 
        JOIN NHANVIEN nv ON nv.MANV = mm.MAGV
    WHERE nv.MADV = (
        SELECT MADV 
        FROM DONVI 
        WHERE TRGDV = SYS_CONTEXT('USERENV', 'SESSION_USER'));

GRANT SELECT ON UV_TRGDV_MOMON TO RL_TRGDV;

----- Sinh viên có quyền xem các dòng dữ liệu trong quan hệ MOMON liên quan đến các dòng
----- mở các học phần thuộc quyền phụ trách chuyên môn bởi Khoa mà sinh viên đang theo học.
CREATE OR REPLACE VIEW UV_SV_MOMON AS
    SELECT mm.*
    FROM MOMON mm 
        JOIN HOCPHAN hp ON mm.MAHP = hp.MAHP
        JOIN SINHVIEN sv ON sv.KHOA = hp.MADV
    WHERE sv.MASV = SYS_CONTEXT('USERENV', 'SESSION_USER');

GRANT SELECT ON UV_SV_MOMON TO RL_SV;

-- Câu 3: Em hãy ép thỏa các chính sách bảo mật trên quan hệ SINHVIEN dùng cơ chế VPD theo mô tả bên dưới

----- Sinh viên có thể xem dòng dữ liệu liên quan đến chính mình, có thể sửa các trường địa chỉ (ĐCHI), số điện thoại (ĐT) liên quan đến chính mình. 
----- Người dùng có vai trò "NV PCTSV” có thể thêm, xóa, sửa thông tin trên quan hệ SINHVIEN. 
-----    Tuy nhiên, trường TINHTRANG mang giá trị NULL cho đến khi người dùng với vai trò "NV PĐT” cập nhật thành giá trị mới,
-----    cho biết tình trạng học vụ của sinh viên.
----- Người dùng có vai trò "GV” được xem danh sách sinh viên thuộc đơn vị (khoa) mà giảng viên trực thuộc.
GRANT SELECT, UPDATE(DCHI, DT) ON SINHVIEN TO RL_SV;
GRANT SELECT, INSERT, UPDATE, DELETE ON SINHVIEN TO RL_NV_CTSV;
GRANT SELECT ON SINHVIEN TO RL_GV;
GRANT SELECT, UPDATE(TINHTRANG) ON SINHVIEN TO RL_NV_PDT;

CREATE OR REPLACE FUNCTION UDF_POLICY_SINHVIEN (
    p_schema VARCHAR2, p_obj VARCHAR2)
RETURN VARCHAR2 AS
    v_user VARCHAR2(20);
    v_role NVARCHAR2(20);
    v_khoa NVARCHAR2(20);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    IF v_user LIKE 'SV%' THEN
        RETURN 'MASV = ''' || v_user || '''';
    ELSIF v_user LIKE 'NV%' THEN
        SELECT VAITRO INTO v_role FROM NHANVIEN
        WHERE MANV = v_user;

        IF v_role = 'GV' THEN
            SELECT MADV INTO v_khoa 
            FROM NHANVIEN WHERE MANV = v_user;

            RETURN 'KHOA = ''' || v_khoa || '''';
        
        ELSIF v_role IN ('NV_CTSV', 'NV_PDT') THEN
            RETURN '1=1';
        END IF;
    END IF;
    
    RETURN '';
END;
/

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'QLDAIHOC',
        object_name     => 'SINHVIEN',
        policy_name     => 'POLICY_SINHVIEN',
        policy_function => 'UDF_POLICY_SINHVIEN',
        statement_types => 'SELECT, UPDATE, INSERT, DELETE',
        update_check => TRUE
    );
END;
/

CREATE OR REPLACE TRIGGER TRG_CTSV_TINHTRANG_NULL
BEFORE INSERT OR UPDATE ON SINHVIEN
FOR EACH ROW
DECLARE
    v_role VARCHAR2(20);
    cnt_old INT;
BEGIN
    IF SYS_CONTEXT('USERENV', 'SESSION_USER') LIKE 'SV%' THEN
        RETURN; -- Không làm gì nếu là sinh viên
    END IF;

    SELECT VAITRO INTO v_role 
    FROM NHANVIEN 
    WHERE MANV = SYS_CONTEXT('USERENV', 'SESSION_USER');

    IF v_role = 'NV_CTSV' THEN
        IF INSERTING THEN -- Nếu là insert
            :NEW.TINHTRANG := NULL;
        ELSIF UPDATING THEN -- Nếu là update
            :NEW.TINHTRANG := :OLD.TINHTRANG;
        END IF;
    END IF;
END;
/

-- *Câu 4*: Em hãy ép thỏa các chính sách bảo mật trên quan hệ ĐANGKY dùng cơ chế VPD theo mô tả bên dưới
----- Sinh viên được xem dữ liệu đăng ký học phần và bảng điểm liên quan đến chính sinh viên.
----- Sinh viên có thể thêm, cập nhật đăng ký, xóa các dòng dữ liệu đăng ký học phần liên quan đến chính sinh viên
-----    trong 14 ngày đầu của học kỳ. Dữ liệu về điểm số liên quan các dòng đăng ký học phần đều mang giá trị NULL.
----- Người dùng có vai trò "NV PĐT” cũng có quyền xem, thêm, xóa, sửa trên quan hệ sinh viên trong thời gian 14 ngày đầu học kỳ 
-----   theo nguyện vọng của sinh viên. Dữ liệu về điểm số liên quan các dòng đăng ký học phần đều mang giá trị NULL. 
----- Người dùng có vai trò "NV PKT” có quyền xem dữ liệu đăng ký học phần của SV và được quyền cập nhật các trường liên quan đến điểm số (theo bảng điểm do giảng viên quyết định).
----- Người dùng có vai trò "GV” có quyền xem danh sách lớp, bảng điểm các lớp học phần mà giảng viên đó phụ trách giảng dạy.
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO RL_SV, RL_NV_PDT;
GRANT SELECT, UPDATE ON DANGKY TO RL_NV_PKT;
GRANT SELECT ON DANGKY TO RL_GV;
/
CREATE OR REPLACE FUNCTION UDF_POLICY_DANGKY_SEL (
    p_schema VARCHAR2, p_obj VARCHAR2)
RETURN VARCHAR2 AS
    v_user VARCHAR2(20);
    v_role NVARCHAR2(20);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    IF v_user LIKE 'SV%' THEN
        RETURN 'MASV = ''' || v_user || '''' ;
        
    ELSIF v_user LIKE 'NV%' THEN
        SELECT VAITRO INTO v_role FROM NHANVIEN 
        WHERE MANV = v_user;

        IF v_role = 'GV' THEN
            RETURN 'MAMM IN (SELECT MAMM FROM MOMON WHERE MAGV = ''' || v_user || '''' || ')';
        ELSIF v_role IN ('NV_PDT', 'NV_PKT') THEN
            RETURN '1=1';
        END IF;
    END IF;
    RETURN '';
END;
/

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'QLDAIHOC',
        object_name     => 'DANGKY',
        policy_name     => 'POLICY_SINHVIEN_SEL',
        policy_function => 'UDF_POLICY_DANGKY_SEL',
        statement_types => 'SELECT',
        update_check => TRUE
    );
END;
/

CREATE OR REPLACE FUNCTION UDF_POLICY_DANGKY_INS_UPD (
    p_schema VARCHAR2, p_obj VARCHAR2)
RETURN VARCHAR2 AS
    v_user VARCHAR2(20);
    v_role VARCHAR2(20);
    v_hk NUMBER;
    v_nam NUMBER;
    v_start DATE;
    v_end DATE;
    v_month VARCHAR2(2);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    IF v_user LIKE 'SV%' THEN
        SELECT HK, NAM INTO v_hk, v_nam
        FROM MOMON
        WHERE MAMM IN (SELECT MAMM FROM DANGKY WHERE MASV = v_user)
        FETCH FIRST 1 ROWS ONLY;

        -- Xác định tháng của học kỳ
        CASE v_hk
            WHEN 1 THEN v_month := '09';
            WHEN 2 THEN v_month := '01';
            WHEN 3 THEN v_month := '05';
            ELSE v_month := '01';  -- Default value
        END CASE;
        
        -- Mỗi năm học có 3 học kỳ gồm 1, 2, 3 bắt đầu tương ứng vào ngày đầu tiên các tháng 9, 1, 5.
        v_start := TO_DATE('01-' || v_month || '-' || v_nam, 'DD-MM-YYYY');
        v_end := v_start + 14;

        RETURN 'MASV = ''' || v_user || ''' AND SYSDATE <= TO_DATE(''' || 
               TO_CHAR(v_end, 'DD-MM-YYYY') || ''', ''DD-MM-YYYY'')';
    END IF;
    
    RETURN '';
END;
/
BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'QLDAIHOC',
        object_name     => 'DANGKY',
        policy_name     => 'POLICY_DANGKY_INS_UPD_DEL',
        policy_function => 'UDF_POLICY_DANGKY_INS_UPD',
        statement_types => 'INSERT, UPDATE, DELETE',
        update_check => TRUE
    );
END;
/

-- Những bảng còn lại không có chính sách bảo mật thì
-- cho phép tất cả người dùng có quyền truy cập đọc
GRANT SELECT ON HOCPHAN TO RL_NVCB, RL_GV, RL_NV_PDT, RL_NV_PKT, RL_NV_TCHC, RL_NV_CTSV, RL_TRGDV, RL_SV;

-- ======================================================
-- ======= Kiểm tra các cơ chế kiểm soát truy cập =======
-- ======================================================
-- Tạo tài khoản cho tất cả người dùng (sinh viên và nhân viên)
-- Xóa các tài khoản cũ nếu có
BEGIN 
    FOR account IN (SELECT * FROM DBA_USERS WHERE USERNAME LIKE 'NV0%' OR USERNAME LIKE 'SV0%') LOOP
        -- Ngắt kết nối của user
        FOR ses IN (SELECT * FROM V$SESSION WHERE USERNAME = account.USERNAME) LOOP
            EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || ses.SID || ',' || ses.SERIAL# || ''' IMMEDIATE';
        END LOOP;
        -- Xóa tài khoản
        EXECUTE IMMEDIATE 'DROP USER ' || account.USERNAME || ' CASCADE';
    END LOOP;
END;
/
EXEC QLDAIHOC.USP_CreateAllAccounts;
SELECT * FROM DBA_USERS WHERE USERNAME LIKE 'NV0%' OR USERNAME LIKE 'SV0%';

-- ======================================================
-- ======= Kiểm tra các cơ chế kiểm soát truy cập =======
-- ======================================================
-- Tạo tài khoản cho tất cả người dùng (sinh viên và nhân viên)
-- Xóa các tài khoản cũ nếu có
BEGIN 
    FOR account IN (SELECT * FROM DBA_USERS WHERE USERNAME LIKE 'NV0%' OR USERNAME LIKE 'SV0%') LOOP
        -- Ngắt kết nối của user
        FOR ses IN (SELECT * FROM V$SESSION WHERE USERNAME = account.USERNAME) LOOP
            EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || ses.SID || ',' || ses.SERIAL# || ''' IMMEDIATE';
        END LOOP;
        -- Xóa tài khoản
        EXECUTE IMMEDIATE 'DROP USER ' || account.USERNAME || ' CASCADE';
    END LOOP;
END;
/
EXEC QLDAIHOC.USP_CreateAllAccounts;
SELECT * FROM DBA_USERS WHERE USERNAME LIKE 'NV0%' OR USERNAME LIKE 'SV0%';

-- =============================================
-- Kiểm tra chính sách RBAC với NHANVIEN
-- =============================================
-- Kiểm tra người dùng NVCB chỉ xem được dữ liệu của chính mình
-- Kiểm tra xem người dùng có thể truy cập bảng NHANVIEN trực tiếp không (Phải thất bại)
CONNECT NV00000001/NV00000001
SELECT * FROM QLDAIHOC.NHANVIEN;
-- Kiểm tra truy cập qua view (Phải thành công và chỉ hiện dữ liệu của NV00000001)
CONNECT NV00000001/NV00000001
SELECT * FROM QLDAIHOC.UV_NVCB_NHANVIEN;

-- Kiểm tra cập nhật số điện thoại
CONNECT NV00000001/NV00000001
UPDATE QLDAIHOC.UV_NVCB_NHANVIEN SET DT = '0912345678' WHERE MANV = 'NV00000001';
COMMIT;
-- Kiểm tra cập nhật lương (Phải thất bại vì không có quyền)
CONNECT NV00000001/NV00000001
UPDATE QLDAIHOC.UV_NVCB_NHANVIEN SET LUONG = 15000 WHERE MANV = 'NV00000001';

-- Kiểm tra người dùng TRGDV xem dữ liệu của đơn vị mình quản lý
-- Kiểm tra view đặc biệt cho trưởng đơn vị
-- Giả sử NV00000001 là trưởng khoa Toán cơ sở 1 (TOAN_CS1)
CONNECT NV00000001/NV00000001
SELECT * FROM QLDAIHOC.UV_TRGDV_NHANVIEN;

-- Kiểm tra người dùng NV_TCHC có toàn quyền
-- Giả sử NV00000017 là nhân viên TCHC ở cơ sở 1
-- Kiểm tra xem/chèn/cập nhật/xóa
CONNECT NV00000017/NV00000017  
SELECT * FROM QLDAIHOC.NHANVIEN;
-- Thử thêm nhân viên mới
CONNECT NV00000017/NV00000017  
INSERT INTO QLDAIHOC.NHANVIEN (HOTEN, PHAI, NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV, COSO)
VALUES ('Test User', 'Nam', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 10000, 1000, '0987654399', 'NVCB', 'TOAN_CS1', 'CS1');
COMMIT;
-- Thử cập nhật lương
CONNECT NV00000017/NV00000017  
UPDATE QLDAIHOC.NHANVIEN SET LUONG = 12000 WHERE DT = '0987654399';
COMMIT;
-- Thử xóa nhân viên test
CONNECT NV00000017/NV00000017  
DELETE FROM QLDAIHOC.NHANVIEN WHERE DT = '0987654399';
COMMIT;

-- =============================================
-- Kiểm tra chính sách RBAC với MOMON
-- =============================================
-- Kiểm tra giảng viên chỉ xem được môn họ dạy
-- Giả sử NV00000002 là giảng viên khoa Toán cơ sở 1
-- Kiểm tra xem tất cả môn học (Phải thất bại)
CONNECT NV00000002/NV00000002 
SELECT * FROM QLDAIHOC.MOMON;
-- Kiểm tra xem qua view giới hạn (Chỉ hiển thị môn của giảng viên)
CONNECT NV00000002/NV00000002 
SELECT * FROM QLDAIHOC.UV_GV_MOMON;

-- Kiểm tra NV_PDT với việc quản lý môn học
-- Giả sử NV00000015 là nhân viên PDT cơ sở 1
CONNECT NV00000015/NV00000015  
-- Kiểm tra quyền thêm/sửa/xóa trên view học kỳ hiện tại
SELECT * FROM QLDAIHOC.UV_NVPDT_MOMON;
-- Thử thêm một mở môn mới cho học kỳ hiện tại
INSERT INTO QLDAIHOC.UV_NVPDT_MOMON (MAHP, MAGV, HK, NAM)
VALUES ('MTH0001_CS1', 'NV00000002', 3, 2024);
-- Xóa môn mới thêm sau khi test
DELETE FROM QLDAIHOC.UV_NVPDT_MOMON 
WHERE MAHP = 'MTH0001_CS1' AND MAGV = 'NV00000002' AND HK = 3 AND NAM = 2024;
COMMIT;
-- Thử thêm một mở môn học kỳ trước (Phải thất bại vì WITH CHECK OPTION)
INSERT INTO QLDAIHOC.UV_NVPDT_MOMON (MAHP, MAGV, HK, NAM)
VALUES ('MTH0001_CS1', 'NV00000002', 1, 2023);

-- Kiểm tra TRGDV xem môn học của các giảng viên đơn vị mình
-- Giả sử NV00000001 là trưởng khoa Toán cơ sở 1
CONNECT NV00000001/NV00000001
-- Kiểm tra xem qua view giới hạn
SELECT * FROM QLDAIHOC.UV_TRGDV_MOMON;

-- Kiểm tra sinh viên xem môn học của khoa mình
-- Giả sử SV00000001 thuộc khoa Toán cơ sở 1
CONNECT SV00000001/SV00000001  
-- Kiểm tra danh sách môn học
SELECT * FROM QLDAIHOC.UV_SV_MOMON;

-- =============================================
-- Kiểm tra chính sách VPD với SINHVIEN
-- =============================================
-- Kiểm tra sinh viên chỉ xem và sửa dữ liệu của chính mình
CONNECT SV00000001/SV00000001
-- Kiểm tra xem tất cả sinh viên (Phải chỉ hiển thị 1 hàng)
SELECT * FROM QLDAIHOC.SINHVIEN;
-- Kiểm tra cập nhật số điện thoại
UPDATE QLDAIHOC.SINHVIEN SET DT = '0912345678' WHERE MASV = 'SV00000001';
-- Kiểm tra cập nhật TINHTRANG (Phải thất bại)
UPDATE QLDAIHOC.SINHVIEN SET TINHTRANG = 'Nghỉ học' WHERE MASV = 'SV00000001';
COMMIT;

-- Kiểm tra nhân viên CTSV thêm sinh viên mới
-- Giả sử NV00000018 là nhân viên CTSV cơ sở 1
CONNECT NV00000018/NV00000018
INSERT INTO QLDAIHOC.SINHVIEN (HOTEN, PHAI, NGSINH, DT, KHOA, TINHTRANG, COSO)
VALUES ('Sinh Viên Test', 'Nam', TO_DATE('2000-01-01', 'YYYY-MM-DD'), '0987654333', 'TOAN_CS1', 'Đang học', 'CS1');
COMMIT;
-- Kiểm tra TINHTRANG phải là NULL sau khi chèn
SELECT * FROM QLDAIHOC.SINHVIEN WHERE HOTEN = 'Sinh Viên Test';

-- Kiểm tra nhân viên PDT cập nhật TINHTRANG
-- Giả sử NV00000015 là nhân viên PDT cơ sở 1
CONNECT NV00000015/NV00000015  
UPDATE QLDAIHOC.SINHVIEN SET TINHTRANG = 'Đang học'
WHERE HOTEN = 'Sinh Viên Test';
SELECT MASV, HOTEN, TINHTRANG FROM QLDAIHOC.SINHVIEN WHERE HOTEN = 'Sinh Viên Test';
COMMIT;

-- Kiểm tra giảng viên chỉ xem được sinh viên của khoa mình
-- Giả sử NV00000002 là giảng viên khoa Toán cơ sở 1
CONNECT NV00000002/NV00000002  
-- Kiểm tra danh sách sinh viên (Chỉ sinh viên thuộc khoa Toán cơ sở 1)
SELECT * FROM QLDAIHOC.SINHVIEN;

-- =============================================
-- Kiểm tra chính sách VPD với DANGKY
-- =============================================

-- Kiểm tra sinh viên chỉ xem được đăng ký của mình
CONNECT SV00000001/SV00000001
-- Xem danh sách đăng ký
SELECT * FROM QLDAIHOC.DANGKY;
-- Kiểm tra sinh viên thêm đăng ký mới (trong kỳ đang mở đăng ký)
INSERT INTO QLDAIHOC.DANGKY (MASV, MAMM, DIEMTH, DIEMQT, DIEMTHI, DIEMTK)
VALUES ('SV00000001', 10, NULL, NULL, NULL, NULL);
COMMIT;

-- Kiểm tra giảng viên xem được đăng ký của môn mình dạy
-- Giả sử NV00000002 là giảng viên khoa Toán cơ sở 1
CONNECT NV00000002/NV00000002  
SELECT d.*, s.HOTEN
FROM QLDAIHOC.DANGKY d
    JOIN QLDAIHOC.SINHVIEN s ON d.MASV = s.MASV;

-- Kiểm tra nhân viên PDT quản lý đăng ký
-- Giả sử NV00000015 là nhân viên PDT cơ sở 1
CONNECT NV00000015/NV00000015  
-- Xem tất cả đăng ký
SELECT * FROM QLDAIHOC.DANGKY;
-- Thử thêm đăng ký cho sinh viên (trong kỳ đang mở)
INSERT INTO QLDAIHOC.DANGKY (MASV, MAMM, DIEMTH, DIEMQT, DIEMTHI, DIEMTK)
VALUES ('SV00000002', 11, NULL, NULL, NULL, NULL);
COMMIT;

-- Kiểm tra nhân viên khảo thí cập nhật điểm
-- Giả sử NV00000016 là nhân viên khảo thí cơ sở 1
CONNECT NV00000016/NV00000016  
-- Kiểm tra quyền xem tất cả đăng ký
SELECT * FROM QLDAIHOC.DANGKY;
-- Thử cập nhật điểm
UPDATE QLDAIHOC.DANGKY SET DIEMTHI = 8.5 
WHERE MASV = 'SV00000001' AND MAMM = 1;
COMMIT;