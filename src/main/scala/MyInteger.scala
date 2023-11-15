import java.util.Scanner

class MyInteger extends IDataType {

    private var integer: Integer = 0

    def this(integer: Integer) {

        this()
        this.integer = integer

    }


    def getTypeName: String = this.getClass.getSimpleName

    def create = new MyInteger

    override def clone = new MyInteger(integer)

    @throws[Exception]
    def parseValue(ss: String): MyInteger = {
        val scanner = new Scanner(ss)
        try new MyInteger(scanner.nextInt)
        catch {
            case e: Exception =>
                throw new Exception(e.getMessage)
        }
    }

    def getTypeComparator: MyComparator = new MyComparator() {
        override def compare(o1: Any, o2: Any): Int = return o1.asInstanceOf[MyInteger].integer - o2.asInstanceOf[MyInteger].integer
    }

    override def toString: String = integer.toString

    def setInteger(integer: Integer): Unit = {
        this.integer = integer
    }

    def getInteger: Integer = integer

}
