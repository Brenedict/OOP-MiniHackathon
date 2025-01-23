import  java.util.*;

public class YlananPrimeFactorization {

    /*
     * Ylanan, Isaac Emmanuel F.
     * BSCS 2-2
     * OOP Hands On Page 1
     * */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int positiveInteger;

        // the purpose of this while loop is to loop until a positive integer is entered.
        while(true)
        {
            System.out.print("Enter positive integer: ");
            positiveInteger = scanner.nextInt();

            if(positiveInteger > 1){
                break; // stop the loop is the int is positive
            } else {
                //prompts the user to try again after inputting negative integer
                System.out.println("Please enter a positive integer. Try again.");
            }
        }

        System.out.println("The prime factorization of " + positiveInteger + " is: ");

        boolean firstFactorChecker = true; // this boolean is to track if the first factor is being printed

       // iterate over potential factors starting from 2 up to sqrt of postiveInteger
        for( int div = 2; div * div <= positiveInteger; div++) {
            // divide the integer by 2 as much as possible
            while(positiveInteger % div == 0)
            {
                if(!firstFactorChecker)
                {
                    System.out.print(" x ");// this will only print x if it's not the first factor (last)
              }
                //print the current factor
                System.out.print(div);
                //divide the number by the current divisor
                positiveInteger = positiveInteger / div;
                //this set the checker to false after the first factor is printed
                firstFactorChecker = false;

            }

        }
        //this is for checking if the remaining number is greater than 1
        // if this is true, it means this number is a prime factor
        if(positiveInteger != 1)
        {
            if(!firstFactorChecker)
            {
                System.out.print(" x "); // only prints x if its not the first factor
            }
            //prints the remaining prime factor
            System.out.print(positiveInteger);
        }
    }
}
