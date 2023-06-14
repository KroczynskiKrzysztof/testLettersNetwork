package Basic;

import java.util.ArrayList;

public class Layer {
    public ArrayList<Perceptron> perceptrons = new ArrayList<>();

    public Layer(ArrayList<Perceptron> perceptrons) {
        this.perceptrons = perceptrons;
    }
    public Layer(int amount,int size, double a){
        for (int i = 0; i < amount; i++) {
            perceptrons.add(new SigmoidPerceptron(size, String.valueOf(i),a));
        }
    }
    public Double[] queryLayer(Double[] data){
        Double[] result = new Double[perceptrons.size()];
        for (int i = 0; i < perceptrons.size(); i++) {
            result[i]=perceptrons.get(i).sigmoidThink(data);
        }
        return result;
    }
    public Double[] queryLayerRaw(Double[] data){
        Double[] result = new Double[perceptrons.size()];
        for (int i = 0; i < perceptrons.size(); i++) {
            result[i]=perceptrons.get(i).thinkRaw(new Row(data,""));
        }

        return result;
    }
}
