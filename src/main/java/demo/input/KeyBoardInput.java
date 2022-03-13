package demo.input;

import demo.tests.Trebuchet;
import demo.utils.ColourSettings;
import demo.window.TestBedWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoardInput extends TestbedControls implements KeyListener, ActionListener {
    public KeyBoardInput(TestBedWindow testBedWindow) {
        super(testBedWindow);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (TESTBED.isPaused()) {
                TESTBED.resume();
            } else {
                TESTBED.pause();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_B) {
            if (TESTBED.getWorld().joints.size() == 3 && Trebuchet.active) {
                TESTBED.getWorld().joints.remove(2);
                TestBedWindow.HERTZ = 60;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            loadDemo(currentDemo);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_M) {
            ColourSettings p = TESTBED.getPAINT_SETTINGS();
            p.setDrawText(p.getNotDrawText());
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        loadDemo(event.getActionCommand());
    }
}

