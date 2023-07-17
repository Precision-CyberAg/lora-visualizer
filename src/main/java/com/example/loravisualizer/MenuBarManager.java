package com.example.loravisualizer;

import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class MenuBarManager {
    private static MenuBar menuBar;
    private static CheckMenuItem liveLogItem;

    public static void setLogMenuItemChecked(boolean newValue) {
        if(liveLogItem!=null)
            liveLogItem.setSelected(newValue);
    }

    public interface MenuBarListener{

        enum eventType{
            FILE_LOAD,
            EXIT,
            LIVE_LOG_WINDOW
        }

        void onFileLoadItemClicked();

        void onExitItemClicked();

        void onLiveLogItemClicked(boolean val);
    }

    private static final ArrayList<MenuBarListener> menuBarListeners = new ArrayList<>();

    public static void addMenuBarListener(MenuBarListener menuBarListener){
        menuBarListeners.add(menuBarListener);
    }

    public static void removeMenuBarListener(MenuBarListener menuBarListener){
        menuBarListeners.remove(menuBarListener);
    }

    private static void handleMenuEvent(MenuBarListener.eventType eventType){
        for(MenuBarListener menuBarListener: menuBarListeners){
            switch (eventType){
                case FILE_LOAD -> menuBarListener.onFileLoadItemClicked();
                case EXIT -> menuBarListener.onExitItemClicked();
                default -> throw new RuntimeException("Invalid data for event handler");
            }

        }
    }

    private static void handleMenuEvent(MenuBarListener.eventType eventType, boolean isSelected){
        for(MenuBarListener menuBarListener: menuBarListeners){
            switch (eventType){
                case LIVE_LOG_WINDOW -> menuBarListener.onLiveLogItemClicked(isSelected);
                default -> throw new RuntimeException("Invalid data for event handler");
            }

        }
    }
    public static MenuBar createMenuBar(){
        if(menuBar == null){
            menuBar = new MenuBar();

            menuBar.setUseSystemMenuBar(true);

            Menu fileMenu = new Menu("File");

            MenuItem fileLoadItem = new MenuItem("Load File");
            fileLoadItem.setOnAction(event -> handleMenuEvent(MenuBarListener.eventType.FILE_LOAD));

            fileMenu.getItems().add(fileLoadItem);


            MenuItem exitItem = new MenuItem("Exit");
            exitItem.setOnAction(event -> handleMenuEvent(MenuBarListener.eventType.EXIT));
            fileMenu.getItems().add(exitItem);

            Menu windowMenu = new Menu("Window");
            liveLogItem = new CheckMenuItem("Log");
            liveLogItem.setOnAction(event -> {
                handleMenuEvent(MenuBarListener.eventType.LIVE_LOG_WINDOW, liveLogItem.isSelected());
            });

            windowMenu.getItems().add(liveLogItem);

            menuBar.getMenus().add(fileMenu);
            menuBar.getMenus().add(windowMenu);
        }
        return menuBar;
    }
}
