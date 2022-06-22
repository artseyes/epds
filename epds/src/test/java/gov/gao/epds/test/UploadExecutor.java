package gov.gao.epds.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadExecutor{

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(5);

        String[] fileLocations = new String[10];

        for (int i = 0; i < 10; i++) {

            Runnable worker = new UploadThread(fileLocations[i]);

            executor.execute(worker);
        }
        executor.shutdown();

        while (!executor.isTerminated()) { }

        System.out.println("Finished uploading");
    }
}
