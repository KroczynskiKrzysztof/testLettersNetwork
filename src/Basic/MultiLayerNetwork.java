package Basic;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MultiLayerNetwork {
    public ArrayList<Layer> layers = new ArrayList<>();
    ArrayList<Double[]> outputs = new ArrayList<>();
    ArrayList<Double[]> deltas = new ArrayList<>();
    ArrayList<String> classes;
    double a;


    public Double[] askAllLayers(Double[] data) {
        Double[] doubles = data.clone();
        for (int i = 0; i < layers.size(); i++) {
            doubles = layers.get(i).queryLayer(doubles).clone();
            outputs.set(i, doubles.clone());
        }
        return outputs.get(outputs.size() - 1);
    }
    public Double[] askAllLayersRaw(Double[] data){
        Double[] doubles = data.clone();
        for (int i = 0; i < layers.size()-1; i++) {
            doubles=layers.get(i).queryLayer(doubles).clone();
            outputs.set(i,doubles.clone());
        }
        doubles=layers.get(layers.size()-1).queryLayerRaw(doubles).clone();
        outputs.set(layers.size()-1,doubles.clone());
        return outputs.get(outputs.size()-1);
    }


    public void learn(Row row) {


        Double[] expectedOutput = new Double[classes.size()];
        Arrays.fill(expectedOutput, 0.);
        if (classes.contains(row.className))
            expectedOutput[classes.indexOf(row.className)] = 1.0;
        Double[] output = askAllLayers(row.data);
        Double[] lastLayerDeltas = deltas.get(deltas.size() - 1);
        //last layer (delta)
        for (int i = 0; i < layers.get(layers.size() - 1).perceptrons.size(); i++) {
            lastLayerDeltas[i] = (expectedOutput[i] - output[i])*output[i]*(1-output[i]);//
        }
        deltas.set(deltas.size() - 1, lastLayerDeltas);


        //other layers (delta)
        for (int i = deltas.size() - 2; i >= 0; i--) {
            for (int j = 0; j < deltas.get(i).length; j++) {
                double sum = 0.;
                for (int k = 0; k < deltas.get(i + 1).length; k++) {
                    sum += deltas.get(i + 1)[k] * layers.get(i + 1).perceptrons.get(k).weightsAndT[j];
                }
                double delta = sum * outputs.get(i)[j] * (1 - outputs.get(i)[j]);
                deltas.get(i)[j] = delta;
            }
        }


        //dostosowanie wag pierwszej warstwy
        Layer firstLayer = layers.get(0);
        for (int i = 0; i < firstLayer.perceptrons.size(); i++) {
            for (int j = 0; j < firstLayer.perceptrons.get(i).weightsAndT.length - 1; j++) {
                firstLayer.perceptrons.get(i).weightsAndT[j] += a * deltas.get(0)[i] * row.data[j];
            }
            firstLayer.perceptrons.get(i).weightsAndT[firstLayer.perceptrons.get(i).weightsAndT.length - 1] += a * deltas.get(0)[i];
        }
        //dostosowanie wag kolejnych warstw
        for (int i = 1; i < layers.size(); i++) {
            for (int j = 0; j < layers.get(i).perceptrons.size(); j++) {
                for (int k = 0; k < layers.get(i).perceptrons.get(j).weightsAndT.length - 1; k++) {
                    layers.get(i).perceptrons.get(j).weightsAndT[k] += a * deltas.get(i)[j] * outputs.get(i - 1)[k];
                }
                layers.get(i).perceptrons.get(j).weightsAndT[layers.get(i).perceptrons.get(j).weightsAndT.length - 1] += a * deltas.get(0)[i];
            }
        }


    }

    public MultiLayerNetwork(int[] hiddenLayerPerceptronCount, ArrayList<String> classes, int perceptronSize, double a) {
        this.a = a;
        this.classes = classes;
        if (hiddenLayerPerceptronCount.length > 0) {
            layers.add(new Layer(hiddenLayerPerceptronCount[0], perceptronSize, a));
            outputs.add(new Double[hiddenLayerPerceptronCount[0]]);
            deltas.add(new Double[hiddenLayerPerceptronCount[0]]);
            for (int i = 1; i < hiddenLayerPerceptronCount.length; i++) {
                int j = hiddenLayerPerceptronCount[i];
                layers.add(new Layer(j, hiddenLayerPerceptronCount[i - 1], a));

                outputs.add(new Double[j]);
                deltas.add(new Double[j]);
            }
        }

        ArrayList<Perceptron> outLayer = new ArrayList<>();
        for (String aClass : classes) {
            if (hiddenLayerPerceptronCount.length > 0)
                outLayer.add(new SigmoidPerceptron(hiddenLayerPerceptronCount[hiddenLayerPerceptronCount.length - 1], aClass, a));
            else
                outLayer.add(new SigmoidPerceptron(perceptronSize, aClass, a));

        }
        outputs.add(new Double[classes.size()]);
        deltas.add(new Double[classes.size()]);
        layers.add(new Layer(outLayer));
    }

//    public static void main(String[] args) throws IOException {
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("a");
//        MultiLayerNetwork multiLayerNetwork = new MultiLayerNetwork(new int[]{4,3},arrayList,1,1.);
//        System.out.println(Arrays.toString(multiLayerNetwork.layers.get(1).perceptrons.get(0).weightsAndT));
//        multiLayerNetwork.saveMe("test.json");
//        multiLayerNetwork= loadNetwork("test.json");
//        multiLayerNetwork.teach(new Row(new Double[]{1.},"a"));
//        System.out.println(Arrays.toString(multiLayerNetwork.layers.get(1).perceptrons.get(0).weightsAndT));
//    }



    public void learnEpoch(ArrayList<Row> rows, int epochs) {
        ArrayList<Double[]> certainties = new ArrayList<>();

//        System.out.println("epochs: ");
        for (int i = 0; i < epochs; i++) {
            Collections.shuffle(rows);
            for (Row row : rows) {
                this.learn(row);
            }
//            certainties.add(new Double[classes.size()]);
//            Arrays.fill(certainties.get(certainties.size()-1),0.);
//            for (int j = 0; j < rows.size(); j++) {
//                Double[] cetainty=askAllLayers(rows.get(j).data);
//                for (int i1 = 0; i1 < cetainty.length; i1++) {
//                    certainties.get(certainties.size()-1)[i1]+=cetainty[i1];
//                }
//
//            }
            if (i * 100. / epochs % 1 == 0) {


                System.out.println("\t " +( i * 100. / epochs+1)+"\t"+new Timestamp(System.currentTimeMillis()));
//                for (Layer layer : layers) {
//                    for (Perceptron perceptron : layer.perceptrons) {
//                        perceptron.normalizeMe();
//                    }
//                }
            }
        }
//        try {
//            FileWriter fileWriter = new FileWriter("C:\\Users\\krocz\\Documents\\nai\\sumOfCertainties"+new Timestamp(System.currentTimeMillis()).toString().replace(":","-")+".json");
//            fileWriter.write(new Gson().toJson(certainties));
//            fileWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    public void saveMe(String name) {
        try {
            FileWriter fileWriter = new FileWriter(name);
            fileWriter.write(new Gson().toJson(this));
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultiLayerNetwork loadNetwork(String path) throws IOException {
        return new Gson().fromJson(Files.readString(Path.of(path)), MultiLayerNetwork.class);
    }

    public static int findMaxIndex(Double[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

//    public Double[] testNetwork(ArrayList<Row> testSet) {
//        Double[] result = new Double[(int) testSet.stream().map(row -> row.className).distinct().count()];
//        Arrays.fill(result, 0.);
//        for (Row row : testSet) {
//            Double[] expectedOutput = new Double[classes.size()];
//            Arrays.fill(expectedOutput, 0.);
//            if (classes.contains(row.className))
//                expectedOutput[classes.indexOf(row.className)] = 1.0;
//            Double[] queryResult = askAllLayers(row.data);
//            if (findMaxIndex(expectedOutput) == findMaxIndex(queryResult)) result[findMaxIndex(expectedOutput)]++;
//
//        }
//        for (int i = 0; i < result.length; i++) {
//            int finalI = i;
//            result[i] /= testSet.stream().filter(row -> row.className.equals(classes.get(finalI))).count();
//        }
//        return result;
//    }

//    public String testNetworkString(ArrayList<Row> testSet) {
//        String result = "";
//        for (Row row : testSet) {
//            Double[] expectedOutput = new Double[classes.size()];
//            Arrays.fill(expectedOutput, 0.);
//            if (classes.contains(row.className))
//                expectedOutput[classes.indexOf(row.className)] = 1.0;
//            Double[] queryResult = askAllLayers(row.data);
//            if (!(findMaxIndex(expectedOutput) == findMaxIndex(queryResult)))
//                result += (char)(findMaxIndex(expectedOutput) + 'A') + " -> " + (char) (findMaxIndex(queryResult) + 'A')+"\n";
//
//        }
//        return result;
//    }

    public int[][] testNetworkMatrix(ArrayList<Row> testSet) {
        int[][] result = new int[classes.size()][classes.size()];
        for (Row row : testSet) {

            Double[] queryResult = askAllLayers(row.data);
            result[classes.indexOf(row.className)][findMaxIndex(queryResult)]++;


        }
        return result;
    }
}
