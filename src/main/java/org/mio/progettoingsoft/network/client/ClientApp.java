package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.views.gui.Gui;
import org.mio.progettoingsoft.views.tui.Tui;
import org.mio.progettoingsoft.views.View;

public class ClientApp implements Runnable{
    private final View view;

    public ClientApp(boolean isGui) {
        view = isGui ? new Gui() : new Tui();
    }

    @Override
    public void run(){
        view.run();
    }
}
