package Basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SoloPerceptron {
    public static void main(String[] args) {
        // declare variables
        double a = 0;
        String trainingSet = null;
        String testSet = null;
        String className = null;
        int epochs = 1;

        for (int i = 0; i < args.length-1; i++) {
            switch (args[i]) {
                case "-a" -> a = Double.parseDouble(args[i + 1]);
                case "-training" -> trainingSet = args[i + 1];
                case "-test" -> testSet = args[i + 1];
                case "-epochs" -> epochs = Integer.parseInt(args[i + 1]);
                case "-class" -> className = args[i + 1];
            }
        }

        // check if all required arguments are supplied
        if(a == 0 || trainingSet == null || className==null) {
            System.out.println("Missing required arguments");
            System.exit(0);
        }
        if (epochs==1) System.out.println("-epochs argument not supplied, default value (1) substituted");

        ArrayList<Row> training = Row.importFromCsv(trainingSet);
        Perceptron perceptron = new Perceptron(training.get(0).data.length,className,a);
        System.out.printf("starting perceptron weights: %s%n", Arrays.toString(perceptron.weightsAndT));
        Perceptron mostAccurate=perceptron.clone();
        double bestAccuracy=0;
        int epochOfMostAccurate=0;
        for (int i = 0; i < epochs; i++) {
            perceptron.thinkAndLearnALot(training);
            double epochAccuracy=perceptron.thinkALot(training,false);
            if (epochAccuracy>bestAccuracy){
                mostAccurate=perceptron.clone();
                epochOfMostAccurate=i;
                bestAccuracy=epochAccuracy;
            }
        }
        System.out.printf("The highest accuracy was achieved in epoch %d and was equal to: %f%n",epochOfMostAccurate,bestAccuracy);

        if(testSet == null) {
            System.out.println("No -test argument supplied\nManual testing mode:");
            System.out.printf("please enter %d values seperated by white characters:%n",perceptron.size);
            Scanner scanner = new Scanner(System.in);
            try {
                while (true){
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("")) break;
                    String[] inputValues = input.split("\\s+");
                    if (inputValues.length != perceptron.size) {
                        System.out.printf("Expected %d values, got %d values%n", perceptron.size, inputValues.length);
                    }
                    Double[] parsedData = new Double[inputValues.length];
                    for (int i = 0; i < parsedData.length; i++) {
                        parsedData[i] = Double.parseDouble(inputValues[i]);
                    }
                    int guess = mostAccurate.think(parsedData);
                    if (guess == 1) System.out.printf("Verdict: %s%n", mostAccurate.name);
                    else System.out.printf("Verdict: not %s%n", mostAccurate.name);
                }
            }catch (Exception e){System.exit(0);}
        }else {
            ArrayList<Row> test = Row.importFromCsv(testSet);
            System.out.println(mostAccurate.thinkALot(test,true));
            System.out.printf("perceptron weights and t after training: %s%n", Arrays.toString(mostAccurate.weightsAndT));

        }
    }
}
