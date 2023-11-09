import javafx.fxml.FXML
import javafx.scene.control._
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io._
import java.time.format.DateTimeFormatter
import java.util

class Controller {
    private val chooseTypePane: Pane = null
    private val chooseTypeButton: Button = null
    private val parentPane: Pane = null
    private val addDataPane: Pane = null
    private val chooseDatatypeCombobox: ComboBox[String] = null
    private val addDataToEndButton: Button = null
    private val addIntVbox: VBox = null
    private val enterIntTextfield: TextField = null
    private val addDateVbox: VBox = null
    private val addObjectButton: Button = null
    private val getObjectButton: Button = null
    private val sortButton: Button = null
    private val menuPane: Pane = null
    private val datePicker: DatePicker = null
    private val listView: ListView[String] = null
    private val saveButton: Button = null
    private val loadButton: Button = null
    private val delObjectButton: Button = null
    private val delObjectTextfield: TextField = null
    private val getObjectTextfield: TextField = null
    private val addDataToBeginButton: Button = null
    private val addDataToIndexButton: Button = null
    private val addDataToIndexTextfield: TextField = null
    private val backToMenuButton: Button = null
    private val addUniVbox: VBox = null
    private val enterUniTextfield: TextField = null
    private[lab1] var dtf: Null = _
    private[lab1] var list: Null = _
    private[lab1] var proto: Null = _
    final private val sizeMenu = 560
    final private val sizeDef = 310
    // final private val canonical = "com.ktpo.lab1."

    def initialize(): Unit = {
        menuPane.setVisible(false)
        addDataPane.setVisible(false)
        chooseTypePane.setVisible(true)
        dtf = new Nothing
        val type_names = dtf.getDataTypes
        chooseDatatypeCombobox.getItems.addAll(type_names)
    }

    @FXML private def choiceDatatypeComboboxClick(): Unit = {
        chooseTypeButton.setDisable(chooseDatatypeCombobox.getValue.isEmpty)
    }

    @FXML private def chooseTypeButtonClick(): Unit = {
        list = new Nothing
        proto = dtf.createDataType(canonical + chooseDatatypeCombobox.getValue)
        chooseTypePane.setVisible(false)
        resizeWindow(sizeMenu)
        menuPane.setVisible(true)
        chooseTypeButton.setDisable(true)
        chooseDatatypeCombobox.setDisable(true)
    }

    private def addDataControlType(mode: Int): Unit = {
        if (proto.getTypeName.equals("MyInteger")) addData(mode, enterIntTextfield.getText)
        else if (proto.getTypeName.equals("MyDate")) addData(mode, datePicker.getValue.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        else addData(mode, enterUniTextfield.getText)
    }

    private def addData(mode: Int, value: String): Unit = {
        if (mode == 1) try list.pushEnd(proto.parseValue(value))
        catch {
            case e: Exception =>
                inputAlert(e.getMessage)
        }
        else if (mode == 2) try list.pushBegin(proto.parseValue(value))
        catch {
            case e: Exception =>
                inputAlert(e.getMessage)
        }
        else if (mode == 3) try {
            val number = addDataToIndexTextfield.getText.toInt
            if (number >= 1 && number <= list.size + 1) try if (!list.insert(proto.parseValue(value), number)) accessErrorAlert()
            catch {
                case e: Exception =>
                    inputAlert(e.getMessage)
            }
            else accessErrorAlert()
        } catch {
            case ex: NumberFormatException =>
                inputAlert(ex.getMessage)
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
        if (proto.getTypeName.equals("MyInteger")) addIntVbox.setVisible(true)
        else if (proto.getTypeName.equals("MyDate")) addDateVbox.setVisible(true)
        else addUniVbox.setVisible(true)
        enterUniTextfield.clear()
        enterIntTextfield.clear()
        datePicker.setValue(null)
    }

    @FXML def getObjectButtonClick(): Unit = {
        try {
            val num = getObjectTextfield.getText.toInt
            if (list.getValue(num) != null) showValueAlert(num)
            else accessErrorAlert()
        } catch {
            case ex: NumberFormatException =>
                inputAlert(ex.getMessage)
        }
    }

    @FXML def sortButtonClick(): Unit = {
        list = list.sort(proto.getTypeComparator)
        updateList()
    }

    @FXML def delObjectButtonClick(): Unit = {
        try {
            val num = delObjectTextfield.getText.toInt
            if (!list.remove(num)) accessErrorAlert()
            updateList()
        } catch {
            case ex: NumberFormatException =>
                inputAlert(ex.getMessage)
        }
    }

    @FXML def saveButtonClick(): Unit = {
        var filepath: String = null
        if ((filepath = chooseSaveFile) != null) {
            // save
            try {
                val oos = new ObjectOutputStream(new FileOutputStream(filepath))
                try oos.writeObject(list)
                catch {
                    case ex: Exception =>
                        System.out.println("Error: " + ex.getMessage)
                } finally if (oos != null) oos.close()
            }
        }
        else fileAlert()
    }

    @FXML def loadButtonClick(): Unit = {
        var filepath: String = null
        if ((filepath = chooseLoadFile) != null) {
            // load
            try {
                val ois = new ObjectInputStream(new FileInputStream(filepath))
                try list = ois.readObject.asInstanceOf[Nothing]
                catch {
                    case ex: Exception =>
                        System.out.println("Error: " + ex.getMessage)
                } finally if (ois != null) ois.close()
            }
            updateList()
        }
        else fileAlert()
    }

    private def inputAlert(errorMessage: String): Unit = {
        val alert = new Alert(Alert.AlertType.ERROR)
        alert.setTitle("Ошибка")
        alert.setHeaderText("Неверный ввод")
        alert.setContentText(errorMessage)
        alert.showAndWait
    }

    private def accessErrorAlert(): Unit = {
        val alert = new Alert(Alert.AlertType.ERROR)
        alert.setTitle("Ошибка")
        alert.setHeaderText("Невозможно получить доступ к объекту")
        alert.setContentText("Пожалуйста, проверьте введенные данные.")
        alert.showAndWait
    }

    private def showValueAlert(id: Int): Unit = {
        val alert = new Alert(Alert.AlertType.INFORMATION)
        alert.setTitle("Успех!")
        alert.setHeaderText("Получение объекта по номеру")
        alert.setContentText("Номер: " + id + "\nЗначение: " + list.getValue(id))
        alert.showAndWait
    }

    private def fileAlert(): Unit = {
        val alert = new Alert(Alert.AlertType.WARNING)
        alert.setTitle("Внимание!")
        alert.setHeaderText("Файл не был выбран")
        alert.setContentText("Пожалуйста, выберите файл.")
        alert.showAndWait
    }

    private def resizeWindow(x: Int): Unit = {
        val stage = parentPane.getScene.getWindow.asInstanceOf[Stage]
        stage.setWidth(x)
    }

    private def updateList(): Unit = {
        val l = new util.ArrayList[String]
        var i = 0
        while (i <= list.size) {
            if (list.getValue(i) != null) l.add(i + ")  " + list.getValue(i).toString)
            i += 1
        }
        listView.getItems.clear()
        listView.getItems.addAll(l)
    }

    private def chooseSaveFile = {
        val fileChooser = new FileChooser
        fileChooser.setTitle("Создайте файл для сохранения")
        val extFilter = new FileChooser.ExtensionFilter("List data extensions", "*." + proto.getTypeName)
        fileChooser.getExtensionFilters.add(extFilter)
        val selectedFile = fileChooser.showSaveDialog(new Stage)
        if (selectedFile != null) selectedFile.getAbsolutePath
        else null
    }

    private def chooseLoadFile = {
        val fileChooser = new FileChooser
        fileChooser.setTitle("Выберите файл для загрузки")
        val extFilter = new FileChooser.ExtensionFilter("List data extensions", "*." + proto.getTypeName)
        fileChooser.getExtensionFilters.add(extFilter)
        val selectedFile = fileChooser.showOpenDialog(new Stage)
        if (selectedFile != null) selectedFile.getAbsolutePath
        else null
    }

    @FXML private def backToMenuButtonClick(): Unit = {
        addDataPane.setVisible(false)
        resizeWindow(sizeMenu)
        menuPane.setVisible(true)
    }
}