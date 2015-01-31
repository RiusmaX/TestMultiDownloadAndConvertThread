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

    public static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir")+"musicExtractorTemp\\";

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

    //audioOnly
    private boolean audioOnly;
    public boolean isAudioOnly() { return audioOnly; }
    public void setAudioOnly(boolean audioOnly) { this.audioOnly = audioOnly; }


    /**
     * Constructeur
     * @param url : url de la vidéo
     * @param threadNumber : numéro du thread
     */
    public ThreadDownloadAndConvert(String url, String outputPath, int threadNumber, boolean audioOnly){
        setThreadNumber(threadNumber);
        setOutputPath(outputPath);
        setUrl(url);
        setAudioOnly(audioOnly);
    }


    @Override
    public void run() {
        File youtubeDl = new File("lib\\youtube-dl.exe");
        File ffmpeg = new File("lib\\ffmpeg.exe");
        File tempFolder = new File(TEMP_FOLDER);
        tempFolder.mkdirs();
        File destYoutubeDl = new File(TEMP_FOLDER+"youtube-dl("+String.valueOf(getThreadNumber())+").exe");
        File destFfmpeg = new File(TEMP_FOLDER+"ffmpeg("+String.valueOf(getThreadNumber())+").exe");
        try {
            System.out.println("copie de youtubeDl ("+getThreadNumber()+")");
            Files.copy(youtubeDl.toPath(), destYoutubeDl.toPath());
            System.out.println("copie de ffmpeg (" + getThreadNumber() + ")");
            Files.copy(ffmpeg.toPath(), destFfmpeg.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isAudioOnly()){
            //Cas audio seul
            try {
                getAudio(getUrl(),getOutputPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //Cas video
            try {
                getVideo(getUrl(), getOutputPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Suppression des fichiers ("+getThreadNumber()+")");
        destYoutubeDl.delete();
        destFfmpeg.delete();
        tempFolder.delete();
    }

    /**
     * Récupère l'audio de la vidéo et l'enregistre dans le dossier pasé en paramètre
     * @param videoURL : l'url de la vidéo
     * @param outputPath : dossier de sortie
     * @throws Exception : Exception levée si erreur dans youtube-dl.exe ou ffmpeg.exe
     */
    private void getAudio(String videoURL, String outputPath) throws Exception {
        String cmdYoutubeDl = TEMP_FOLDER+"youtube-dl("+String.valueOf(getThreadNumber())+").exe";
        String cmdFfmpeg = TEMP_FOLDER+"ffmpeg("+String.valueOf(getThreadNumber())+").exe";

        Process[] p = new Process[3];

        p[0] = new ProcessBuilder(cmdYoutubeDl,"--get-filename", videoURL).start();
        BufferedReader in = new BufferedReader( new InputStreamReader(p[0].getInputStream()) );

        String finalFileDir = outputPath +"\\"+ in.readLine().replace(".mp4",".mp3");

        String videoFileDirTemp = TEMP_FOLDER +"video_temp"+String.valueOf(getThreadNumber())+".mp4";
        String audioFileDirTemp = TEMP_FOLDER+"audio_temp"+String.valueOf(getThreadNumber())+".mp3";

        System.out.println("Debut du téléchargement de la vidéo du Thread n°"+String.valueOf(getThreadNumber()));
        p[1] = new ProcessBuilder(cmdYoutubeDl,
                "-i",
                videoURL,
                "-o",
                videoFileDirTemp
        ).start();
        printProcessOutput(p[1]);
        p[1].waitFor();
        System.out.println("Fin du téléchargement de la vidéo du Thread n°"+String.valueOf(getThreadNumber()));

        System.out.println("Début de la conversion audio du Thread n°"+String.valueOf(getThreadNumber()));
        p[2] = new ProcessBuilder(cmdFfmpeg,
                "-i",
                videoFileDirTemp,
                audioFileDirTemp
        ).start();
        printProcessOutput(p[2]);
        p[2].waitFor();
        System.out.println("fin de la conversion audio du Thread n°"+String.valueOf(getThreadNumber()));

        System.out.println("Thread n°"+String.valueOf(getThreadNumber())+" Nettoyage...");
        File fvideo = new File(videoFileDirTemp);
        File faudioTemp = new File(audioFileDirTemp);
        if(faudioTemp.exists()){
           faudioTemp.renameTo(new File(finalFileDir));
        }
        if(fvideo.exists()){
            fvideo.delete();
        }
        in.close();
        //youtube-dl.exe https://www.youtube.com/watch?v=2F6d6crjRyU -x --audio-format "mp3" --audio-quality 0 -o C:\Users\Marius\Music\Youtube\%(title)s.%(ext)s
    }

    /**
     * Télécharge la vidéo de l'url passée en parametre vers le dossier également passé en parametre
     * @param videoURL : l'url de la vidéo a télécharger
     * @param outputPath : le dossier de sortie
     * @throws Exception : Si erreur dans youtube-dl.exe
     */
    private void getVideo(String videoURL, String outputPath) throws Exception{
        String cmdYoutubeDl = TEMP_FOLDER+"youtube-dl("+String.valueOf(threadNumber)+").exe";

        Process[] p = new Process[2];

        String finalFileDir = outputPath +"\\"+ "%(title)s.%(ext)s";

        System.out.println("Debut du téléchargement de la vidéo du Thread n°"+String.valueOf(getThreadNumber()));
        p[1] = new ProcessBuilder(cmdYoutubeDl,
                "-i",
                videoURL,
                "-o",
                finalFileDir
        ).start();
        printProcessOutput(p[1]);
        p[1].waitFor();
        System.out.println("Fin du téléchargement de la vidéo du Thread n°"+String.valueOf(getThreadNumber()));
    }

    /**
     * Redirige la sortie de console du processus passé en parametre dans la sortie du logiciel
     * @param p : le processus dont la sortie doit être redirigée
     */
    private void printProcessOutput(Process p){
        BufferedReader in = new BufferedReader( new InputStreamReader(p.getInputStream()));
        String cmdOutput;
        try {
            while ( (cmdOutput = in.readLine()) != null ) { System.out.println(cmdOutput); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
