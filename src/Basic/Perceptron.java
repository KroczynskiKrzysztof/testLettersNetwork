package Basic;

import java.util.*;

public class Perceptron implements Cloneable{
    double[] weightsAndT;
    int size;
    public String name;
    double a;

    public Perceptron(int size, String name, double a) {
        this.size = size;
        this.name=name;
        this.a=a;
        Random random = new Random();
        weightsAndT =new double[size+1];
        for (int i = 0; i < weightsAndT.length; i++) {
            weightsAndT[i]= random.nextDouble(-0.1,0.1);
        }
        normalizeMe();
    }
    public int think(Double[] data) throws IllegalArgumentException {
        double sum = thinkRaw(new Row(data,""));

        if (sum>=0) return 1;
        return 0;
    }
    public int thinkButBetter(Row row) throws IllegalArgumentException {
        Double[] data = row.data;
        int guess = think(data);
        int sign;
        if (name.equalsIgnoreCase(row.className) && guess==1) {
            sign = 0;
        }
        else if (name.equalsIgnoreCase(row.className) && guess==0) sign=1;
        else if (!name.equalsIgnoreCase(row.className) && guess==0) sign=0;
        else sign=-1;
        return (sign==0)?1:0;
    }
    public boolean thinkAndLearn(Row row){
        int guess=think(row.data);
        Double[] data = new Double[row.data.length+1];
        System.arraycopy(row.data, 0, data, 0, data.length - 1);
        data[data.length-1]=-1d;
        int sign;
        if (name.equals(row.className) && guess==1) sign=0;
        else if (name.equals(row.className) && guess==0) sign=1;
        else if (!name.equals(row.className) && guess==0) sign=0;
        else sign=-1;

        for (int i = 0; i < this.weightsAndT.length; i++) {
            this.weightsAndT[i]+=a*sign*data[i];
        }
        return sign==0;
    }

    public void thinkAndLearnALot(Collection<Row> rows){
        if (rows.size()==0) return;
        for (Row row : rows) {
            boolean outcome=this.thinkAndLearn(row);
        }
    }
    public double thinkALot(Collection<Row> rows, boolean verbose){
        if (rows.size()==0) return 1;
        double correct=0;
        for (Row row : rows) {
            int wasCorrect=thinkButBetter(row);
            correct+=wasCorrect;
            if (verbose && wasCorrect==0){
                System.out.println(row);
            }
        }
        return correct/rows.size();
    }


    @Override
    public Perceptron clone() {
        Perceptron p = new Perceptron(this.size,this.name,this.a);

        p.weightsAndT =this.weightsAndT.clone();

        return p;
    }
    public static Perceptron train(ArrayList<Row> training,String className, double a, int epochs){
        Perceptron perceptron = new Perceptron(training.get(0).data.length,className,a);
        Perceptron mostAccurate=perceptron.clone();
        double bestAccuracy=0;
        for (int i = 0; i < epochs; i++) {
            perceptron.thinkAndLearnALot(training);
            double epochAccuracy=perceptron.thinkALot(training,false);
            if (epochAccuracy>bestAccuracy){
                mostAccurate=perceptron.clone();
                bestAccuracy=epochAccuracy;
            }
        }
        return mostAccurate;
    }
    public double thinkRaw(Row row){
        Double[] data = row.data;
        if (data.length!=size) throw new IllegalArgumentException();
        double sum=0;
        for (int i = 0; i < size; i++) {
            sum+=data[i]* weightsAndT[i];
        }
        sum-= weightsAndT[weightsAndT.length-1];
        return sum;
    }
    public void normalizeMe(){
        double lenSquared = 0;
        for (int i = 0; i < weightsAndT.length-1; i++) {
            lenSquared+=Math.pow(weightsAndT[i],2);
        }
        double len = Math.sqrt(lenSquared);
        for (int i = 0; i < weightsAndT.length-1; i++) {
            weightsAndT[i]/=len;
        }
    }
    public static HashMap<String,Double> askSingleLayerNetwork(ArrayList<Perceptron> layer,Double[] data){
        LinkedHashMap<String,Double> result = new LinkedHashMap<>();

        for (Perceptron perceptron : layer) {
            result.put(perceptron.name, perceptron.thinkRaw(new Row(data,"")));
        }

        ArrayList<Map.Entry<String, Double>> listOfEntries = new ArrayList<>(result.entrySet());
        listOfEntries.sort((o1, o2) -> -Double.compare(o1.getValue(), o2.getValue()));

        result=new LinkedHashMap<>();

        for (Map.Entry<String, Double> listOfEntry : listOfEntries) {
            result.put(listOfEntry.getKey(),listOfEntry.getValue());
        }

        return result;
    }


    public Double sigmoidThink(Double[] data) {
        return (double) think(data);
    }
}
