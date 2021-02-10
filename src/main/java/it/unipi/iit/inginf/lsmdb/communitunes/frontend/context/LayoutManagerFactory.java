package it.unipi.iit.inginf.lsmdb.communitunes.frontend.context;

import java.io.IOException;

public class LayoutManagerFactory {

    private static LayoutManager manager = null;

    public static LayoutManager getManager() {
        if(manager == null){
            manager = new LayoutManager();
        }
        return manager;
    }
}
