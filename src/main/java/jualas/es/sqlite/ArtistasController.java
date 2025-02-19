package jualas.es.sqlite;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ArtistasController {

    @FXML
    private ListView<String> listViewArtistas;

    @FXML
    private TextField searchField;

    private ObservableList<String> artistasList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializar la lista de artistas al cargar la vista
        cargarArtistas("");
    }

    @FXML
    protected void buscarArtista() {
        // Buscar artistas basados en el texto ingresado
        String searchText = searchField.getText();
        cargarArtistas(searchText);
    }

    private void cargarArtistas(String filtro) {
        artistasList.clear();
        try (Connection connection = getDatabaseConnection();
             Statement statement = connection.createStatement()) {
            // Construir la consulta SQL para obtener los nombres de los artistas
            String query = "SELECT Name FROM artists";
            if (filtro != null && !filtro.isEmpty()) {
                query += " WHERE Name LIKE '%" + filtro + "%'";
            }
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                // Agregar cada nombre de artista a la lista observable
                artistasList.add(resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            // Mostrar un mensaje de error específico para problemas de consulta a la base de datos
            System.err.println("Error al cargar la lista de artistas: " + e.getMessage());
            e.printStackTrace();

            // Mostrar una ventana de alerta con el mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setHeaderText("No se pudo cargar la lista de artistas");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
        // Actualizar la vista de lista con los artistas obtenidos
        listViewArtistas.setItems(artistasList);
    }

    private Connection getDatabaseConnection() {
        String url = "jdbc:sqlite:data/chinook.db";
        Connection connection = null;
        try {
            // Intentar establecer una conexión con la base de datos
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
    protected void generarInformeArtista() {
        // Obtener el artista seleccionado de la lista
        String selectedArtist = listViewArtistas.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            try (Connection connection = getDatabaseConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT ArtistId FROM artists WHERE Name = ?")) {
                // Preparar la consulta para obtener el ID del artista seleccionado
                statement.setString(1, selectedArtist);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Obtener el ID del artista y generar el informe
                    int artistId = resultSet.getInt("ArtistId");
                    generarInforme(artistId);
                }
            } catch (SQLException e) {
                // Mostrar un mensaje de error específico para problemas al obtener el ID del artista
                System.err.println("Error al obtener el ID del artista: " + e.getMessage());
                e.printStackTrace();

                // Mostrar una ventana de alerta con el mensaje de error
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de Consulta");
                alert.setHeaderText("No se pudo obtener el ID del artista");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void generarInforme(int artistId) {
        try {
            // Verificar y cargar el archivo de informe
            String reportPath = "/jualas/es/sqlite/artista.jrxml";
            if (getClass().getResourceAsStream(reportPath) == null) {
                throw new IllegalArgumentException("El archivo de informe no se encuentra en la ruta especificada: " + reportPath);
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(reportPath));

            // Crear un mapa de parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ArtistId", artistId);

            // Llenar el informe con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, getDatabaseConnection());

            // Mostrar el informe
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            // Mostrar un mensaje de error específico para problemas con JasperReports
            System.err.println("Error al generar el informe del artista: " + e.getMessage());
            e.printStackTrace();

            // Mostrar una ventana de alerta con el mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Informe");
            alert.setHeaderText("No se pudo generar el informe del artista");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void cerrarEscena(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}