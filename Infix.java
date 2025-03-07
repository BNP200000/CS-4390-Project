import java.util.*;

/**
 * Infix.java
 * 
 * Java class to perform infix evaluation using
 * an algorithm similar to the Shunting Yard
 * algorithm.
 */
public class Infix {
    static Stack<Double> operand; // Stack of operands
    static Stack<Character> operator; // Stack of operators

    /**
     * Infix()
     * 
     * Constructor for the Infix class
     */
    public Infix() {
        operand = new Stack<Double>();
        operator = new Stack<Character>();
    }

    /**
     * evaluate(String expr)
     * 
     * @param expr Mathematical expression
     * @return The calculated value of expr
     */
    double evaluate(String expr) {
        String[] split = expr.split(" ");
        for(int i = 0; i < split.length; i++) {
            char c = split[i].charAt(0);
            if(Character.isDigit(c)) {
                double x = Double.parseDouble(split[i]);
                operand.push(x);
            } else if(c == '(') {
                operator.push(c);
            } else if(c == ')') {
                while(operator.peek() != '(') {
                    operate();
                }
                operator.pop();
            } else if(isOperator(c)) {
                while(!operator.isEmpty() && precedence(c) <= precedence(operator.peek())) {
                    operate();
                }
                operator.push(c);
            }
        }

        while(!operator.isEmpty()) {
            operate();
        }

        return operand.pop();
    }

    /**
     * isOperator(char c)
     * 
     * @param c Operator character
     * @return True if c is a mathematical operator; otherwise, False
     */
    boolean isOperator(char c) {
        return c == '*' || c == '/' || c == '+' || c == '-' || c == '^';
    }

    /**
     * precedence(char c)
     * 
     * @param c Operator character
     * @return The precedence of c based on PEMDAS
     */
    int precedence(char c) {
        int prec = -1;

        if(c == '^') {
            prec = 3;
        } else if(c == '*' || c == '/') {
            prec = 2;
        } else if(c == '+' || c == '-') {
            prec = 1;
        }

        return prec;
    }

    /**
     * operate()
     * 
     * Perform math operation on the two most recent
     * item in the operand stack
     */
    void operate() {
        double res = -1;
        double a = operand.pop();
        double b = operand.pop();
        char op = operator.pop();

        switch(op) { 
            case '+':
                res = (double)Math.round((a + b) * 10) / 10;
                break;
            case '-':
                res = (double)Math.round((b - a) * 10) / 10;
                break;
            case '*':
                res = (double)Math.round((a * b) * 10) / 10;
                break;
            case '/':
                if(a == 0) {
                    throw new ArithmeticException("Cannot divide by 0!");
                }
                res = (double)Math.round((b / a) * 10) / 10;
                break;
            case '^':
                res = (double)Math.round(Math.pow(b, a) * 10) / 10;
                break;
        }
        operand.push(res);
    }
}
