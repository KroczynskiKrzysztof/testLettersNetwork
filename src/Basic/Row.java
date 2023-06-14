package Basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Row {
    public Double[] data;
    public String className;

    public Row(Double[] data, String className) {
        this.data = data;
        this.className = className;
    }
    public Row(String csvLine){
        String[] values=csvLine.split(",");
        Double[] data = new Double[values.length-1];
        for (int i = 0; i < values.length-1; i++) {
            data[i]=Double.parseDouble(values[i]);
        }
        this.data=data;
        this.className =values[values.length-1];
    }



    public static ArrayList<Row> importFromCsv(String path){
        ArrayList<Row> rows = new ArrayList<>();
        String fileContents = "";
        try {
            fileContents= Files.readString(Path.of(path)).replace("\r","");
        } catch (IOException ignored) {
        }

        String[] lines = fileContents.split("\n");
        for (String line : lines) {
            rows.add(new Row(line));
        }
        return rows;
    }
    @Override
    public String toString(){
        return className+": "+ Arrays.toString(data);
    }

    public void normalize(){
        Double length = 0.;
        for (Double datum : data) {
            length+=datum*datum;
        }
        length=Math.sqrt(length);
        for (int i = 0; i < data.length; i++) {
            data[i]/=length;
        }
    }
}
