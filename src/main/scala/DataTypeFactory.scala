import scala.collection.mutable.ArrayBuffer

class DataTypeFactory {
    private val types: Array[String] = Array("MyInteger", "MyDate")
    private val dataTypes: ArrayBuffer[String] = ArrayBuffer(types: _*)

    def getDataTypes: ArrayBuffer[String] = dataTypes

    def createDataType(className: String): IDataType = {
        val tokens = className.split("\\.")

        dataTypes.find(dataType => dataType == tokens.last) match {
            case Some(dataType) =>
                Class.forName(className).getDeclaredConstructor().newInstance().asInstanceOf[IDataType]
            case None =>
                throw new RuntimeException(s"Class $className not found in dataTypes")
        }
    }
}