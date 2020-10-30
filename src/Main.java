import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import soundrecorder.JavaSoundRecorder;

public class Main {
    private static final String ACCESS_TOKEN = "";

    public static void main(String[] args) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        JavaSoundRecorder recorder = new JavaSoundRecorder(client);
        recorder.recordAudio(30000);
    }
}
