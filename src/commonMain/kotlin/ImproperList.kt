class ImproperList<E>(vararg elems: E): List<E> {

    private val list = listOf(*elems)

    override val size: Int = list.size

    override fun contains(element: @UnsafeVariance E) = list.contains(element)

    override fun containsAll(elements: Collection<@UnsafeVariance E>) = list.containsAll(elements)

    override fun get(index: Int) = list[index]

    override fun indexOf(element: @UnsafeVariance E) = list.indexOf(element)

    override fun isEmpty() = list.isEmpty()

    override fun iterator() = list.iterator()

    override fun lastIndexOf(element: @UnsafeVariance E) = list.lastIndexOf(element)

    override fun listIterator() = list.listIterator()

    override fun listIterator(index: Int) = list.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)
}
