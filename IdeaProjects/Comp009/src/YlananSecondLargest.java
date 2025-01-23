import java.util.*;

public class YlananSecondLargest {
    /*
    * Ylanan, Isaac Emmanuel F.
    * BSCS 2-2
    * OOP Hands On Page 1
    * */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the size of array: ");
        int sizeOfArray = scanner.nextInt();

        // check if the entered array size is less than 2, if it's less than 2 exit the program
        if(sizeOfArray < 2) {
            System.out.println("Entered size must be at least 2!");
            return; // exit the program because the size is invalid
        }

        System.out.println("Please enter integers " + sizeOfArray  + " times.");

        int largestNumber = Integer.MIN_VALUE; //This set to the smallest possible integer value
        int secondLargestNumber = Integer.MIN_VALUE; //This set to the smallest possible integer value

        //loop until the input numbers is based on the array size
        for(int i = 0; i < sizeOfArray ; i++) {
            int elements = scanner.nextInt();

            //check if the current number is greater than the largest number
            if(elements > largestNumber) {
                secondLargestNumber = largestNumber; // update second largest to be the previous largest number
                largestNumber = elements; // update largest to the current number
                // Check if the current number is greater than the second largest and is not equal to the largest
            } else if (elements > secondLargestNumber && elements != largestNumber){
                secondLargestNumber = elements; // update the second largest number
            }

        }
        // This checks is a valid second largest number was found
        if(secondLargestNumber == Integer.MIN_VALUE) {
            System.out.println("There is no second largest number found.");
        } else {
            System.out.println("The second largest number is: " + secondLargestNumber);
        }
    }

}
