trait IDataType extends Serializable {

    def getTypeName: String

    def create: IDataType

    @throws[Exception]
    def parseValue(ss: String): IDataType

    def getTypeComparator: MyComparator
}