package im.kvalhe.andrew.josm.imagery_used;

import java.util.Map;
import java.util.stream.Collectors;
import org.openstreetmap.josm.actions.UploadAction;
import org.openstreetmap.josm.actions.upload.UploadHook;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.spi.preferences.Config;

public class ImageryUsedPlugin extends Plugin {
  public ImageryUsedPlugin(PluginInformation info) {
    super(info);

    if (Config.getPref().getBoolean("upload.source.obtainautomatically", false))
      Config.getPref().putBoolean("upload.source.obtainautomatically", false);

    UploadAction.registerUploadHook(new ImageryUsedUploadHook());
  }
}

class ImageryUsedUploadHook implements UploadHook {
  @Override
  public void modifyChangesetTags(Map<String, String> tags) {
    String names =
        MainApplication.getLayerManager().getVisibleLayersInZOrder().stream()
            .filter(ImageryLayer.class::isInstance)
            .map(layer -> layer.getChangesetSourceTag())
            .map(name -> name == "Bing" ? "Bing aerial imagery" : name)
            .distinct()
            .collect(Collectors.joining(";"));

    if (names.isEmpty()) tags.remove("imagery_used");
    else tags.put("imagery_used", names);
  }
}
