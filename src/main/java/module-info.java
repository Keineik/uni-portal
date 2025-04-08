module iss.kienephongthuyfvix.hcmums {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires com.oracle.database.jdbc;
    requires static lombok;
    requires org.slf4j;
    requires com.jfoenix;
    requires org.kordamp.ikonli.javafx;

    opens iss.kienephongthuyfvix.uniportal.controller to javafx.fxml;
    exports iss.kienephongthuyfvix.uniportal;
}