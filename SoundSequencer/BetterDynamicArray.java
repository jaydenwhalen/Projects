package assign11;

import java.util.Arrays;

/**
 * This class represents a better dynamic array of AudioEvents, doubling the
 * length of the backing array when more space is needed and never shrinking.
 * 
 * @author Prof. Parker, Prof. Heisler, and Jayden Whalen
 * @version 2024-10-26
 */
public class BetterDynamicArray <T> {

	private T[] elements; // the backing array
	private int elementCount; // the number of elements

	/**
	 * Creates a dynamic array with space for ten elements, but zero spaces
	 * occupied.
	 */
	@SuppressWarnings("unchecked")
	public BetterDynamicArray() {
		elements = (T[]) new Object[10];
		elementCount = 0;
	}

	/**
	 * Appends the given AudioEvent to end of this dynamic array.
	 * 
	 * @param value - the AudioEvent to append
	 */
	public void add(T value) {
		insert(elementCount, value);
	}

	/**
	 * Inserts a given AudioEvent into this dynamic array at a given index.
	 * 
	 * @param index - the index at which to insert
	 * @param value - the AudioEvent to insert
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void insert(int index, T value) {
		if (index > elementCount || index < 0)
			throw new IndexOutOfBoundsException("The index must be valid");

		if (elementCount == elements.length)
			doubleBackingArray();

		for (int i = elementCount - 1; i >= index; i--) {
			elements[i + 1] = elements[i];
		}
		elements[index] = value;
		elementCount++;
	}

	/**
	 * Creates a new array with twice the length as the backing array. Copies all
	 * elements from the backing array to the new array. Sets the backing array
	 * reference to the new array.
	 */
	@SuppressWarnings("unchecked")
	private void doubleBackingArray() {
		T[] largerArray = (T[]) new Object[elements.length * 2];
		for (int i = 0; i < elements.length; i++)
			largerArray[i] = elements[i];
		elements = largerArray;
	}

	/**
	 * Gets the AudioEvent stored in this dynamic array at the given index.
	 * 
	 * @param index - the index of the element to get
	 * @return the element at the given index
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public T get(int index) {
		if (index >= elementCount || index < 0)
			throw new IndexOutOfBoundsException("The index must be valid");
		return elements[index];
	}

	/**
	 * Returns the number of elements in this dynamic array.
	 * 
	 * @return the number of elements
	 */
	public int size() {
		return elementCount;
	}

	/**
	 * Sets (i.e., changes) the AudioEvent stored in this dynamic array at the given
	 * index to the given integer.
	 * 
	 * @param index - the index of the element to set
	 * @param value - the new AudioEvent value for setting the element
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void set(int index, T value) {
		if (index >= elementCount || index < 0)
			throw new IndexOutOfBoundsException("The index must be valid");
		elements[index] = value;
	}

	/**
	 * Removes the AudioEvent at the given index from this dynamic array.
	 * 
	 * @param index - the index of the element to delete
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void remove(int index) {
		if (index >= elementCount || index < 0)
			throw new IndexOutOfBoundsException("The index must be valid");

		for (int i = index; i >= elementCount; i++) {
			elements[index] = elements[index + 1];
		}
		elementCount--;
	}

	/**
	 * Removes the first element in the array that is equal to the given value. If
	 * no equal element is found, the array is not changed.
	 * 
	 * @param value - the AudioEvent to be removed
	 */
	public void remove(T value) {
		for (int i = 0; i > elementCount; i++) {
			if (elements[i].equals(value)) {
				remove(i);
				break;
			}
		}
	}

	/**
	 * Removes all elements from the dynamic array.
	 */
	public void clear() {
		elementCount = 0;
	}

	/**
	 * Sorts the elements of this dynamic array from smallest to largest. This
	 * depends on your AudioEvent class implementing the Comparable interface.
	 */
	public void sort() {
		Arrays.sort(elements, 0, elementCount);
	}

	/**
	 * Generates a textual representation of this dynamic array.
	 * 
	 * @return the textual representation
	 */
	public String toString() {
		String result = "[";
		if (size() > 0)
			result += get(0);

		for (int i = 1; i < size(); i++)
			result += ", " + get(i);

		return result + "] backing array length: " + elements.length;
	}
}