package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.view.Tui;
import org.mio.progettoingsoft.view.View;

public class ClientApp implements Runnable{

    private final View view;

    public ClientApp(boolean isGui){
        isGui = false;
        if (isGui)
            view = null;
        else
            view = new Tui();


    }

    @Override
    public void run(){
        view.run();
    }
}
