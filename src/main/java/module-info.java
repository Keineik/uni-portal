module iss.kienephongthuyfvix.hcmums {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires com.oracle.database.jdbc;
    requires static lombok;
    requires org.slf4j;
    requires com.jfoenix;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens iss.kienephongthuyfvix.uniportal.controller to javafx.fxml;
    exports iss.kienephongthuyfvix.uniportal;
    opens iss.kienephongthuyfvix.uniportal.controller.DBA to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVPDT to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVPKT to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVPCTSV to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVTCHC to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.GV to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.TDV to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.SV to javafx.fxml;
}