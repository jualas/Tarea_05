package jualas.es.sqlite;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private VBox rootNode;

    @FXML
    protected void generateCustomerReport() {
        try {
            // Verificar y cargar el archivo de informe
            String reportPath = "/jualas/es/sqlite/cliente.jrxml";
            if (getClass().getResourceAsStream(reportPath) == null) {
                throw new IllegalArgumentException("El archivo de informe no se encuentra en la ruta especificada: " + reportPath);
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(reportPath));

            // Crear un mapa de parámetros si es necesario
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CompanyName", "Nombre de la Empresa");
            // Agregar más parámetros según sea necesario

            // Llenar el informe con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, getDatabaseConnection());

            // Mostrar el informe
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            // Mostrar un mensaje de error específico para problemas con JasperReports
            System.err.println("Error al generar el informe: " + e.getMessage());
            e.printStackTrace();

            // Mostrar una ventana de alerta con el mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Informe");
            alert.setHeaderText("No se pudo generar el informe del cliente");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Método para obtener la conexión a la base de datos
    private Connection getDatabaseConnection() {
        String url = "jdbc:sqlite:data/chinook.db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            // Mostrar un mensaje de error específico para problemas de conexión a la base de datos
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();

            // Mostrar una ventana de alerta con el mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("No se pudo conectar con la base de datos");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
        return connection;
    }

    @FXML
    protected void generateArtistReport() {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jualas/es/sqlite/artistas-view.fxml"));
            BorderPane artistasRoot = fxmlLoader.load();

            // Crear una nueva escena con el contenido cargado
            Scene scene = new Scene(artistasRoot);

            // Crear una nueva ventana (Stage)
            Stage stage = new Stage();
            stage.setTitle("Informe de Artistas");
            stage.setScene(scene);

            // Cargar el logo.png
            javafx.scene.image.Image logo = new javafx.scene.image.Image(getClass().getResourceAsStream("/asset/logo.png"));
            stage.getIcons() .add(logo);

            // Mostrar la nueva ventana con la limitacion de que no se pueda redimensionar
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            // Mostrar un mensaje de error específico para problemas al cargar el archivo FXML
            System.err.println("Error al cargar la vista de artistas: " + e.getMessage());
            e.printStackTrace();

            // Mostrar una ventana de alerta con el mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudo cargar la vista de artistas");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void closeApplication() {
        // Código para cerrar la aplicación
        System.exit(0);
    }
}