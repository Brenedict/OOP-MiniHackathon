import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class YlananPalindromeAndAnagramUI {

    /*
     * Ylanan, Isaac Emmanuel F.
     * BSCS 2-2
     * OOP Hands On Page 1
     * */

    public static void main(String[] args) {
    // THis will create a JFrame for our UI
    JFrame frame = new JFrame("Palindrome and Anagram UI");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // operation to exit the program
    frame.setSize(400, 450); // this set the size of our frame
    frame.setLocationRelativeTo(null);  // center the frame on the screen
    frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10,20 )); // this sets the frame to FlowLayout, with center alignment and gaps


        //Create the components of the UI
        JLabel palindrome = new JLabel("Enter a string to check if it's palindrome: ");
        JTextField palindromeField = new JTextField();
        JButton palindromeButton = new JButton("Check palindrome");

        JLabel anagram = new JLabel("Enter a string to compare for anagram");
        JTextField anagramField = new JTextField();
        JTextField anagramFieldTwo = new JTextField();
        JButton anagramButton = new JButton("Check anagram");

        JButton resetButton = new JButton("Reset");

        //set the size of text field and buttons
        palindromeField.setPreferredSize(new Dimension(200, 30));
        anagramField.setPreferredSize(new Dimension(200, 30));
        anagramFieldTwo.setPreferredSize(new Dimension(200, 30));
        palindromeButton.setPreferredSize(new Dimension(200, 30));
        anagramButton.setPreferredSize(new Dimension(200, 30));
        resetButton.setPreferredSize(new Dimension(200, 30));

        // this will add the components to the frame
        frame.add(palindrome);
        frame.add(palindromeField);
        frame.add(palindromeButton);
        frame.add(anagram);
        frame.add(anagramField);
        frame.add(anagramFieldTwo);
        frame.add(anagramButton);
        frame.add(resetButton);



        // action listener to the palindrome buttons
        palindromeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //this will get the input, remove the spaces, and convert to lowercase for safe comparison when reversed
                String input = palindromeField.getText().replaceAll("\\s", "").toLowerCase();
                //Reverse the inputted string
                String reversed = new StringBuilder(input).reverse().toString();
                // check if the input is palindrome and I use show message dialog to show the results
                if(input.equals(reversed)) {
                    JOptionPane.showMessageDialog(frame, "It's a palindrome!");
                } else {
                    JOptionPane.showMessageDialog(frame, "It's not a palindrome!");
                }
            }
        });
        // add action listener to the anagram button
        anagramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // this will get the input1 and input2 then remove spaces, and convert to lower case for safe comparison
                String input1 = anagramField.getText().replaceAll("\\s", "").toLowerCase();
                String input2 = anagramFieldTwo.getText().replaceAll("\\s", "").toLowerCase();

                // check if the strings are anagram
                if (AnagramChecker(input1, input2)) {
                    JOptionPane.showMessageDialog(frame, "It's an anagram!");
                } else {
                    JOptionPane.showMessageDialog(frame, "It's not an anagram!");

                }
            }
        });

        // this is the action listener of reset button, this will clear all the text field.
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //use setText(""); to empty the textFields
                palindromeField.setText("");
                anagramField.setText("");
                anagramFieldTwo.setText("");

            }
        });

        // makes the frame visible
        frame.setVisible(true);
    }
    public static boolean AnagramChecker(String str1, String str2) {
        if (str1.length() != str2.length()){ // checks first if the lengths are different, if they are different they are not anagrams
            return false;
        }
        // this convert the string to array of char
        char[] arr1 = str1.toCharArray();
        char[] arr2 = str2.toCharArray();

        // sorts the array
        java.util.Arrays.sort(arr1);
        java.util.Arrays.sort(arr2);

        // check if the sorted arrays are equal
        return java.util.Arrays.equals(arr1, arr2);
    }

}
