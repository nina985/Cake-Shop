module main{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens main to javafx.fxml;

    exports main;
    exports service;
}