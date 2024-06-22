import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringCalculatorGUI {

    public static void main(String[] args) {
        // Créer la fenêtre 
        JFrame frame = new JFrame("Bienvenue dans la calculatrice de chaînes : ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Créer le panneau
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Afficher le cadre pour que ca soit visible
        frame.setVisible(true);
    }
    

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Demandez l'expression de l'usager
        JLabel expressionLabel = new JLabel("Entrez une expression:");
        expressionLabel.setBounds(10, 20, 160, 25);
        panel.add(expressionLabel);

        
        JTextField expressionText = new JTextField(20);
        expressionText.setBounds(180, 20, 165, 25);
        panel.add(expressionText);

        // Le button pour faire  l'evaluation de l'expression
        JButton evaluateButton = new JButton("Évaluez");
        evaluateButton.setBounds(10, 60, 150, 25);
        panel.add(evaluateButton);

        // Montrer les resultats
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setBounds(10, 100, 80, 25);
        panel.add(resultLabel);

       
        JLabel resultValueLabel = new JLabel("");
        resultValueLabel.setBounds(100, 100, 250, 25);
        panel.add(resultValueLabel);

        // les resultats pour des cas de test 
        JTextArea testCaseArea = new JTextArea();
        testCaseArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(testCaseArea);
        scrollPane.setBounds(10, 140, 560, 200);
        panel.add(scrollPane);

        // Montrer les tests
        displayTestCases(testCaseArea);

        
       
        evaluateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Trim whitespace             
            	// quitter le programme si l'entrée est nulle
                String expression = expressionText.getText().trim(); 
                if (expression.isEmpty()) {
                    resultValueLabel.setText("");
                    return; 
                }
                
                try {
                    String result = evaluate(expression);
                    resultValueLabel.setText(result);
                } catch (ArithmeticException ex) {
                    resultValueLabel.setText("Erreur : Division par zéro");
                } 
                
                catch (Exception ex) {
                    resultValueLabel.setText("Erreur: " + ex.getMessage());
                }
            }
        });
    }

    
    // Ignorer s'il y a des "+" extra 
    public static String evaluate(String expression) {
        if (expression.startsWith("+")) {
            expression = expression.substring(1);
        }

        // Utiliser tokenization pour arranger les espaces puis d'autres characters.
        List<String> tokens = tokenize(expression);
        
        // Analyser les jetons et calculer le résultat
        double result = parseExpression(tokens);

        // Vérifiez si le résultat doit être un entier
        if (result == (int) result) {
            return String.valueOf((int) result);
        }
        
        else {
            return String.valueOf(result);
        }
    }

    
    
    // Convertit la chaîne d'entrée en une liste de jetons
    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        // Tokenize the input string 
        StringTokenizer st = new StringTokenizer(expression, "()+-*/^\" ", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim(); // Couper les espaces du jeton
            
            if (!token.isEmpty() && !token.equals("\"")) {
                tokens.add(token); // Ajoutez des jetons non vides à la liste, à l'exclusion "
            }
        }
        return tokens;
    }

    // évalue l'expression, en commençant par l'addition et la soustraction
    private static double parseExpression(List<String> tokens) {
        return parseAddSubtract(tokens);
    }

    //Analyse et évalue l'addition et la soustraction
    private static double parseAddSubtract(List<String> tokens) {
        double value = parseMultiplyDivide(tokens);
        while (!tokens.isEmpty()) {
            String op = tokens.get(0);
            if (!op.equals("+") && !op.equals("-")) {
                break;
            }
            tokens.remove(0);
            double nextValue = parseMultiplyDivide(tokens);
            if (op.equals("+")) {
                value += nextValue;
            } else {
                value -= nextValue;
            }
        }
        return value;
    }

    // évalue la multiplication et la division
    private static double parseMultiplyDivide(List<String> tokens) {
        double value = parseExponent(tokens);
        while (!tokens.isEmpty()) {
            String op = tokens.get(0);
            
            if (!op.equals("*") && !op.equals("/")) {
                break;
            }
            tokens.remove(0);
            double nextValue = parseExponent(tokens);
            if (op.equals("*")) {
                value *= nextValue;
            } else {
                if (nextValue == 0) {
                    throw new ArithmeticException("Division par zéro");
                }
                value /= nextValue;
            }
        }
        return value;
    }

    // Analyse et évalue l'exponentiation
    private static double parseExponent(List<String> tokens) {
        double value = parseFactor(tokens);
        while (!tokens.isEmpty()) {
            String op = tokens.get(0);
            if (!op.equals("^")) {
                break;
            }
            tokens.remove(0);
            
            double nextValue = parseFactor(tokens);
            value = customPow(value, nextValue);
        }
        return value;
    }

    
    private static double parseFactor(List<String> tokens) {
        String token = tokens.remove(0);
        if (token.equals("(")) {
            double value = parseExpression(tokens);
            tokens.remove(0); 
            return value;
        } 
        else if (token.equals("sqrt")) {
            tokens.remove(0);
            double value = customSqrt(parseExpression(tokens));
            tokens.remove(0); 
            return value;
        } else if (token.equals("-")) {
            return -parseFactor(tokens); 
        } else {
            return Double.parseDouble(token); 
        }
    }

    // Méthode personnalisée pour l'exponentiation
    private static double customPow(double base, double exponent) {
        double result = 1.0;
        for (int i = 0; i < (int) exponent; i++) {
            result *= base;
        }
        return result;
    }

    // Méthode personnalisée pour la racine carrée utilisant la méthode de Newton
    private static double customSqrt(double value) {
        if (value < 0) {
            throw new ArithmeticException("Impossible de calculer la racine carrée d'un nombre négatif");
        }
        double guess = value / 2.0;
        double epsilon = 1e-7;
        
        while (Math.abs(guess * guess - value) > epsilon) {
            guess = (guess + value / guess) / 2.0;
        }
        
        
        return guess;
    }

    // Méthode pour afficher les cas de test et leurs résultats dans le JTextArea
    private static void displayTestCases(JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Quelques cas de tests courants\n\n");

        String[] testCases = {
            "1+1",
            "1 + 2",
            "1 + -1",
            "-1 - -1",
            "5-4",
            "5*2",
            "(2+5)*3",
            "10/2",
            "2+2*5+5",
            "2.8*3-1",
            "2^8",
            "2^8*5-1",
            "sqrt(4)",
            "1/0",
            "\"\"",
            "\"  \"",
            "\"4\"+\"4\"",
            "\"4+3\""
        };

        for (String testCase : testCases) {
           
        	if (!testCase.isEmpty()) {
               
        		try {
                    String result = evaluate(testCase);
                    sb.append(testCase).append(" = ").append(result).append("\n");
                } 
        		catch (Exception e) {
                    sb.append(testCase).append(" = Error: ").append(e.getMessage()).append("\n");
                }
            }
        }

        textArea.setText(sb.toString());
    }
}

