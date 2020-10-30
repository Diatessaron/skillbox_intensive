package soundrecorder;

import com.dropbox.core.v2.DbxClientV2;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaSoundRecorder {
    private final DbxClientV2 client;
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private final AudioFormat format;
    private final DataLine.Info info;
    private TargetDataLine line;

    public JavaSoundRecorder(DbxClientV2 client) {
        this.client = client;
        format = getAudioFormat();
        info = new DataLine.Info(TargetDataLine.class, format);
    }

    public void recordAudio(int milliseconds) {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".wav";
        String filePath = "C:/Users/User/Desktop/" + fileName;
        File file = new File(filePath);
        start(file);
        stop(file, milliseconds);
    }

    private void start(File file) {
        Thread thread = new Thread(() -> {
            try {
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Line not supported");
                    System.exit(0);
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                AudioInputStream ais = new AudioInputStream(line);
                AudioSystem.write(ais, fileType, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
    }

    private void stop(File file, int milliseconds) {
        Thread thread = new Thread(() -> {
            try(InputStream in = new FileInputStream(file.getAbsoluteFile())){
                Thread.sleep(milliseconds);
                line.stop();
                line.close();
                recordAudio(milliseconds);

                client.files().uploadBuilder("/" + file.getName())
                      .uploadAndFinish(in);

                Files.delete(Paths.get(file.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 24000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;

        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }
}
