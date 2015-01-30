import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marius on 26/01/2015.
 */
public class MainClass {

    private static List<String> links = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Ajout des liens");
        links.add("https://www.youtube.com/watch?v=BJxGx2IIIwU");
        links.add("https://www.youtube.com/watch?v=S2bjqrRbNW4");
        links.add("https://www.youtube.com/watch?v=JhAlUuRfKJA");
        links.add("https://www.youtube.com/watch?v=0STb5f-QkCU");
        links.add("https://www.youtube.com/watch?v=VZvARSvqjtI");
        links.add("https://www.youtube.com/watch?v=dNkJHHfgP8I");
        links.add("https://www.youtube.com/watch?v=EaK-F8YHPJk");
        links.add("https://www.youtube.com/watch?v=FVgKy7boViQ");
        links.add("https://www.youtube.com/watch?v=pbYmLk55teE");
        links.add("https://www.youtube.com/watch?v=WTrNsAsjEmY");

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            System.out.println("Ajout du thread " +String.valueOf(i) +" au pool");
            pool.submit(new ThreadDownloadAndConvert(links.get(i),"",i,true));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
