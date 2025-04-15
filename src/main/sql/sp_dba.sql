ALTER SESSION SET CURRENT_SCHEMA = QLDAIHOC;

CREATE OR REPLACE PROCEDURE QLDAIHOC.USP_CreateAllAccounts
AS
BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET "_ORACLE_SCRIPT"=true';
    
    -- Tạo tài khoản cho nhân viên (NHANVIEN)
    FOR emp_rec IN (SELECT MANV, VAITRO FROM QLDAIHOC.NHANVIEN) LOOP
    BEGIN
        -- Tạo tài khoản
        EXECUTE IMMEDIATE 'CREATE USER ' || emp_rec.MANV || ' IDENTIFIED BY ' || emp_rec.MANV;
        -- Cấp quyền cơ bản
        EXECUTE IMMEDIATE 'GRANT CONNECT, CREATE SESSION, RESOURCE TO ' || emp_rec.MANV;
        -- Cấp quyền dựa trên VAITRO
        EXECUTE IMMEDIATE 'GRANT RL_' || emp_rec.VAITRO || ' TO ' || emp_rec.MANV;
        -- Log lại kết quả
        DBMS_OUTPUT.PUT_LINE('Created account for employee: ' || emp_rec.MANV || ' with role RL_' || emp_rec.VAITRO);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error creating account for employee ' || emp_rec.MANV || ': ' || SQLERRM);
    END;
    END LOOP;
    
    -- Create accounts for students (SINHVIEN)
    FOR std_rec IN (SELECT MASV FROM QLDAIHOC.SINHVIEN) LOOP
        BEGIN
            -- Tạo tài khoản
            EXECUTE IMMEDIATE 'CREATE USER ' || std_rec.MASV || ' IDENTIFIED BY ' || std_rec.MASV;
            -- Cấp quyền cơ bản
            EXECUTE IMMEDIATE 'GRANT CONNECT, CREATE SESSION, RESOURCE TO ' || std_rec.MASV;
            -- Cấp quyền sinh viên
            EXECUTE IMMEDIATE 'GRANT RL_SV TO ' || std_rec.MASV;
            -- Log lại kết quả
            DBMS_OUTPUT.PUT_LINE('Created account for student: ' || std_rec.MASV || ' with role RL_SV');
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Error creating account for student ' || std_rec.MASV || ': ' || SQLERRM);
        END;
    END LOOP;
    
    DBMS_OUTPUT.PUT_LINE('Account creation completed');
END;
/