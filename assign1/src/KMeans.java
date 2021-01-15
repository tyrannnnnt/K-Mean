/*
program description: Get KMean point through
                        1.get random centroid point from random data point
                        2.separate data point by nearest centroid
                        3.do average and get new centroid
                        4.repeat 2 and 3 until converge
                        repeat 1-4 for specified times and select the most frequency result to print out(to solve bonus problem)
Author name : ZHANG Jiayi
Student Number: 19250568
*/

import java.util.Scanner;
import java.io.*;

public class KMeans {
    public static void main(String [] args)throws Exception{
        new KMeans().runApp(args);
    }
    void runApp(String [] args)throws Exception{
        //get input file name
        File inFName = new File(args[1]);
        Scanner input1 = new Scanner(inFName);

        //get total num
        int totalNum = getNum(input1.nextLine());

        //get point data
        int[][] dataPoint = getNumArray(input1, totalNum,2);

        //get number of k
        int k = getNum(args[0]);
        int[][] kArray = new int[k][2];

        //cluster
        int [] clusterArray = new int [totalNum];

        //total run times
        int runtimes = 20;

        //specify run times
        int[] count = new int[runtimes];

        double[] recordDis1 = new double[runtimes];
        double[] countArray = new double[runtimes];

        //record total distance in every times and repeat 1-4 steps
        for(int i = 0; i < runtimes; i++) {
            double dis = mainApp(args, totalNum, dataPoint, kArray, clusterArray, k);
            recordDis1[i] = dis;
        }

        //count the number of the same distance appeared in the distance array
        for(int i = 0; i < runtimes; i++) {
            if(recordDis1[i] != 0) {
                countArray[i] = recordDis1[i];
                for (int j = 0; j < runtimes; j++) {
                    if (countArray[i] == recordDis1[j]) {
                        count[i]++;
                        recordDis1[j] = 0;
                    }
                }
            }
        }

        int max = count[0];
        int pos = 0;

        //find which result is the most frequency
        for(int i = 0; i < runtimes; i++) {
            if(count[i] > max){
                max = count[i];
                pos = i;
            }
        }

        //output the most frequency result
        for(int i = 0; i < runtimes; i++) {
            double dis = mainApp(args, totalNum, dataPoint, kArray, clusterArray, k);
            if (countArray[pos] == dis) {
                outputApp(args, totalNum, dataPoint, kArray, clusterArray, k);
                break;
            }
        }

        input1.close();

        System.out.println("over");
    }

    //print output file
    void outputApp(String [] args, int totalNum, int[][] dataPoint, int[][]kArray, int[] clusterArray,int k)throws Exception{
        //get output filename
        PrintWriter out = new PrintWriter(args[2]);

        //print total number
        out.println(totalNum);

        //print k point
        for (int i = 0; i < k; i++){
            out.print(kArray[i][0] + ", ");
            out.println(kArray[i][1]);
        }

        //print data point and cluster num
        for(int i = 0; i <totalNum; i++){
            out.print(dataPoint[i][0] + ", ");
            out.print(dataPoint[i][1] + ", ");
            out.println(clusterArray[i]);
        }

        out.close();
    }

    //do 1-4 steps with data point, total number, kArray(centroid), clusterArray(every point belongs to which cluster)
    double mainApp(String [] args, int totalNum, int[][] dataPoint, int[][]kArray, int[] clusterArray,int k)throws Exception{
        //get random k point(centroid point)
        for(int i = 0; i < kArray.length;i++){
            for(int j = 0; j < kArray[k-1].length;j++) {
                double d = Math.random();
                int c =(int) (d*totalNum);
                kArray[i][j] = dataPoint[c][j];
            }
        }

        //cluster
        for(int i = 0; i < dataPoint.length; i++){
            clusterArray[i] = smallerDistance(dataPoint, kArray)[i];
        }
        //clusterArray = smallerDistance(dataPoint, kArray);

        for (int i = 0; i < k; i++) {
            kArray[i][0] = average(dataPoint, clusterArray, k)[i][0];
            kArray[i][1] = average(dataPoint, clusterArray, k)[i][1];
        }

        //repeat separate and get k point until converge
        do {
            if(isConverge(kArray,dataPoint,clusterArray)){
                break;
            }
            for(int i = 0; i < dataPoint.length; i++){
                clusterArray[i] = smallerDistance(dataPoint, kArray)[i];
            }
            for (int i = 0; i < k; i++) {
                kArray[i][0] = average(dataPoint, clusterArray, k)[i][0];
                kArray[i][1] = average(dataPoint, clusterArray, k)[i][1];
            }
        }while(true);

        //get total distance which is the distance between every point and their centroid
        double totalDistance = 0;
        for(int i = 0; i < dataPoint.length; i++ ){
            for(int j = 0; j < kArray.length; j++){
                totalDistance += distance(dataPoint[i][0], dataPoint[i][1], kArray[j][0], kArray[j][1]);
            }
        }

        return totalDistance;

    }

    //judge if it is converge
    boolean isConverge(int[][] kArray, int[][] dataPoint, int[] clusterArray){
        boolean isConverge = false;
        int count = 0;

        for(int i = 0; i < clusterArray.length; i++) {
            if (clusterArray[i] == (smallerDistance(dataPoint, kArray)[i])) {
                count++;
            }
        }
        if(count == clusterArray.length) {
            isConverge = true;
        }
        return isConverge;
    }

    //make data points average to get new centroid and return average points as centroid
    int[][] average (int[][] dataPoint, int[] clusterArray, int k){
        int [][] average = new int[k][2];  //avg of k points
        int[] sumX = new int[k];
        int[] sumY = new int[k];
        int[] count = new int[k];

        for(int j = 0; j < k; j++){
            for(int i = 0; i < clusterArray.length; i++) { //i : number of point
                if (clusterArray[i] == j) {
                    sumX[j] += dataPoint[i][0];
                    sumY[j] += dataPoint[i][1];
                    count[j] += 1;
                }
            }
        }

        for(int i = 0; i < k; i++){
            if(count[i] != 0) {
                average[i][0] = sumX[i] / count[i];
                average[i][1] = sumY[i] / count[i];
            }
            else{
                average[i] = dataPoint[(int)(Math.random()*clusterArray.length)];
            }
        }

        return average;
    }

    //find the smallest distance point and return the position of data point
    int [] smallerDistance ( int [][] dataPoint, int[][] kArray){

        int[] small = new int[dataPoint.length];
        double[] distance = new double[kArray.length];
        for(int i = 0; i < dataPoint.length; i++ ){
            for(int j = 0; j < kArray.length; j++){
                distance[j] = distance(dataPoint[i][0], dataPoint[i][1], kArray[j][0], kArray[j][1]);
            }
            small[i] = findNearest(distance);
        }
        return small;
    }

    //find a smallest data and return the position
    int findNearest(double[] array){
        double min = array[0];
        int pos = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                pos = i;
            }
        }
        return pos;
    }

    //get distance between two points
    double distance(int x1, int y1, int x2, int y2){
        double dis = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        return  dis;
    }

    //change string to int and return integer
    int getNum (String a){
        int num = 0;
        for(int i = 0;i < a.length(); i++){
            num = num *10 + (a.charAt(i)-'0');
        }
        return num;
    }

    //read string in the file and change it to int, then return the int array
    int[] getInteger(String a, int n){ //n is the number of integers
        int [] b = new int[n];
        int count = 0;
        String str = "";
        for (int i = 0; i < a.length(); i++){
            if(a.charAt(i) != ' ' && a.charAt(i) != ',' ){
                str =str + a.charAt(i);
            }else if(a.charAt(i) == ','){
                b [count] = getNum(str);
                str = "";
                count++;
            }
        }
        b [count] = getNum(str);
        return b;
    }

    //store data in the format of two-dimensional array
    int[][] getNumArray (Scanner input,int totalNum, int n){
        int[][] array = new int[totalNum][n];
        for (int i = 0; i < totalNum; i++) {
            String str1 = input.nextLine();
            array[i] = getInteger(str1, n);
        }
        return array;
    }

}
