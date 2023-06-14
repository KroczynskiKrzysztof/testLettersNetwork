package Image;

import Basic.Row;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class ImageTools {

    static int[][] resizePixelTableTo100By100(int[][] sourceTable) {
        sourceTable = stretchTo100Width(sourceTable);
        sourceTable = stretchTo100Height(sourceTable);
        return sourceTable;
    }

    private static int[][] stretchTo100Height(int[][] sourceTable) {
        if (sourceTable.length == 100) return sourceTable;
        int currHeight = sourceTable.length;
        int[] rowIndexes = liczbyWRownychOdstepach(currHeight, 100);
        int[][] resultPixelTable = new int[100][100];
        for (int i = 0; i < resultPixelTable.length; i++) {
            resultPixelTable[i] = sourceTable[rowIndexes[i]].clone();
        }
        return resultPixelTable;
    }

    private static int[][] stretchTo100Width(int[][] sourceTable) {
        if (sourceTable[0].length == 100) return sourceTable;
        int currWidth = sourceTable[0].length;
        int[] rowIndexes = liczbyWRownychOdstepach(currWidth, 100);
        int[][] resultPixelTable = new int[sourceTable.length][100];
        for (int i = 0; i < sourceTable.length; i++) {
            for (int j = 0; j < rowIndexes.length; j++) {
                resultPixelTable[i][j] = sourceTable[i][rowIndexes[j]];
            }
        }
        return resultPixelTable;
    }

    static int[][] cutExcessWhite(int[][] sourceTable) {
        int minW = findLeftExcess(sourceTable);
        int maxW = findRightExcess(sourceTable);
        int maxH = findBottomExcess(sourceTable);
        int minH = findTopExcess(sourceTable);
        if (maxH < 100) maxH++;
        if (maxW < 100) maxW++;
        if (minH > maxH || minW > maxW) return new int[0][0];


        int[][] resultTable = new int[maxH - minH][maxW - minW];

        for (int i = 0; i < resultTable.length; i++) {
            System.arraycopy(sourceTable[i + minH], minW, resultTable[i], 0, resultTable[0].length);
        }

        return resultTable;
    }

    static int findLeftExcess(int[][] sourceTable) {

        for (int i = 0; i < sourceTable[0].length; i++) {
            for (int[] ints : sourceTable) {
                if (ints[i] != -1) return i;
            }
        }
        return sourceTable[0].length;
    }

    static int findRightExcess(int[][] sourceTable) {

        for (int i = sourceTable[0].length - 1; i > -1; i--) {
            for (int[] ints : sourceTable) {
                if (ints[i] != -1) return i;
            }
        }
        return sourceTable[0].length;
    }

    static int findTopExcess(int[][] sourceTable) {

        for (int i = 0; i < sourceTable.length; i++) {
            for (int j = 0; j < sourceTable[0].length; j++) {
                if (sourceTable[i][j] != -1) return i;
            }
        }
        return sourceTable[0].length;
    }

    static int findBottomExcess(int[][] sourceTable) {

        for (int i = sourceTable.length - 1; i >= 0; i--) {
            for (int j = 0; j < sourceTable[0].length; j++) {
                if (sourceTable[i][j] != -1) return i;
            }
        }
        return sourceTable[0].length;
    }

    public static void showPicture(int[][] pixelTable) {
        for (int[] ints : pixelTable) {
            for (int anInt : ints) {
                System.out.print(anInt == -1 ? "." : "*");
            }
            System.out.println();
        }
    }

    public static int[] liczbyWRownychOdstepach(int n, int m) { //ew losowo?
        int[] tablica = new int[m];
        double odstep = 0;
        double odst2 = (double) n / m;
        ;

        for (int i = 0; i < m; i++) {
            tablica[i] = ((int) odstep);
            odstep += odst2;
        }
        return tablica;
    }

    public static int[] getTopSamples(int[][] pixelTable, int sampleCount) {
        int[] samplingSpots = liczbyWRownychOdstepach(100, sampleCount);
        int[] samples = new int[sampleCount];

        //System.out.println(Arrays.toString(samplingSpots));

        for (int i = 0; i < samplingSpots.length; i++) {
            int samplingSpot = samplingSpots[i];

            for (int j = 0; j < pixelTable.length; j++) {
                int[] ints = pixelTable[j];
                if (ints[samplingSpot] != -1) {
                    samples[i] = j;
                    break;
                }

            }
        }
        return samples;
    }

    public static int[] getBottomSamples(int[][] pixelTable, int sampleCount) {
        int[] samplingSpots = liczbyWRownychOdstepach(100, sampleCount);
        int[] samples = new int[sampleCount];


        for (int i = 0; i < samplingSpots.length; i++) {
            int samplingSpot = samplingSpots[i];

            for (int j = pixelTable.length - 1; j >= 0; j--) {
                int[] ints = pixelTable[j];
                if (ints[samplingSpot] != -1) {
                    samples[i] = j;
                    break;
                }

            }
        }
        return samples;
    }

    public static int[] getLeftSamples(int[][] pixelTable, int sampleCount) {
        int[] samplingSpots = liczbyWRownychOdstepach(100, sampleCount);
        int[] samples = new int[sampleCount];
        for (int i = 0; i < samplingSpots.length; i++) {
            int[] row = pixelTable[samplingSpots[i]];
            for (int j = 0; j < row.length; j++) {
                if (row[j] != -1) {
                    samples[i] = j;
                    break;
                }
                if (j == row.length - 1) samples[i] = j;
            }
        }
        return samples;
    }

    public static int[] getRightSamples(int[][] pixelTable, int sampleCount) {
        int[] samplingSpots = liczbyWRownychOdstepach(100, sampleCount);
        int[] samples = new int[sampleCount];
        for (int i = 0; i < samplingSpots.length; i++) {
            int[] row = pixelTable[samplingSpots[i]];
            for (int j = row.length - 1; j >= 0; j--) {
                if (row[j] != -1) {
                    samples[i] = j;
                    break;
                }
                if (j == row.length - 1) samples[i] = j;
            }
        }
        return samples;
    }

    static Double[] getSamplesFromFile(Path filePath, int samplesPerSide) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(filePath.toFile());

        int h = bufferedImage.getHeight();
        int x = bufferedImage.getWidth();
        int[][] pixelTable = new int[h][x];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < x; j++) {
                pixelTable[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        return getSamples(pixelTable,samplesPerSide);

    }
    public static Double[] getSamples(int[][] pixelTable,int samplesPerSide){
        int[][] resizedPixelTable = cutExcessWhite(pixelTable);
        int widthRaw = resizedPixelTable[0].length;
        int heightRaw = resizedPixelTable.length;

        resizedPixelTable = resizePixelTableTo100By100(resizedPixelTable);

        int[] topSamples = getTopSamples(resizedPixelTable, samplesPerSide);
        int[] bottomSamples = getBottomSamples(resizedPixelTable, samplesPerSide);
        int[] leftSamples = getLeftSamples(resizedPixelTable, samplesPerSide);
        int[] rightSamples = getRightSamples(resizedPixelTable, samplesPerSide);
        int holeCount = getHoleCount(resizedPixelTable);
        Double[] dataRow = new Double[samplesPerSide * 4 + 2];
        for (int i = 0; i < topSamples.length; i++) {
            dataRow[i] = (double) topSamples[i];
            dataRow[i + samplesPerSide] = (double) bottomSamples[i];
            dataRow[i + samplesPerSide * 2] = (double) leftSamples[i];
            dataRow[i + samplesPerSide * 3] = (double) rightSamples[i];
        }
        dataRow[4*samplesPerSide]= (double) heightRaw/ (double) widthRaw;

        dataRow[4*samplesPerSide+1]= (double) holeCount;

        return dataRow;
    }
    public static int[][] fillWhite(int[][] sourceTable,int x,int y){
        ArrayList<int[]> pixelsToFill = new ArrayList<>();
        pixelsToFill.add(new int[]{y,x});
        while (pixelsToFill.size()!=0){
            int pixelY = pixelsToFill.get(0)[0];
            int pixelX = pixelsToFill.get(0)[1];
            int pixelValue = sourceTable[pixelY][pixelX];
            if (pixelValue==-1){
                sourceTable[pixelY][pixelX]=0;
                if (pixelY<sourceTable.length-1) pixelsToFill.add(new int[]{pixelY+1,pixelX});
                if (pixelY>0) pixelsToFill.add(new int[]{pixelY-1,pixelX});
                if (pixelX<sourceTable[0].length-1)pixelsToFill.add(new int[]{pixelY,pixelX+1});
                if (pixelX>0) pixelsToFill.add(new int[]{pixelY,pixelX-1});
            }



            pixelsToFill.remove(0);
        }
        return sourceTable;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("Z:\\sem4\\nai\\Perceptron\\Letters\\a1.png"));
        int h = bufferedImage.getHeight();
        int x = bufferedImage.getWidth();
        int[][] pixelTable = new int[h][x];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < x; j++) {
                pixelTable[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        int[][] resizedPixelTable = resizePixelTableTo100By100(cutExcessWhite(pixelTable));
        showPicture(resizedPixelTable);

        System.out.println(getHoleCount(resizedPixelTable));
//        fillWhite(resizedPixelTable, 99, 0);
//        showPicture(resizedPixelTable);
    }
    public static int getHoleCount(int[][] sourceTable){
        int counter = 0;
        for (int i = 0; i < sourceTable.length; i++) {
            for (int i1 = 0; i1 < sourceTable[i].length; i1++) {
                if (sourceTable[i][i1]==-1){
                    sourceTable=fillWhite(sourceTable,i1,i);

                    counter++;
                }
            }
        }
        return counter;
    }
    public static ArrayList<Row> readImageFiles(String path, int sampleCount) throws IOException {

        ArrayList<Row> rows = new ArrayList<>();

        FileVisitor<Path> fileVisitor = new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().endsWith(".png")) {
                    Row row = new Row(ImageTools.getSamplesFromFile(file, sampleCount), file.getParent().getFileName().toString());
                    //row.normalize();
                    rows.add(row);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        };


        Files.walkFileTree(new File(path).toPath(), fileVisitor);


        return rows;
    }
}
