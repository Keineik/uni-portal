ALTER SESSION SET "_ORACLE_SCRIPT"=true;
ALTER SESSION SET CURRENT_SCHEMA = "QLDAIHOC";

-- Nếu chưa mở OLS, mở OLS trên CDB bằng các lệnh dưới
--EXEC LBACSYS.CONFIGURE_OLS;
--EXEC LBACSYS.OLS_ENFORCEMENT.ENABLE_OLS;
--SHUTDOWN IMMEDIATE;
--STARTUP;
--ALTER USER LBACSYS ACCOUNT UNLOCK;
--ALTER USER LBACSYS IDENTIFIED BY LABCSYS;

GRANT EXECUTE ON LBACSYS.SA_COMPONENTS TO QLDAIHOC WITH GRANT OPTION;
GRANT EXECUTE ON LBACSYS.sa_user_admin TO QLDAIHOC WITH GRANT OPTION;
GRANT EXECUTE ON LBACSYS.sa_label_admin TO QLDAIHOC WITH GRANT OPTION;
GRANT EXECUTE ON sa_policy_admin TO QLDAIHOC WITH GRANT OPTION;
GRANT EXECUTE ON char_to_label TO QLDAIHOC WITH GRANT OPTION;
GRANT LBAC_DBA TO QLDAIHOC;
GRANT EXECUTE ON sa_sysdba TO QLDAIHOC;
GRANT EXECUTE ON TO_LBAC_DATA_LABEL TO QLDAIHOC;

SELECT VALUE FROM v$option WHERE parameter = 'Oracle Label Security';
SELECT status FROM dba_ols_status WHERE name = 'OLS_CONFIGURE_STATUS';

-- Điều chỉnh bảng THONGBAO để lưu thêm thông tin phân loại thông báo
ALTER TABLE THONGBAO
ADD (
    NGAYTAO DATE DEFAULT SYSDATE,
    TIEUDE NVARCHAR2(100),
    CAPBAC VARCHAR2(10),
    LINHVUC NVARCHAR2(30),
    COSO VARCHAR2(7)
);

GRANT OLS_THONGBAO_DBA TO QLDAIHOC;

-- Tạo chính sách label security mới
BEGIN
    -- Xoá policy cũ nếu tồn tại
    BEGIN
        SA_SYSDBA.DROP_POLICY(
            policy_name => 'OLS_THONGBAO',
            drop_column => TRUE
        );
    EXCEPTION 
        WHEN OTHERS THEN NULL;
    END;
    
    -- Tạo policy mới
    SA_SYSDBA.CREATE_POLICY(
        policy_name => 'OLS_THONGBAO',
        column_name => 'OLS_LABEL'
    );
END;
/
EXEC SA_SYSDBA.ENABLE_POLICY ('OLS_THONGBAO');

-- a) Tạo Levels cho các cấp bậc
BEGIN
    -- Cấp bậc: Trưởng đơn vị > Nhân viên > Sinh viên
    SA_COMPONENTS.CREATE_LEVEL(
        policy_name => 'OLS_THONGBAO',
        level_num => 3000,
        short_name => 'TRGDV',
        long_name => 'Trưởng đơn vị'
    );
    
    SA_COMPONENTS.CREATE_LEVEL(
        policy_name => 'OLS_THONGBAO',
        level_num => 2000,
        short_name => 'NV',
        long_name => 'Nhân viên'
    );
    
    SA_COMPONENTS.CREATE_LEVEL(
        policy_name => 'OLS_THONGBAO',
        level_num => 1000,
        short_name => 'SV',
        long_name => 'Sinh viên'
    );
END;
/

-- b) Tạo Compartments cho lĩnh vực
BEGIN
    -- Lĩnh vực hoạt động: Toán, Lý, Hóa, Hành chính
    -- Phân theo CS1 và CS2 để phản ánh cấu trúc mới
    
    -- Khoa Toán
    SA_COMPONENTS.CREATE_COMPARTMENT(
        policy_name => 'OLS_THONGBAO',
        comp_num => 10,
        short_name => 'TOAN',
        long_name => 'Khoa Toán Tin'
    );
    
    -- Khoa Lý
    SA_COMPONENTS.CREATE_COMPARTMENT(
        policy_name => 'OLS_THONGBAO',
        comp_num => 20,
        short_name => 'LY',
        long_name => 'Khoa Vật lý'
    );
    
    -- Khoa Hóa
    SA_COMPONENTS.CREATE_COMPARTMENT(
        policy_name => 'OLS_THONGBAO',
        comp_num => 30,
        short_name => 'HOA',
        long_name => 'Khoa Hóa học'
    );
    
    -- Hành chính
    SA_COMPONENTS.CREATE_COMPARTMENT(
        policy_name => 'OLS_THONGBAO',
        comp_num => 40,
        short_name => 'HC',
        long_name => 'Hành chính'
    );
END;
/

-- c) Tạo Groups cho vị trí địa lý
BEGIN
    -- Vị trí cơ sở
    SA_COMPONENTS.CREATE_GROUP(
        policy_name => 'OLS_THONGBAO',
        group_num => 1,
        short_name => 'CS1',
        long_name => 'Cơ sở 1'
    );
    
    SA_COMPONENTS.CREATE_GROUP(
        policy_name => 'OLS_THONGBAO',
        group_num => 2,
        short_name => 'CS2',
        long_name => 'Cơ sở 2'
    );
END;
/



-- Áp dụng chính sách OLS vào bảng THONGBAO
BEGIN
    SA_POLICY_ADMIN.APPLY_TABLE_POLICY(
        policy_name => 'OLS_THONGBAO',
        schema_name => 'QLDAIHOC',
        table_name => 'THONGBAO',
        table_options => 'READ_CONTROL'
    );
END;
/

-- Gán toàn bộ label (full access) vào acc admin
BEGIN
    
    SA_USER_ADMIN.SET_USER_LABELS(
        policy_name    => 'OLS_THONGBAO',
        user_name      => 'QLDAIHOC',
        max_read_label => 'TRGDV:TOAN,LY,HOA,HC:CS1,CS2',
        max_write_label => 'TRGDV:TOAN,LY,HOA,HC:CS1,CS2',
        def_label      => 'TRGDV:TOAN,LY,HOA,HC:CS1,CS2',
        row_label      => 'TRGDV:TOAN,LY,HOA,HC:CS1,CS2'
    );
END;
/
-- =========================
-- Stored Procedures
-- =========================
-- 1. Tự động gán label cho tất cả user
CREATE OR REPLACE PROCEDURE USP_AssignOLSLabels
AS
BEGIN
    -- Gán nhãn cho trưởng các đơn vị
    FOR trgdv_rec IN (SELECT nv.MANV, dv.MADV, nv.COSO
                     FROM QLDAIHOC.NHANVIEN nv 
                     JOIN QLDAIHOC.DONVI dv ON nv.MANV = dv.TRGDV) LOOP
        DECLARE
            v_linhvuc VARCHAR2(10);
        BEGIN
            -- Trích xuất lĩnh vực cơ bản từ MADV
            IF INSTR(trgdv_rec.MADV, 'TOAN_') > 0 THEN
                v_linhvuc := 'TOAN';
            ELSIF INSTR(trgdv_rec.MADV, 'LY_') > 0 THEN
                v_linhvuc := 'LY';
            ELSIF INSTR(trgdv_rec.MADV, 'HOA_') > 0 THEN
                v_linhvuc := 'HOA';
            ELSE
                v_linhvuc := 'HC';
            END IF;
            
            -- Trưởng đơn vị chỉ đọc thông báo của đơn vị và cơ sở mình
            SA_USER_ADMIN.SET_USER_LABELS(
                policy_name => 'OLS_THONGBAO',
                user_name => trgdv_rec.MANV,
                max_read_label => 'TRGDV:' || v_linhvuc || ':' || trgdv_rec.COSO,
                max_write_label => 'TRGDV:' || v_linhvuc || ':' || trgdv_rec.COSO,
                def_label => 'TRGDV:' || v_linhvuc || ':' || trgdv_rec.COSO,
                row_label => 'TRGDV:' || v_linhvuc || ':' || trgdv_rec.COSO
            );
            
            DBMS_OUTPUT.PUT_LINE('Đã gán nhãn cho trưởng đơn vị: ' || trgdv_rec.MANV || 
                                 ' với label: TRGDV:' || v_linhvuc || ':' || trgdv_rec.COSO);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Lỗi khi gán nhãn cho trưởng đơn vị ' || trgdv_rec.MANV || ': ' || SQLERRM);
        END;
    END LOOP;
    
    -- Gán nhãn cho nhân viên thường
    FOR nv_rec IN (SELECT MANV, MADV, COSO, VAITRO 
                  FROM QLDAIHOC.NHANVIEN 
                  WHERE MANV NOT IN (SELECT TRGDV FROM QLDAIHOC.DONVI WHERE TRGDV IS NOT NULL)) LOOP
        DECLARE
            v_linhvuc VARCHAR2(10);
            v_read_label VARCHAR2(100);
        BEGIN
            -- Nhân viên cơ bản đọc thông báo cấp NV cơ bản của cơ sở đó
            -- Nhân viên thuộc đơn vị nào chỉ đọc thông báo cấp NV thuộc đơn vị và cơ sở đó
            
            -- Trích xuất lĩnh vực
            IF INSTR(nv_rec.MADV, 'TOAN_') > 0 THEN
                v_linhvuc := 'TOAN';
            ELSIF INSTR(nv_rec.MADV, 'LY_') > 0 THEN
                v_linhvuc := 'LY';
            ELSIF INSTR(nv_rec.MADV, 'HOA_') > 0 THEN
                v_linhvuc := 'HOA';
            ELSIF INSTR(nv_rec.MADV, 'TCHC_') > 0 THEN
                v_linhvuc := 'HC';
            ELSE
                v_linhvuc := '';  -- Nhân viên cơ bản không thuộc lĩnh vực nào cụ thể
            END IF;
            
            -- Xác định nhãn đọc
            IF v_linhvuc = '' THEN
                -- Nhân viên cơ bản chỉ đọc thông báo cấp NV cơ bản (không thuộc TOAN, LY, HOA, HC) của cơ sở đó
                v_read_label := 'NV::' || nv_rec.COSO;
            ELSE
                -- Nhân viên thuộc đơn vị nào chỉ đọc thông báo cấp NV thuộc đơn vị và cơ sở đó
                v_read_label := 'NV:' || v_linhvuc || ':' || nv_rec.COSO;
            END IF;
            
            -- Gán nhãn cho user
            SA_USER_ADMIN.SET_USER_LABELS(
                policy_name => 'OLS_THONGBAO',
                user_name => nv_rec.MANV,
                max_read_label => v_read_label,
                max_write_label => v_read_label,
                def_label => v_read_label,
                row_label => v_read_label
            );
            
            DBMS_OUTPUT.PUT_LINE('Đã gán nhãn cho nhân viên: ' || nv_rec.MANV || ' với label: ' || v_read_label);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Lỗi khi gán nhãn cho nhân viên ' || nv_rec.MANV || ': ' || SQLERRM);
        END;
    END LOOP;
    
    -- Gán nhãn cho sinh viên
    FOR sv_rec IN (SELECT MASV, KHOA, COSO FROM QLDAIHOC.SINHVIEN) LOOP
        DECLARE
            v_linhvuc VARCHAR2(10);
            v_read_label VARCHAR2(100);
        BEGIN
            -- Sinh viên thuộc khoa, cơ sở nào thì chỉ được đọc thông báo của cấp SV của khoa, cơ sở đó
            
            -- Trích xuất lĩnh vực từ KHOA
            IF INSTR(sv_rec.KHOA, 'TOAN_') > 0 THEN
                v_linhvuc := 'TOAN';
            ELSIF INSTR(sv_rec.KHOA, 'LY_') > 0 THEN
                v_linhvuc := 'LY';
            ELSIF INSTR(sv_rec.KHOA, 'HOA_') > 0 THEN
                v_linhvuc := 'HOA';
            ELSE
                v_linhvuc := '';  -- Sinh viên không thuộc khoa nào cụ thể
            END IF;
            
            -- Xác định nhãn đọc
            IF v_linhvuc = '' THEN
                -- Sinh viên không thuộc khoa nào cụ thể, chỉ đọc thông báo cấp SV chung của cơ sở
                v_read_label := 'SV::' || sv_rec.COSO;
            ELSE
                -- Sinh viên thuộc khoa cụ thể, đọc thông báo cấp SV của khoa và cơ sở đó
                v_read_label := 'SV:' || v_linhvuc || ':' || sv_rec.COSO;
            END IF;
            
            -- Gán nhãn cho user
            SA_USER_ADMIN.SET_USER_LABELS(
                policy_name => 'OLS_THONGBAO',
                user_name => sv_rec.MASV,
                max_read_label => v_read_label,
                max_write_label => v_read_label,
                def_label => v_read_label,
                row_label => v_read_label
            );
            
            DBMS_OUTPUT.PUT_LINE('Đã gán nhãn cho sinh viên: ' || sv_rec.MASV || ' với label: ' || v_read_label);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Lỗi khi gán nhãn cho sinh viên ' || sv_rec.MASV || ': ' || SQLERRM);
        END;
    END LOOP;
    
    -- Gán quyền cho DBA (chỉ thực hiện nếu là DBA)
    BEGIN
        IF SYS_CONTEXT('USERENV', 'ISDBA') = 'TRUE' THEN
            SA_USER_ADMIN.SET_USER_LABELS(
                policy_name => 'OLS_THONGBAO',
                user_name => SYS_CONTEXT('USERENV', 'SESSION_USER'),
                max_read_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2',  -- Đọc tất cả
                max_write_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2', -- Ghi tất cả
                def_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2',
                row_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2'
            );
            
            DBMS_OUTPUT.PUT_LINE('Đã gán nhãn cho DBA với quyền đầy đủ');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Lỗi khi gán nhãn cho DBA: ' || SQLERRM);
    END;
    
    DBMS_OUTPUT.PUT_LINE('Hoàn thành gán nhãn OLS cho người dùng');
END;
/
EXEC USP_AssignOLSLabels;

GRANT SELECT ON THONGBAO TO RL_SV, RL_NVCB;

-- 2. Thêm thông báo mới
CREATE OR REPLACE PROCEDURE USP_DBA_ThemThongBao(
    p_tieude IN NVARCHAR2,
    p_noidung IN NVARCHAR2,
    p_capbac_name IN VARCHAR2,    -- Trưởng đơn vị, Nhân viên, Sinh viên
    p_linhvuc_name IN VARCHAR2,   -- Khoa Toán Tin, Khoa Vật lý, Khoa Hóa học, Hành chính
    p_coso_name IN VARCHAR2       -- Cơ sở 1, Cơ sở 2, hoặc "Cơ sở 1, Cơ sở 2"
) AS
    v_label VARCHAR2(100);
    v_capbac VARCHAR2(10);
    v_linhvuc VARCHAR2(30) := '';
    v_coso VARCHAR2(10);
BEGIN
    -- Kiểm tra người dùng có phải là SYSDBA
--    IF SYS_CONTEXT('USERENV', 'ISDBA') != 'TRUE' THEN
--        RAISE_APPLICATION_ERROR(-20001, 'Chỉ DBA mới có thể thực hiện procedure này');
--    END IF;
    
    -- Chuyển đổi tên dài của cấp bậc thành tên ngắn
    CASE p_capbac_name
        WHEN 'Trưởng đơn vị' THEN v_capbac := 'TRGDV';
        WHEN 'Nhân viên' THEN v_capbac := 'NV';
        WHEN 'Sinh viên' THEN v_capbac := 'SV';
        ELSE RAISE_APPLICATION_ERROR(-20002, 'Cấp bậc không hợp lệ: ' || p_capbac_name);
    END CASE;
    
    -- Chuyển đổi tên dài của lĩnh vực thành tên ngắn
    -- Chuyển hết xuống viết thường
    v_linhvuc := '';
    IF INSTR(LOWER(p_linhvuc_name), 'toán') > 0 THEN
        v_linhvuc := v_linhvuc || 'TOAN,'; 
    END IF;
    IF INSTR(LOWER(p_linhvuc_name), 'lý') > 0 THEN
        v_linhvuc := v_linhvuc || 'LY,';
    END IF;
    IF INSTR(LOWER(p_linhvuc_name), 'hóa') > 0 THEN
        v_linhvuc := v_linhvuc || 'HOA,';
    END IF;
    IF INSTR(LOWER(p_linhvuc_name), 'hành chính') > 0 THEN
        v_linhvuc := v_linhvuc || 'HC,';
    END IF;
    -- Bỏ dấu phẩy cuối nếu có
    IF v_linhvuc IS NOT NULL AND SUBSTR(v_linhvuc, -1) = ',' THEN
        v_linhvuc := SUBSTR(v_linhvuc, 1, LENGTH(v_linhvuc) - 1);
    END IF;
    
    -- Chuyển đổi tên dài của cơ sở thành tên ngắn
    v_coso := '';
    IF INSTR(p_coso_name, '1') > 0 THEN
        v_coso := v_coso || 'CS1,';
    END IF;
    IF INSTR(p_coso_name, '2') > 0 THEN
        v_coso := v_coso || 'CS2,';
    END IF;
    -- Bỏ dấu phẩy cuối nếu có
    IF v_coso IS NOT NULL AND SUBSTR(v_coso, -1) = ',' THEN
        v_coso := SUBSTR(v_coso, 1, LENGTH(v_coso) - 1);
    END IF;
    
    -- Tạo label dựa trên tham số đã chuyển đổi
    v_label := v_capbac || ':' || v_linhvuc || ':' || v_coso;
    
    -- Insert thông báo với nhãn OLS tương ứng (đã bỏ cột NGUOITAO)
    INSERT INTO THONGBAO(TIEUDE, NOIDUNG, NGAYTAO, CAPBAC, LINHVUC, COSO, OLS_LABEL)
    VALUES(p_tieude, p_noidung, SYSDATE, v_capbac, v_linhvuc, v_coso, CHAR_TO_LABEL('OLS_THONGBAO', v_label));
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Lỗi khi thêm thông báo: ' || SQLERRM);
        ROLLBACK;
        RAISE;
END;
/