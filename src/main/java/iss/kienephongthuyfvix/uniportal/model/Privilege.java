package iss.kienephongthuyfvix.uniportal.model;

import java.util.List;

public class Privilege {
    private String object;
    private String type;
    private String grantor;
    private String grantee;
    private List<String> privileges;
    private List<String> withGrantOption;
    private List<String> updateColumns;
    private List<String> originalPrivileges;

    public Privilege(String object, String type, List<String> privileges, List<String> withGrantOption, List<String> updateColumns) {
        this.object = object;
        this.type = type;
        this.privileges = privileges;
        this.withGrantOption = withGrantOption;
        this.updateColumns = updateColumns;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public List<String> getWithGrantOption() {
        return withGrantOption;
    }

    public void setWithGrantOption(List<String> withGrantOption) {
        this.withGrantOption = withGrantOption;
    }

    public List<String> getUpdateColumns() {
        return updateColumns;
    }

    public void setUpdateColumns(List<String> updateColumns) {
        this.updateColumns = updateColumns;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String privilege : privileges) {
            sb.append(privilege);
            if (withGrantOption != null && withGrantOption.contains(privilege)) {
                sb.append(" (WITH GRANT OPTION)");
            }
            sb.append(", ");
        }

        if (updateColumns != null && !updateColumns.isEmpty()) {
            sb.append("UPDATE columns: ").append(String.join(", ", updateColumns)).append(", ");
        }

        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2); // remove trailing comma
        }

        return String.format("[%s %s] -> %s", type, object, sb.toString());
    }

    public String getDisplayPrivileges() {
        StringBuilder sb = new StringBuilder();
        for (String p : privileges) {
            sb.append(p);
            if (withGrantOption != null && withGrantOption.contains(p)) {
                sb.append(" (WITH GRANT OPTION)");
            }
            sb.append(", ");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 2); // remove trailing comma
        return sb.toString();
    }

    public List<String> getOriginalPrivileges() {
        return originalPrivileges;
    }

    public void setOriginalPrivileges(List<String> originalPrivileges) {
        this.originalPrivileges = originalPrivileges;
    }

    public String getGrantor() {
        return grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }



    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

}
