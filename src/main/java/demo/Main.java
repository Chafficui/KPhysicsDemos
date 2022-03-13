package demo;

import demo.window.TestBedWindow;

public class Main {
    public static void main(String[] args) {
        TestBedWindow demoWindow = new TestBedWindow(true);
        TestBedWindow.showWindow(demoWindow, "KPhysics Demo", 1280,720);
        demoWindow.startThread();
    }
}