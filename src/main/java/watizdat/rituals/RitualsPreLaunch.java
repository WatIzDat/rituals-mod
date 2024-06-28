package watizdat.rituals;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class RitualsPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        System.setProperty("devauth.enabled", "true");
    }
}
