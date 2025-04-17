module iss.kienephongthuyfvix.hcmums {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.oracle.database.jdbc;
    requires static lombok;
    requires com.jfoenix;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires org.slf4j;
    requires java.compiler;

    opens iss.kienephongthuyfvix.uniportal.controller to javafx.fxml;
    exports iss.kienephongthuyfvix.uniportal;
    opens iss.kienephongthuyfvix.uniportal.controller.DBA to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVPDT to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVPKT to javafx.fxml;
    //opens iss.kienephongthuyfvix.uniportal.controller.NVPCTSV to javafx.fxml;
    //opens iss.kienephongthuyfvix.uniportal.controller.NVTCHC to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.GV to javafx.fxml;
    //opens iss.kienephongthuyfvix.uniportal.controller.TDV to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.SV to javafx.fxml;
    opens iss.kienephongthuyfvix.uniportal.controller.NVCB to javafx.fxml;
}