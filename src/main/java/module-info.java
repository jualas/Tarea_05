module jualas.es.sqlite {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;
    requires java.desktop;
    requires org.apache.logging.log4j;

    opens jualas.es.sqlite to javafx.fxml, jasperreports;
    exports jualas.es.sqlite;
}