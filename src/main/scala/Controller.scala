import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.scene.layout.{Pane, VBox}
import javafx.stage.{FileChooser, Stage}

import java.io._
import java.time.format.DateTimeFormatter
import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class Controller {
    @FXML private var chooseTypePane: Pane = _
    @FXML private var chooseTypeButton: Button = _
    @FXML private var parentPane: Pane = _
    @FXML private var addDataPane: Pane = _
    @FXML private var chooseDatatypeCombobox: ComboBox[String] = _
    @FXML private var addDataToEndButton: Button = _
    @FXML private var addIntVbox: VBox = _
    @FXML private var enterIntTextfield: TextField = _
    @FXML private var addDateVbox: VBox = _
    @FXML private var addObjectButton: Button = _
    @FXML private var getObjectButton: Button = _
    @FXML private var sortButton: Button = _
    @FXML private var menuPane: Pane = _
    @FXML private var datePicker: DatePicker = _
    @FXML private var listView: ListView[String] = _
    @FXML private var saveButton: Button = _
    @FXML private var loadButton: Button = _
    @FXML private var delObjectButton: Button = _
    @FXML private var delObjectTextfield: TextField = _
    @FXML private var getObjectTextfield: TextField = _
    @FXML private var addDataToBeginButton: Button = _
    @FXML private var addDataToIndexButton: Button = _
    @FXML private var addDataToIndexTextfield: TextField = _
    @FXML private var backToMenuButton: Button = _
    @FXML private var addUniVbox: VBox = _
    @FXML private var enterUniTextfield: TextField = _

    private var dtf: DataTypeFactory = _
    private var list: MyList[IDataType] = _
    private var proto: IDataType = _
    private final val sizeMenu = 550
    private final val sizeDef = 300

    def initialize(): Unit = {
        menuPane.setVisible(false)
        addDataPane.setVisible(false)
        chooseTypePane.setVisible(true)
        dtf = new DataTypeFactory()
        val type_names: ArrayBuffer[String] = dtf.getDataTypes
        chooseDatatypeCombobox.getItems.addAll(type_names.asJava)
    }

    @FXML private def choiceDatatypeComboboxClick(event: ActionEvent): Unit = {
        chooseTypeButton.setDisable(chooseDatatypeCombobox.getValue.isEmpty)
    }

    @FXML private def chooseTypeButtonClick(event: ActionEvent): Unit = {
        list = new MyList[IDataType]()
        proto = dtf.createDataType(chooseDatatypeCombobox.getValue)
        chooseTypePane.setVisible(false)
        resizeWindow(sizeMenu)
        menuPane.setVisible(true)
        chooseTypeButton.setDisable(true)
        chooseDatatypeCombobox.setDisable(true)
    }

    private def addDataControlType(mode: Int): Unit = {
        if (proto.getTypeName.equals("MyInteger")) {
            addData(mode, enterIntTextfield.getText)
        } else if (proto.getTypeName.equals("MyDate")) {
            addData(mode, datePicker.getValue.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        } else {
            addData(mode, enterUniTextfield.getText)
        }
    }

    private def addData(mode: Int, value: String): Unit = {
        if (mode == 1) {
            try {
                list.pushEnd(proto.parseValue(value))
            } catch {
                case e: Exception => inputAlert(e.getMessage)
            }
        } else if (mode == 2) {
            try {
                list.pushBegin(proto.parseValue(value))
            } catch {
                case e: Exception => inputAlert(e.getMessage)
            }
        } else if (mode == 3) {
            try {
                val number: Int = Integer.parseInt(addDataToIndexTextfield.getText)
                if (number >= 1 && number <= list.size + 1) {
                    try {
                        if (!list.insert(proto.parseValue(value), number)) {
                            accessErrorAlert()
                        }
                    } catch {
                        case e: Exception => inputAlert(e.getMessage)
                    }
                } else {
                    accessErrorAlert()
                }
            } catch {
                case ex: NumberFormatException => inputAlert(ex.getMessage)
            }
        }
        updateList()
        backToMenuButtonClick()
    }

    @FXML def addDataToEndButtonClick(): Unit = {
        addDataControlType(1)
    }

    @FXML def addDataToBeginButtonClick(): Unit = {
        addDataControlType(2)
    }

    @FXML def addDataToIndexButtonClick(): Unit = {
        addDataControlType(3)
    }

    @FXML def addObjectButtonClick(): Unit = {
        resizeWindow(sizeDef)
        menuPane.setVisible(false)
        addDataPane.setVisible(true)
        if (proto.getTypeName.equals("MyInteger")) {
            addIntVbox.setVisible(true)
        } else if (proto.getTypeName.equals("MyDate")) {
            addDateVbox.setVisible(true)
        } else {
            addUniVbox.setVisible(true)
        }
        enterUniTextfield.clear()
        enterIntTextfield.clear()
        datePicker.setValue(null)
    }

    @FXML def getObjectButtonClick(): Unit = {
        try {
            val num: Int = Integer.parseInt(getObjectTextfield.getText)
            if (list.getValue(num) != null) {
                showValueAlert(num)
            } else {
                accessErrorAlert()
            }
        } catch {
            case ex: NumberFormatException => inputAlert(ex.getMessage)
        }
    }

    @FXML def sortButtonClick(): Unit = {
        //    list = list.sort(proto.getTypeComparator)
        list = list.quickSort(proto.getTypeComparator)
        updateList()
    }

    @FXML def delObjectButtonClick(): Unit = {
        try {
            val num: Int = Integer.parseInt(delObjectTextfield.getText)
            if (!list.remove(num)) {
                accessErrorAlert()
            }
            updateList()
        } catch {
            case ex: NumberFormatException => inputAlert(ex.getMessage)
        }
    }

    @FXML def saveButtonClick(): Unit = {
        var filepath: String = null
        if ((filepath = chooseSaveFile()) != null) {
            // save
            var oos: ObjectOutputStream = null
            try {
                oos = new ObjectOutputStream(new FileOutputStream(filepath))
                oos.writeObject(list)
            } catch {
                case ex: Exception =>
                    System.out.println("Error: " + ex.getMessage)
            } finally if (oos != null) try oos.close()
            catch {
                case ex: IOException =>
                    ex.printStackTrace()
            }
        } else {
            fileAlert()
        }
    }

    @FXML def loadButtonClick(): Unit = {
        var filepath: String = null
        if ((filepath = chooseLoadFile()) != null) {
            // load
            var ois: ObjectInputStream = null
            try {
                ois = new ObjectInputStream(new FileInputStream(filepath))
                list = ois.readObject.asInstanceOf[MyList[IDataType]]
            } catch {
                case ex: Exception =>
                    System.out.println("Error: " + ex.getMessage)
            } finally if (ois != null) try ois.close()
            catch {
                case ex: IOException =>
                    ex.printStackTrace()
            }
            updateList()
        } else {
            fileAlert()
        }
    }

    private def inputAlert(errorMessage: String): Unit = {
        val alert: Alert = new Alert(Alert.AlertType.ERROR)
        alert.setTitle("Ошибка")
        alert.setHeaderText("Неверный ввод")
        alert.setContentText(errorMessage)
        alert.showAndWait()
    }

    private def accessErrorAlert(): Unit = {
        val alert: Alert = new Alert(Alert.AlertType.ERROR)
        alert.setTitle("Ошибка")
        alert.setHeaderText("Невозможно получить доступ к объекту")
        alert.setContentText("Пожалуйста, проверьте введенные данные.")
        alert.showAndWait()
    }

    private def showValueAlert(id: Int): Unit = {
        val alert: Alert = new Alert(Alert.AlertType.INFORMATION)
        alert.setHeaderText("Получение объекта по номеру")
        alert.setContentText("Номер: " + id + "\nЗначение: " + list.getValue(id).getOrElse("данных нет"))
        alert.showAndWait()
    }

    private def fileAlert(): Unit = {
        val alert: Alert = new Alert(Alert.AlertType.WARNING)
        alert.setTitle("Внимание!")
        alert.setHeaderText("Файл не был выбран")
        alert.setContentText("Пожалуйста, выберите файл.")
        alert.showAndWait()
    }

    private def resizeWindow(x: Int): Unit = {
        val stage: Stage = parentPane.getScene.getWindow.asInstanceOf[Stage]
        stage.setWidth(x)
    }

    private def updateList(): Unit = {
        val l: util.ArrayList[String] = new util.ArrayList[String]
        for (i <- 1 to list.size) {
            if (list.getValue(i) != null) l.add(i + ")  " + list.getValue(i).getOrElse("Null").toString)
        }
        listView.getItems.clear()
        listView.getItems.addAll(l)
    }

    private def chooseSaveFile(): String = {
        val fileChooser: FileChooser = new FileChooser
        fileChooser.setTitle("Создайте файл для сохранения")
        val extFilter = new FileChooser.ExtensionFilter("List data extensions", "*." + proto.getTypeName)
        fileChooser.getExtensionFilters.add(extFilter)
        val selectedFile: File = fileChooser.showSaveDialog(new Stage)
        if (selectedFile != null) {
            selectedFile.getAbsolutePath
        } else {
            null
        }
    }

    private def chooseLoadFile(): String = {
        val fileChooser: FileChooser = new FileChooser
        fileChooser.setTitle("Выберите файл для загрузки")
        val extFilter = new FileChooser.ExtensionFilter("List data extensions", "*." + proto.getTypeName)
        fileChooser.getExtensionFilters.add(extFilter)
        val selectedFile: File = fileChooser.showOpenDialog(new Stage)
        if (selectedFile != null) {
            selectedFile.getAbsolutePath
        } else {
            null
        }
    }

    @FXML private def backToMenuButtonClick(): Unit = {
        addDataPane.setVisible(false)
        resizeWindow(sizeMenu)
        menuPane.setVisible(true)
    }
}