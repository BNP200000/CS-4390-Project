import java.util.*;

/**
 * Infix.java
 * 
 * Java class to perform infix evaluation using
 * an algorithm similar to the Shunting Yard
 * algorithm.
 */
public class Infix {
    Stack<Double> operand; // Stack of operands
    Stack<Character> operator; // Stack of operators

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
     * @param expr The infix expression
     * @return The evaluated result of the expression
     */
    double evaluate(String expr) {
        List<String> tokens = parseExpression(expr);
        for(String token : tokens) {
            if(isOperand(token)) {
                operand.push(Double.parseDouble(token));
            } else if(token.equals("(")) {
                operator.push('(');
            } else if(token.equals(")")) {
                while(!operator.isEmpty() && operator.peek() != '(') {
                    operate();
                }
                operator.pop();
            } else if(isOperator(token.charAt(0))) {
                while(!operator.isEmpty() && precedence(token.charAt(0)) <= precedence(operator.peek())) {
                    operate();
                }
                operator.push(token.charAt(0));
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
     * isOperand(String op)
     * 
     * @param op Operand
     * @return True if op is a valid operand
     */
    boolean isOperand(String op) {
        return op.matches("^[+-]?([0-9]+([.][0-9]+)?|[.][0-9]+)$");
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
                res = a + b;
                break;
            case '-':
                res = b - a;
                break;
            case '*':
                res = a * b;
                break;
            case '/':
                if(a == 0) {
                    throw new ArithmeticException("Cannot divide by 0!");
                }
                res = b / a;
                break;
            case '^':
                res = Math.pow(b, a);
                break;
        }
        operand.push(res);
    }

    /**
     * parseExpression(String expr)
     * 
     * @param expr Infix expression
     * @return Tokenized form of the expression
     */
    List<String> parseExpression(String expr) {
        // Format the expression to be "mathematical"
        expr = expr
                .replaceAll("\\s+", "")
                .replace("[", "(")
                .replace("]", ")")
                .replace("+-", "-")
                .replace("-+", "-")
                .replace("--", "+")
                .replace("**", "^")
                .replace("//", "/")
                .replace(")(", ")*(");

        if(expr.startsWith("-(")) {
            expr = "0" + expr;
        }

        // Keep track of the no. of operators, operands, (, and )
        int numOperators = 0, numOperands = 0, numOpenParen = 0, numClosedParen = 0;
        
        // Build the tokens from the expression
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while(i < expr.length()) {
            char curr = expr.charAt(i);
            
            if(isOperator(curr)) {
                numOperators++;
                tokens.add(String.valueOf(curr));
            } else if(curr == '(') {
                numOpenParen++;
                if(!tokens.isEmpty() && isOperand(tokens.get(tokens.size() - 1))) {
                    numOperators++;
                    tokens.add("*");
                }
                tokens.add(String.valueOf(curr));
            } else if(curr == ')') {
                numClosedParen++;
                tokens.add(String.valueOf(curr));
            } else {
                numOperands++;
                StringBuilder currOperand = new StringBuilder();
                currOperand.append(curr);

                while(i + 1 < expr.length() && !isOperator(expr.charAt(i + 1)) && expr.charAt(i + 1) != '(' && expr.charAt(i + 1) != ')') {
                    currOperand.append(expr.charAt(i + 1));
                    i++;
                }

                // Handle negative numbers
                if(tokens.size() == 1 && tokens.get(0).equals("-")) {
                    numOperators--;
                    tokens.set(0, "-"+currOperand);
                } else if(tokens.size() >= 2 && (isOperator(tokens.get(tokens.size() - 2).charAt(0)) || tokens.get(tokens.size() - 2).equals("(")) && tokens.get(tokens.size() - 1).equals("-")) {
                    numOperators--;
                    tokens.set(tokens.size() - 1, "-"+currOperand);
                } else {
                    // Handles expression matching A(B) or (A)B
                    if(!tokens.isEmpty() && tokens.get(tokens.size() - 1).equals(")")) {
                        tokens.add("*");
                        numOperators++;
                    }
                    tokens.add(currOperand.toString());
                }
            }

            i++;
        }

        if(numOperators == 0) {
            System.err.println("No arithmetic operators detected!");
            System.exit(-1);
        } 

        if(numOperators >= numOperands) {
            System.err.println("Operator count >= Operand count!");
            System.exit(-1);
        }
        
        if(numOpenParen != numClosedParen) {
            System.err.println("Unbalanced expression!");
            System.exit(-1);
        }

        return tokens;
    }
}
