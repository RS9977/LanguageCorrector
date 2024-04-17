package GUI;
import java.util.List;
public interface GUIListener {
    String updateFlagsAndLabel(List<Boolean> flags);
    String loadNextSentece();
}