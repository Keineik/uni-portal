module iss.kienephongthuyfvix.hcmums {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires com.oracle.database.jdbc;
    requires static lombok;
    requires org.slf4j;

    opens iss.kienephongthuyfvix.uniportal.controller to javafx.fxml;
    exports iss.kienephongthuyfvix.uniportal;
}