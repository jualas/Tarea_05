package jualas.es.sqlite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainApplication extends Application {

    // Método para establecer una conexión con la base de datos SQLite
    public static void connect() {
        // Cadena de conexión a la base de datos SQLite
        var url = "jdbc:sqlite:data/chinook.db";
        try (var conn = DriverManager.getConnection(url)) {
            // Mensaje de confirmación si la conexión es exitosa
            System.out.println("La conexión con SQLite se ha establecido.");
        } catch (SQLException e) {
            // Imprimir el mensaje de error si ocurre una excepción SQL
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Establecer conexión a la base de datos al iniciar la aplicación
        connect();

        // Cargar el archivo FXML que define la interfaz de usuario principal
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        // Crear una nueva escena con el contenido cargado del archivo FXML
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);


        // Establecer el título de la ventana principal de la aplicación
        stage.setTitle("Generador de Informes");

        // Establecer el ícono de la aplicación
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/asset/logo.png")));

        // Asignar la escena al escenario (ventana) y mostrarla (limitamos que se pueda redimensionar)
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    // Método principal que lanza la aplicación JavaFX
    public static void main(String[] args) {
        launch(); // Inicia la aplicación JavaFX
    }
}