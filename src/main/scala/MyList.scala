import java.io.Serializable

class MyList[T <: IDataType] extends Iterable[T] with Serializable {

    private var myHead: Node[T] = _
    private var myTail: Node[T] = _
    private var mySize: Int = 0

    def this(value: T) {
        this()
        myHead = new Node(value)
        myTail = myHead
        mySize = 1
    }

     def getSize: Int = mySize

    override def isEmpty: Boolean = mySize <= 0

    def pushEnd(value: T): Unit = {
        if (isEmpty) {
            myHead = new Node(value)
            myTail = myHead
        } else {
            myTail.next = new Node(value)
            myTail.next.prev = myTail
            myTail = myTail.next
        }
        mySize += 1
    }

    def pushBegin(value: T): Unit = {
        if (isEmpty) {
            myHead = new Node(value)
            myTail = myHead
        }
        else {
            myHead.prev = new Node(value)
            myHead.prev.next = myHead
            myHead = myHead.prev
        }
        mySize += 1
    }

    def insert(value: T, number: Int): Boolean = {
        val index = number - 1
        if (index > mySize || index < 0) return false
        var newNode: Node[T] = new Node(value)
        if (index == 0) pushBegin(value)
        else if (index == mySize) pushEnd(value)
        else {
            var cur = myHead
            for (i <- 0 until index - 1) {
                cur = cur.next
            }
            newNode.next = cur.next
            newNode.prev = cur
            cur.next.prev = newNode
            cur.next = newNode
            mySize += 1
        }
        true
    }

    def getValue(number: Int): Option[T] = {
        val index = number - 1
        if (!isEmpty && index < mySize && index >= 0) {
            var cur = myHead
            var i = 0
            while ( {
                i < index
            }) {
                i += 1
                cur = cur.next
            }
            Some(cur.value)
        }
        else None
    }

    def remove(number: Int): Boolean = {
        val index = number - 1
        if (index < 0 || index >= mySize) return false
        if (index == 0 && (mySize == 1)) {
            myHead = null
            myTail = null
        }
        else if (index == 0) {
            myHead = myHead.next
            myHead.prev = null
        }
        else if (index == mySize - 1) {
            myTail = myTail.prev
            myTail.next = null
        }
        else {
            var cur = myHead
            for (i <- 0 until index - 1) {
                cur = cur.next
            }
            cur.next = cur.next.next
            cur.next.prev = cur
        }
        mySize -= 1
        true
    }

    // ДРОПНУТЬ!!!
    def print(): Unit = {
        for (value <- this) {
            System.out.println(value.toString)
        }
    }

//    def sort(comparator: MyComparator): MyList[T] = {
//        def insertSorted(list: MyList[T], value: T): MyList[T] = {
//            if (list.isEmpty) list.pushEnd(value)
//            else if (comparator.compare(value, list.myTail.value) >= 0) list.pushEnd(value)
//            else if (comparator.compare(value, list.myHead.value) <= 0) list.pushBegin(value)
//            else {
//                def insertAtPosition(cur: Node[T], k: Int): MyList[T] = {
//                    if (k == 0) list.insert(value, k)
//                    else insertAtPosition(cur.next, k - 1)
//                }
//
//                insertAtPosition(list.head, 1)
//            }
//        }
//
//        foldLeft(MyList.this) { (acc, value) =>
//            insertSorted(acc, value)
//        }
//    }

    def iterator: Iterator[T] = new MyListIterator

    private class Node[U <: IDataType](var value: U, var next: Node[U], var prev: Node[U]) extends Serializable {
        def this(value: U) {
            this(value, null, null)
        }
    }

    private class MyListIterator(private var curr: Node[T] = myHead) extends Iterator[T] {

        def hasNext: Boolean = curr != null

        def next: T = {
            if (!hasNext) throw new NoSuchElementException
            val value = curr.value // значение в текущем узле
            curr = curr.next // следующий узел
            value
        }

        def remove(): Unit = {
            throw new UnsupportedOperationException
        }
    }
}

