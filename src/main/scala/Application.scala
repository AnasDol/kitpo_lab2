import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

object Main extends App {
    Application.launch(classOf[MainApp], args: _*)
}

class MainApp extends Application {

    override def start(primaryStage: Stage): Unit = {
        val loader = new FXMLLoader(getClass.getResource("view.fxml"))
        val root: Parent = loader.load()
        val controller: Controller = loader.getController
        val scene = new Scene(root, 300, 300)

        primaryStage.setTitle("Lab 2")
        primaryStage.setScene(scene)
        primaryStage.show()
    }
}