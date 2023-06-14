package Basic;

public class SigmoidPerceptron extends Perceptron{
    public SigmoidPerceptron(int size, String name, double a) {
        super(size, name, a);
    }

    public static SigmoidPerceptron loadSigmoidPerceptron(double[] weightsAndT,String name,double a){
        SigmoidPerceptron sigmoidPerceptron = new SigmoidPerceptron(weightsAndT.length, name,a);
        sigmoidPerceptron.weightsAndT=weightsAndT;
        return sigmoidPerceptron;
    }

@Override
    public Double sigmoidThink(Double[] data) throws IllegalArgumentException {
        return sigmoid(super.thinkRaw(new Row(data,"")));
    }
    public static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }
    public static double reverseSigmoid(double y) {return Math.log(y / (1 - y));}
}
