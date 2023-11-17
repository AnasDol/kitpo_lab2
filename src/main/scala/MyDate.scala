import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

class MyDate extends IDataType {

    private var day = 0
    private var month = 0
    private var year = 0

    def this(day: Int, month: Int, year: Int) {
        this()
        this.day = day
        this.month = month
        this.year = year
    }

    def getTypeName: String = this.getClass.getSimpleName

    def create = new MyDate

    override def clone = new MyDate(day, month, year)

    @throws[Exception]
    def parseValue(ss: String): MyDate = {
        if (!isValidDate(ss)) throw new Exception("Wrong input. Date format: dd.mm.yyyy")
        val array = ss.split("\\.")
        new MyDate(Integer.parseInt(array(0)), Integer.parseInt(array(1)), Integer.parseInt(array(2)))
    }

    override def getTypeComparator: MyComparator = new MyComparator {
        override def compare(o1: Any, o2: Any): Int = {
            val dd = o1.asInstanceOf[MyDate].day - o2.asInstanceOf[MyDate].day
            val dm = o1.asInstanceOf[MyDate].month - o2.asInstanceOf[MyDate].month
            val dy = o1.asInstanceOf[MyDate].year - o2.asInstanceOf[MyDate].year
            if (dy != 0) dy else if (dm != 0) dm else dd
        }
    }


    override def toString: String = f"${day}%02d.${month}%02d.${year}%04d"

    private def isValidDate(inputDate: String): Boolean = {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        try {
            // Пытаемся распарсить дату из строки
            val date = LocalDate.parse(inputDate, dateFormatter)

            // Проверяем, соответствует ли распарсенная дата исходной строке
            // (для обработки ввода 31 февраля, например)
            date.format(dateFormatter) == inputDate
        } catch {
            case _: DateTimeParseException =>
                // Ошибка парсинга или несовпадение формата
                false
        }
    }


}