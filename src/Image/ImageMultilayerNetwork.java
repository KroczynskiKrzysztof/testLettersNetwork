package Image;

import Basic.MultiLayerNetwork;
import Basic.Row;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ImageMultilayerNetwork {
    public static void main(String[] args) throws IOException {
        String trainingSetPath = "C:\\Users\\krocz\\Downloads\\Litery";
        int sampleCount = 10;
        int[] hiddenPerceptronCount = new int[]{500,500};
        double a = .01;
        int epochs = 500;


        ArrayList<Row> trainingSet = ImageTools.readImageFiles(trainingSetPath,sampleCount);
        //MultiLayerNetwork multiLayerNetwork = MultiLayerNetwork.loadNetwork("Z:\\sem4\\nai\\Perceptron\\src\\milionEpochNetwork.json");


        ArrayList<String> classNames =  trainingSet.stream().map(row -> row.className).distinct().collect(Collectors.toCollection(ArrayList::new));

        MultiLayerNetwork multiLayerNetwork = new MultiLayerNetwork(hiddenPerceptronCount,classNames,sampleCount*4+2,a);
        multiLayerNetwork.learnEpoch(trainingSet,epochs);

        multiLayerNetwork.saveMe("C:\\Users\\krocz\\Documents\\nai\\network"+epochs+new Timestamp(System.currentTimeMillis()).toString().replace(":","-")+".json");
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("ready!");
//        while(true){
//            Double[] data = ImageTools.getSamplesFromFile(Path.of(scanner.nextLine()), sampleCount);
//            Double[] verdict = multiLayerNetwork.askAllLayers(data);
//            ArrayList<Map.Entry<String, Double>> list = new ArrayList<>();
//            for (int i = 0; i < verdict.length; i++) {
//                Double aDouble = verdict[i];
//                list.add(new AbstractMap.SimpleEntry<>(classNames.get(i), aDouble));
//            }
//            list.sort(((o1, o2) -> -Double.compare(o1.getValue(), o2.getValue())));
//            for (Map.Entry<String, Double> stringDoubleEntry : list) {
//                System.out.println(stringDoubleEntry.getKey() + "\t" + stringDoubleEntry.getValue());
//            }
//        }


        int[][] matrix=(multiLayerNetwork.testNetworkMatrix(trainingSet));
        for (int i = 0; i < matrix.length; i++) {
            System.out.print((char)('A'+i)+":\t");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.print("\t\tright: "+matrix[i][i]);
            System.out.println();
        }
        double sum = 0.;
        for (int i = 0; i < matrix.length; i++) {
            sum+=matrix[i][i];
        }
        System.out.println("overall accuracy: "+sum/trainingSet.size());
    }
}
