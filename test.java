public class test {
    private int[][] array;

    private test(int[][] array) {
        this.array = array;
    }

    public static test createEmpty() {
        return new test(new int[0][0]);
    }

    public void setArray(int[][] newArray) {
        // Perform deep copy of the input array
        int[][] copiedArray = new int[newArray.length][];
        for (int i = 0; i < newArray.length; i++) {
            copiedArray[i] = new int[newArray[i].length];
            System.arraycopy(newArray[i], 0, copiedArray[i], 0, newArray[i].length);
        }
        this.array = copiedArray;
    }

    public int[][] getArray() {
        // Perform deep copy before returning the array
        int[][] copiedArray = new int[array.length][];
        for (int i = 0; i < array.length; i++) {
            copiedArray[i] = new int[array[i].length];
            System.arraycopy(array[i], 0, copiedArray[i], 0, array[i].length);
        }
        return copiedArray;
    }

    // You can add more methods as needed

    public static void main(String[] args) {
        test container = test.createEmpty();

        // Display empty array
        System.out.println("Empty Array:");
        displayArray(container.getArray());

        // Replace array with new values
        int[][] newArray = {{9, 8, 7}, {6, 5, 4}, {3, 2, 1}};
        container.setArray(newArray);

        // Display new array
        System.out.println("\nNew Array:");
        displayArray(container.getArray());
    }

    private static void displayArray(int[][] array) {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
