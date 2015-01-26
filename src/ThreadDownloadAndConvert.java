import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marius on 26/01/2015.
 */
public class ThreadDownloadAndConvert implements Runnable {

    private List<String> links = new ArrayList<>();


    //threadNumber
    private int threadNumber;

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    //url
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //outputPath
    private String outputPath;

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }





    /**
     * Constructeur
     * @param url : url de la vidéo
     * @param threadNumber : numéro du thread
     */
    public ThreadDownloadAndConvert(String url, String outputPath, int threadNumber){
        setThreadNumber(threadNumber);
        setOutputPath(outputPath);
        setUrl(url);
    }


    @Override
    public void run() {
        File youtubeDl = new File("lib\\youtube-dl.exe");
        File ffmpeg = new File("lib\\ffmpeg.exe");

        File destYoutubeDl = new File(System.getProperty("java.io.tmpdir")+"youtube-dl("+String.valueOf(threadNumber)+").exe");
        File destFfmpeg = new File(System.getProperty("java.io.tmpdir")+"ffmpeg("+String.valueOf(threadNumber)+").exe");
        try {
            System.out.println("copie de youtubeDl ("+threadNumber+")");
            Files.copy(youtubeDl.toPath(), destYoutubeDl.toPath());
            System.out.println("copie de ffmpeg (" + threadNumber + ")");
            Files.copy(ffmpeg.toPath(), destFfmpeg.toPath());
        } catch (IOException e) {
            e.printStackTrace();

        }

        try {
            getAudio(getUrl(),getOutputPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Suppression des fichiers ("+threadNumber+")");
        destYoutubeDl.delete();
        destFfmpeg.delete();
    }

    public void getAudio(String videoURL, String outputPath) throws Exception {
        String cmd = System.getProperty("java.io.tmpdir")+"youtube-dl("+String.valueOf(threadNumber)+").exe";

        Process[] p = new Process[2];
        //p[0] = new ProcessBuilder(cmd, "--get-filename", videoURL).start();

        /*String cmd2 = outputPath;
        cmd2 += "\\";
        cmd2 += "%(title)s.%(ext)s";*/

        System.out.println("Debut du téléchargement du Thread n°"+String.valueOf(getThreadNumber()));
        p[1] = new ProcessBuilder(cmd,
                "-i",
                videoURL
        ).start();

        p[1].waitFor();
        System.out.println("Fin du téléchargement du Thread n°"+String.valueOf(getThreadNumber()));
        //youtube-dl.exe https://www.youtube.com/watch?v=2F6d6crjRyU -x --audio-format "mp3" --audio-quality 0 -o C:\Users\Marius\Music\Youtube\%(title)s.%(ext)s
    }
}
