import java.util.*;

/**
 * @author William Wallace
 * @date 22/05/2020
 * 
 * The Arithmetic class reads input and outputs an equation that gives how the
 * target number can be reached using addition and multiplication, if it is
 * possible. N and L give two possible ways to reach the target. N achieves this
 * using the standard sequence of operations and L achieves this by going from left
 * to right over the equation.
 */
public class ArithmeticFinal{

  /* Keeps running the program while there are lines in the input file. */
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      readLines(sc);
    }
  }

  private static ArrayList<String> input = new ArrayList<String>();
  private static ArrayList<String> targetSeq = new ArrayList<String>();

  /*
   * readLines adds the lines from the input file and adds then to the ArrayList
   * called input, appends the targetSeq to line, and processes the line.
   */
  private static void readLines(Scanner sc) {
    String line = sc.nextLine();
    input.add(line);
    line = sc.nextLine();
    targetSeq.add(line);
    prepInput();
  }

  private static ArrayList<Integer> operands = new ArrayList<Integer>(); 
  private static ArrayList<Integer> tempOperands = new ArrayList<Integer>(); 

  /*
   * prepInput ensures input is in correct format before performing it's computation.
   * It reads the input lines, converts the operands into type Integer before adding
   * them to the ArrayLists respectively called "operands" and "tempOperands". 
   */
  private static void prepInput() {
    String operandLine = input.get(0);
    String[] operandStrings = operandLine.split("\\s+");
    for (int i = 0; i < operandStrings.length; i++) {
      String operandStr = operandStrings[i];
      operands.add(Integer.parseInt(operandStr));
      tempOperands.add(Integer.parseInt(operandStr));
    }

    String targetSeqStr = targetSeq.get(0);
    String[] targetSeqStrings = targetSeqStr.split("\\s+");
    int target = Integer.parseInt(targetSeqStrings[0]);
    char sequence = targetSeqStrings[1].charAt(0);

    compute(target, sequence, operands, tempOperands);

    input.clear();
    targetSeq.clear();
    operands.clear();
    tempOperands.clear();
  }

  /* 
  * The compute method calculates the target based on the which mode was selected in input.
  * Handles cases where there is only one operand specified, and computes all if >1 operands.
  */
  public static void compute(int target, char sequence, ArrayList<Integer> operands, ArrayList<Integer> tempOperands) {
    int numOperators = 0;

    switch (sequence) {
      case 'L':
        numOperators = operands.size() - 1;
        int n = numOperators;
        if (operands.size() == 1) {
          System.out.println("L " + operands.get(0));
          return;
        }
        computeL(n, target, operands, tempOperands);
        break;
      case 'N':
        for (int o : operands) {
          numOperators++;
        }
        numOperators--;
        if (operands.size() == 1) {
          System.out.println("N " + operands.get(0));
          return;
        }
        int z = numOperators;
        computeN(numOperators, target, operands, tempOperands);
        break;
    }
  }

  /**
   * The computeL method performs the operations on the operands and evaluates
   * whether the target number can be reached or not, given the input, by applying
   * the operations from left to right.
   * 
   * @param n = number of operators in the line
   * @param signCombos = array of combinations of +'s and *'s 
   * @param binChars = array of elements from bin
   */
  public static void computeL(int n, int target, ArrayList<Integer> operands, ArrayList<Integer> tempOperands) {
    int sum = 0;
    int numOperatorsL = 0;
    boolean firstRun = true;

    /*
     * Combinations of operators to reach target are found using 0 to represent +, 1 to represent *, 
     * padding each of the numbers from 0 to the number of operands, and seeing if the calculation hits target. 
     */
    for (int i = 0; i < Math.pow(2, n); i++) {
      String bin = Integer.toBinaryString(i);
      while (bin.length() < n) {
        bin = "0" + bin;                                        // pads all binary operands from 0 - n, with zeros
      }
      char[] binChars = bin.toCharArray();                         // binChars = {00000...n, 01, 010, 011, ... until n^2}
      char[] signCombos = new char[n];                           // signCombos = {"", "", "", ... } for size n
      for (int j = 0; j < binChars.length; j++) {
        signCombos[j] = binChars[j] == '0' ? '+' : '*';             
      }

      for (char item : signCombos) {
        numOperatorsL++;                                        
        if (firstRun) {
          sum = operands.get(0);                              
          operands.remove(0);                                   
        }

        // Apply operations to operands and keep total in sum
        if (item == '+') {
          sum += operands.get(0);
        } else if (item == '*') {
          sum *= operands.get(0);
        }

        operands.remove(0);
        firstRun = false;

        /* If target number reached by applying operations from left to right, print "L
         * <target number> <equation>". Also handles for when ther are as many operators 
         * as operands - in which case dont print the last operator. 
         */
        if (sum == target && numOperatorsL == n) { 
          int numSigns = 0;
          System.out.print("L " +target+ " ");
          for (int l = 0; l < tempOperands.size(); l++) {           
            System.out.print(tempOperands.get(l) + " ");            
            if (numSigns == n) {                          
              break;                                                
            }
            System.out.print(signCombos[numSigns] + " "); 
            numSigns++;
          }
          System.out.print("\n");
          return; 
        }

        // If target not reached, print "L impossible"
        if (sum != target && numOperatorsL == n) { 
          firstRun = true;
          sum = 0;
          numOperatorsL = 0;
          for (int t = 0; t < tempOperands.size(); t++) {
            operands.add(t, tempOperands.get(t));
          }
        }
      }
    }
    System.out.println("L " +target+ " impossible");
  }

  /**
   * The computeN method performs the operations on the operands and evaluates
   * whether the target number can be reached or not, given the input, by applying
   * the normal sequence of operations.
   * 
   * @param n = number of operators in the line
   * @param signs is an ArrayList holding all of the possile combinations of operators for the read line. 
   * @param tempSigns is the Arraylist for storing and outputting the operators. 
   */
  public static void computeN(int index, int target, ArrayList<Integer> operands, ArrayList<Integer> tempOperands) {

    int sum = 0;
    boolean firstRun = true;
    ArrayList<Character> operators = new ArrayList<Character>();
    operators.add('+');
    operators.add('*');

    /*
     * Combinations of operators to reach target are found using 0 to represent +, 1 to represent *, 
     * padding each of the numbers from 0 to the number of operands, and seeing if the calculation hits target. 
     */
    for (int i = 0; i < Math.pow(2, index); i++) {
      List<Character> signList = new ArrayList<Character>();
      String bin = Integer.toBinaryString(i);
      while (bin.length() < index) {
        bin = "0" + bin;
      }
      char[] binChars = bin.toCharArray();
      for (char c : binChars) {
        signList.add(c);
      }

      ArrayList<Character> signs = new ArrayList<Character>();
      ArrayList<Character> tempSigns = new ArrayList<Character>(); 

      // Adds * or + signs to ArrayLists based on the what the binary digits in 
      // signList correspond to (0 = +, 1 = *).
      for (int j = 0; j < signList.size(); j++) {               
        if (signList.get(j) == '0') {
          signs.add(j, operators.get(0));                      
          tempSigns.add(j, operators.get(0));
        } else if (signList.get(j) == '1') {
          signs.add(j, operators.get(1));
          tempSigns.add(j, operators.get(1));
        }
      }

      // Removes operands and operators as the target is calculated, 
      // and replaces the number for multiplcation with the running total 
      for (int j = 0; j < signs.size(); j++) {
        if (signs.get(j) == '*') { 
          int multiplySum = operands.get(j) * operands.get(j + 1);
          operands.remove(j);
          operands.remove(j); 
          operands.add(j, multiplySum);
          signs.remove(j);
          j -= 1; 
        }
      }
      sum = 0; 
      for (Integer n : operands) {
        sum += n;
      }

      // If target number reached by applying operations from left to right, 
      // print "N <target number> <equation>"
      if (sum == target) {
        int numSigns = 0;
        System.out.print("N " +target+ " ");
        for (int l = 0; l < tempOperands.size(); l++) {
          System.out.print(tempOperands.get(l) + " ");
          if (numSigns == index) {
            break;
          }
          System.out.print(tempSigns.get(numSigns) + " ");
          numSigns++;
        }
        System.out.print("\n");
        return;
      }

      // If target not reached, print "N impossible"
      if (sum != target) {
        operands.clear();
        for (int t = 0; t < tempOperands.size(); t++) {
          operands.add(t, tempOperands.get(t));
        }
      }
    }
    System.out.println("N " +target+ " impossible");
  }
}