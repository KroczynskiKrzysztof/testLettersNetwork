package Image;

import Basic.Perceptron;
import Basic.Row;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static Basic.MultiLayerNetwork.findMaxIndex;

public class ImageSingleLayerNetwork {
    public static void main(String[] args) throws IOException {
        System.out.println("started at: "+new Timestamp(new Date().getTime()));
        double a = .1;
        int epochs = 10000;
        int sampleCount= 50;
        String pathToTrainingData = "C:\\Users\\krocz\\Downloads\\Litery";
        Path testFilePath = Path.of("Z:\\sem4\\nai\\Basic.Perceptron\\Letters\\a1.png");
        AtomicInteger i = new AtomicInteger(1);
        ArrayList<Row> trainingData = ImageTools.readImageFiles(pathToTrainingData,sampleCount);
        System.out.println("read all data and converted it into usable format: "+ new Timestamp(new Date().getTime()));
        ArrayList<Perceptron> perceptronArrayList = new ArrayList<>();
        trainingData.stream().map(row -> row.className).distinct().forEach(
                className -> {
                    Perceptron p = Perceptron.train(trainingData,className,a,epochs);
                    p.normalizeMe();
                    perceptronArrayList.add(p);
                    System.out.println(i.getAndIncrement()+ " at: "+new Timestamp(new Date().getTime()));
                }
        );
        Double[] d =testNetwork(trainingData,perceptronArrayList);
        for (char a1 = 'A'; a1 <='Z' ; a1++) {
            System.out.print(a1+"\t ");
            System.out.println(d[a1-'A']);
        }
//        Scanner scanner = new Scanner(System.in);
//       // while (true) {
//            Double[] data = ImageTools.getSamplesFromFile(Path.of("Z:\\sem4\\nai\\Perceptron\\Letters\\2.png"), sampleCount);
//            HashMap<String, Double> verdictsAndValues = Perceptron.askSingleLayerNetwork(perceptronArrayList, data);
//            System.out.println("Verdict \t value");
//            verdictsAndValues.forEach(((s1, aDouble) -> System.out.printf("%s \t %f %n", s1, aDouble)));
        //}

    }
    public static Double[] testNetwork(ArrayList<Row> testSet,ArrayList<Perceptron> perceptronArrayList){
        Double[] result = new Double[(int) testSet.stream().map(row -> row.className).distinct().count()];
        ArrayList<String> classes= testSet.stream().map(row -> row.className).distinct().collect(Collectors.toCollection(ArrayList<String>::new));
        Arrays.fill(result,0.);
        for (Row row : testSet) {
            Double[] expectedOutput=new Double[(int) testSet.stream().map(row1 -> row1.className).distinct().count()];
            Arrays.fill(expectedOutput,0.);
            if (classes.contains(row.className))
                expectedOutput[classes.indexOf(row.className)]= 1.0;
            LinkedHashMap<String, Double> queryResult = (LinkedHashMap<String, Double>) Perceptron.askSingleLayerNetwork(perceptronArrayList,row.data);
            if (new ArrayList<>(queryResult.entrySet()).get(0).getKey().equals(row.className)) result[findMaxIndex(expectedOutput)]++;

        }
        for (int i = 0; i < result.length; i++) {
            int finalI = i;
            result[i]/=testSet.stream().filter(row -> row.className.equals(classes.get(finalI))).count();
        }
        return result;
    }
}
