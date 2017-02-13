package javanotepade;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class MNotepade extends JFrame {

    static JTextArea mainArea;
    JMenuBar npMenuBar;
    JLabel statusBar;
    JMenu mnuFile, mnuEdit, mnuFormat, mnuHelp;
    JMenuItem itemNew, itemOpen, itemSave, itemSaveAs, itemExit,
            itemCut, itemCopy, itemPaste, itemFontColor,
            itemFind, itemReplce, itemFont;
    JCheckBoxMenuItem wordWrap;
    String fileName;
    JFileChooser jc;
    String fileContent;
    UndoManager undo;
    UndoAction undoAction;
    RedoAction redoAction;
    int fNext = 1;
    public static MNotepade frmMain;
    FontHelper font;

    public MNotepade() {
        initComponent();
        itemSave.addActionListener((ActionEvent ae) -> {
            save();
        });
        itemSaveAs.addActionListener((ActionEvent ae) -> {
            saveAs();
        });
        itemOpen.addActionListener((ActionEvent ae) -> {
            open();
        });
        itemNew.addActionListener((ActionEvent ae) -> {
            openNew();
        });
        itemExit.addActionListener(((ActionEvent ae) -> {
            System.exit(0);
        }));
        itemCut.addActionListener(((ActionEvent ae) -> {
            mainArea.cut();
        }));
        itemCopy.addActionListener(((ActionEvent ae) -> {
            mainArea.copy();
        }));
        itemPaste.addActionListener(((ActionEvent ae) -> {
            mainArea.paste();
        }));
        mainArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent uee) {
                undo.addEdit(uee.getEdit());
                undoAction.update();
                redoAction.update();
            }
        });
        mainArea.setWrapStyleWord(true);
        wordWrap.addActionListener(((ActionEvent ae) -> {
            if (wordWrap.isSelected()) {
                mainArea.setLineWrap(true);
                mainArea.setWrapStyleWord(true);
            } else {
                mainArea.setLineWrap(false);
                mainArea.setWrapStyleWord(false);
            }
        }));
        itemFontColor.addActionListener(((ActionEvent ae) -> {
            Color c = JColorChooser.showDialog(rootPane, "Choose Font Color", Color.yellow);
            mainArea.setForeground(c);
        }));
        itemFind.addActionListener(((ActionEvent ae) -> {
            new FindAndReplace(frmMain, false);
        }));
        itemReplce.addActionListener(((ActionEvent ae) -> {
            new FindAndReplace(frmMain, true);
        }));
        itemFont.addActionListener(((ActionEvent ae) -> {
            font.setVisible(true);
        }));
        font.getOk().addActionListener(((ActionEvent ae) -> {
            mainArea.setFont(font.font());
            font.setVisible(false);
        }));
        font.getCancel().addActionListener(((ActionEvent ae) -> {
            font.setVisible(false);
        }));
    }

    public static JTextArea getArea() {
        return mainArea;
    }

    private void openNew() {
        if (!mainArea.getText().equals("") && !mainArea.getText().equals(fileContent)) {
            if (fileName == null) {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want save the Changes.");
                if (option == 0) {
                    saveAs();
                    clear();
                } else if (option == 2) {

                } else {
                    clear();
                }
            } else {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want save the Changes.");
                if (option == 0) {
                    save();
                    clear();
                } else if (option == 2) {

                } else {
                    clear();
                }
            }
        } else {
            clear();
        }
    }

    private void clear() {
        mainArea.setText(null);
        setTitle("Untitled Notepade");
        fileName = null;
        fileContent = null;
    }

    private void open() {
        try {
            int retval = jc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                mainArea.setText("");
                Reader in = new FileReader(jc.getSelectedFile());
                char[] buff = new char[10000];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    mainArea.append(new String(buff, 0, nch));
                }
            fileName = jc.getSelectedFile().getName();
            setTitle(fileName = jc.getSelectedFile().getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAs() {
        PrintWriter fout = null;
        int retval = -1;
        try {
            retval = jc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {

                if (jc.getSelectedFile().exists()) {
                    int option = JOptionPane.showConfirmDialog(rootPane, "Do you Want to replace file", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                    if (option == 0) {
                        fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
                        String s = mainArea.getText();
                        StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                        while (st.hasMoreElements()) {
                            fout.println(st.nextToken());
                        }
                        JOptionPane.showMessageDialog(rootPane, "File Save");
                        fileContent = mainArea.getText();
                        fileName = jc.getSelectedFile().getName();
                        setTitle(fileName = jc.getSelectedFile().getName());
                    }else{
                        saveAs();
                    }
                } else {
                    fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
                    String s = mainArea.getText();
                    StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                    while (st.hasMoreElements()) {
                        fout.println(st.nextToken());
                    }
                    JOptionPane.showMessageDialog(rootPane, "File Save");
                    fileContent = mainArea.getText();
                    fileName = jc.getSelectedFile().getName();
                    setTitle(fileName = jc.getSelectedFile().getName());
                }
            }
//            String s = mainArea.getText();
//            StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
//            while (st.hasMoreElements()) {
//                fout.println(st.nextToken());
//            }
//            JOptionPane.showMessageDialog(rootPane, "File Save");
//            fileContent = mainArea.getText();
//            fileName = jc.getSelectedFile().getName();
//            setTitle(fileName = jc.getSelectedFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                fout.close();
            }
        }

    }

    private void save() {
        PrintWriter fout = null;
        //int retval = -1;
        try {
            if (fileName == null) {
                saveAs();
            } else {
                fout = new PrintWriter(new FileWriter(fileName));
                String s = mainArea.getText();
                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                while (st.hasMoreElements()) {
                    fout.println(st.nextToken());
                }
                JOptionPane.showMessageDialog(rootPane, "File Save");
                fileContent = mainArea.getText();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                fout.close();
            }
        }

    }

    public void initComponent() {
        jc = new JFileChooser(".");
        mainArea = new JTextArea();
        undo = new UndoManager();
        font = new FontHelper();

        //Border....
        //statusBar = new JLabel("||       Ln 1, Col 1  ", JLabel.RIGHT);
        statusBar = new JLabel("Develop by MMR      ", JLabel.RIGHT);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        getContentPane().add(new JLabel("  "), BorderLayout.EAST);
        getContentPane().add(new JLabel("  "), BorderLayout.WEST);

        ImageIcon iconUndo = new ImageIcon(getClass().getResource("/img/undo.png"));
        ImageIcon iconRedo = new ImageIcon(getClass().getResource("/img/redo.png"));

        undoAction = new UndoAction(iconUndo);
        redoAction = new RedoAction(iconRedo);

        getContentPane().add(mainArea);
        getContentPane().add(new JScrollPane(mainArea), BorderLayout.CENTER);
        setTitle("Untitled Notepade");
        setSize(800, 600);
        //MenuBar..
        npMenuBar = new JMenuBar();
        //Menu...
        mnuFile = new JMenu("File");
        mnuEdit = new JMenu("Edit");
        mnuFormat = new JMenu("Format");
        mnuHelp = new JMenu("Help");
        //Image..
        ImageIcon iconNew = new ImageIcon(getClass().getResource("/img/new2_1.png"));
        ImageIcon iconOpen = new ImageIcon(getClass().getResource("/img/open.png"));
        ImageIcon iconSave = new ImageIcon(getClass().getResource("/img/save.png"));
        ImageIcon iconSaveAs = new ImageIcon(getClass().getResource("/img/saveAs.png"));
        ImageIcon iconExit = new ImageIcon(getClass().getResource("/img/exit.png"));
        ImageIcon iconCut = new ImageIcon(getClass().getResource("/img/cut.png"));
        ImageIcon iconCopy = new ImageIcon(getClass().getResource("/img/copy.png"));
        ImageIcon iconPaste = new ImageIcon(getClass().getResource("/img/paste.png"));
        ImageIcon iconFind = new ImageIcon(getClass().getResource("/img/find.png"));
        ImageIcon iconReplace = new ImageIcon(getClass().getResource("/img/replace.png"));
        ImageIcon iconFont = new ImageIcon(getClass().getResource("/img/font.png"));
        //MeniItem...
        itemNew = new JMenuItem("New", iconNew);
        itemOpen = new JMenuItem("Open", iconOpen);
        itemSave = new JMenuItem("Save", iconSave);
        itemSaveAs = new JMenuItem("Save As", iconSaveAs);
        itemExit = new JMenuItem("Exit", iconExit);
        itemFontColor = new JMenuItem("Font Color");
        itemFont = new JMenuItem("Font", iconFont);

        itemCut = new JMenuItem("Cut", iconCut);
        itemCopy = new JMenuItem("Cut", iconCopy);
        itemPaste = new JMenuItem("Cut", iconPaste);
        itemFind = new JMenuItem("Find", iconFind);
        itemReplce = new JMenuItem("Replace", iconReplace);

        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        //Shorcut...
        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

        itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        //Adding item...
        mnuFile.add(itemNew);
        mnuFile.add(itemOpen);
        mnuFile.add(itemSave);
        mnuFile.add(itemSaveAs);
        mnuFile.addSeparator();
        mnuFile.add(itemExit);

        mnuEdit.add(undoAction);
        mnuEdit.add(redoAction);
        mnuEdit.addSeparator();
        mnuEdit.add(itemCut);
        mnuEdit.add(itemCopy);
        mnuEdit.add(itemPaste);
        mnuEdit.addSeparator();
        mnuEdit.add(itemFind);
        mnuEdit.add(itemReplce);

        mnuFormat.add(wordWrap);
        mnuFormat.add(itemFontColor);
        mnuFormat.add(itemFont);

        //Adding Menu...
        npMenuBar.add(mnuFile);
        npMenuBar.add(mnuEdit);
        npMenuBar.add(mnuFormat);
        npMenuBar.add(mnuHelp);
        //Adding Menubar...
        setJMenuBar(npMenuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    class UndoAction extends AbstractAction {

        public UndoAction(ImageIcon undoIcon) {
            super("Undo", undoIcon);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                undo.undo();
            } catch (CannotUndoException e) {
                e.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, "Undo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }

    }

    class RedoAction extends AbstractAction {

        public RedoAction(ImageIcon RedoIcon) {
            super("Redo", RedoIcon);
            setEnabled(false);
        }

        public RedoAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                undo.redo();
            } catch (CannotUndoException e) {
                e.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, "Redo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }

    }

    public static void main(String[] args) {
        //MNotepade mn = new MNotepade();
        frmMain = new MNotepade();
    }

}
