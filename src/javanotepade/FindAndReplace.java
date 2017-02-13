package javanotepade;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class FindAndReplace extends JDialog implements ActionListener {

    boolean foundOne, isReplace;
    JTextField searchText, replaceText;
    JCheckBox cbCase, cbWhole;
    JRadioButton up, down;
    JLabel statusInfo;
    JFrame owner;
    JPanel north, center, south;

    public FindAndReplace(JFrame owner, boolean isReplace) {
        super(owner, true);
        this.isReplace = isReplace;

        north = new JPanel();
        center = new JPanel();
        south = new JPanel();

        if (isReplace) {
            setTitle("Find And Replace");
            setReplacePanel(north);
        } else {
            setTitle("Find");
            setFindPanel(north);
        }
        addComponent(center);
        statusInfo = new JLabel("Status Info:");
        south.add(statusInfo);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dispose();
            }

        });
        getContentPane().add(north, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        int x = (owner.getWidth() * 3 / 5) - (getWidth() / 2);
        int y = (owner.getHeight() * 3 / 5) - (getHeight() / 2);
        setLocation(x, y);
        setVisible(true);
    }

    private void setFindPanel(JPanel north) {
        final JButton NEXT = new JButton("Find Next");
        NEXT.addActionListener(this);
        NEXT.setEnabled(false);

        searchText = new JTextField(20);
        searchText.addActionListener(this);
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                boolean state = (searchText.getDocument().getLength()) > 0;
                NEXT.setEnabled(state);
                foundOne = false;
            }

        });
        if (searchText.getText().length() > 0) {
            NEXT.setEnabled(true);
        }
        north.add(new JLabel("Find Word:"));
        north.add(searchText);
        north.add(NEXT);
    }

    private void setReplacePanel(JPanel north) {
        GridBagLayout grid = new GridBagLayout();
        north.setLayout(grid);
        GridBagConstraints con = new GridBagConstraints();
        con.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblFWord = new JLabel(" Find Word");
        JLabel lblRWord = new JLabel(" Find Word");
        final JButton NEXT = new JButton("Replace Text");
        NEXT.addActionListener(this);
        NEXT.setEnabled(false);
        final JButton REPLACE = new JButton("Replace All");
        REPLACE.addActionListener(this);
        REPLACE.setEnabled(false);
        searchText = new JTextField(20);
        replaceText = new JTextField(20);
        replaceText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                boolean state = (replaceText.getDocument().getLength()) > 0;
                NEXT.setEnabled(state);
                REPLACE.setEnabled(state);
                foundOne = false;
            }

        });
        con.gridx = 0;
        con.gridy = 0;
        grid.setConstraints(lblFWord, con);
        north.add(lblFWord);

        con.gridx = 1;
        con.gridy = 0;
        grid.setConstraints(searchText, con);
        north.add(searchText);

        con.gridx = 2;
        con.gridy = 0;
        grid.setConstraints(NEXT, con);
        north.add(NEXT);

        con.gridx = 0;
        con.gridy = 1;
        grid.setConstraints(lblRWord, con);
        north.add(lblRWord);

        con.gridx = 1;
        con.gridy = 1;
        grid.setConstraints(replaceText, con);
        north.add(replaceText);

        con.gridx = 2;
        con.gridy = 1;
        grid.setConstraints(REPLACE, con);
        north.add(REPLACE);

    }

   

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(searchText) || ae.getSource().equals(replaceText)) {
            validate();
        }
        process();
        if(ae.getActionCommand().equals("Replace All")){
            replaceAll();
        }
    }

    private void addComponent(JPanel center) {
        JPanel east = new JPanel();
        JPanel west = new JPanel();
        center.setLayout(new GridLayout(1, 2));
        east.setLayout(new GridLayout(2, 1));
        west.setLayout(new GridLayout(2, 1));
        cbCase = new JCheckBox("Mach Case", true);
        cbWhole = new JCheckBox("Mach Word", true);
        ButtonGroup group = new ButtonGroup();
        up = new JRadioButton("Search Up", false);
        down = new JRadioButton("Search Down", true);
        group.add(up);
        group.add(down);
        east.add(cbCase);
        east.add(cbWhole);
        east.setBorder(BorderFactory.createTitledBorder("Search Option"));

        west.add(up);
        west.add(down);
        west.setBorder(BorderFactory.createTitledBorder("Search Direction"));
        center.add(east);
        center.add(west);

    }

    private void process() {
        if (isReplace) {
            statusInfo.setText("Replacing " + searchText.getText());
        } else {
            statusInfo.setText("Searching for " + searchText.getText());

        }
        int caret = MNotepade.getArea().getCaretPosition();
        String word = getWord();
        String text = getAllText();
        caret = search(text, word, caret);
        if (caret < 0) {
            endResult(false, 0);
        }
    }

    private void endResult(boolean isReplaceAll, int tally) {
        String message = "";
        if (isReplaceAll) {
            if (tally == 0) {
                message = searchText.getText() + "Not Found";
            } else if (tally == 1) {
                message = "One Change was made to " + searchText.getText();
            } else {
                message = "" + tally + " Changes were made to " + searchText.getText();
            }
        } else {
            String str = "";
            if (isSearchDown()) {
                str = "Search Down";
            } else {
                str = "Search Up";
            }
            if (foundOne && !isReplace) {
                message = "End of " + str + "for " + searchText.getText();
            } else if (foundOne && isReplace) {
                message = "End of Replase " + searchText.getText() + " With " + replaceText.getText();
            }
        }
        statusInfo.setText(message);

    }

    private String getAllText() {
        if (caseNotSelected()) {
            return MNotepade.getArea().getText().toLowerCase();
        }
        return MNotepade.getArea().getText();
    }

    private String getWord() {
        if (caseNotSelected()) {
            return searchText.getText().toLowerCase();
        }
        return searchText.getText();
    }

    private boolean caseNotSelected() {
        return !cbCase.isSelected();
    }

    private int search(String text, String word, int caret) {
        boolean found = false;
        int all = text.length();
        int check = word.length();
        if (isSearchDown()) {
            int add = 0;
            for (int i = caret + 1; i < (all - check); i++) {
                String temp = text.substring(i, (i + check));
                if (temp.equals(word)) {
                    if (wholeWordIsSelected()) {
                        if (checkForWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        }
                    } else {
                        caret = i;
                        found = true;
                        break;
                    }
                }

            }
        } else {
            int add = caret;
            for (int i = caret - 1; i >= check; i--) {
                add--;
                String temp = text.substring((i - check), i);
                if (temp.equals(word)) {
                    if (wholeWordIsSelected()) {
                        if (checkForWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        }
                    } else {
                        caret = i;
                        found = true;
                        break;
                    }
                }
            }
        }
        MNotepade.getArea().setCaretPosition(0);
        if (found) {
            MNotepade.getArea().requestFocus();
            if (isSearchDown()) {
                MNotepade.getArea().select(caret, caret + check);
            } else {
                MNotepade.getArea().select(caret - check, caret);
            }
            //for Replace...
            if (isReplace) {
                String replace = replaceText.getText();
                MNotepade.getArea().replaceSelection(replace);
                if (isSearchDown()) {
                    System.out.println(check);
                    System.out.println(caret);
                    MNotepade.getArea().select(caret, caret + replace.length());

                } else {
                    MNotepade.getArea().select(caret - replace.length(), caret);
                }

            }
            foundOne = true;
            return caret;
        }

        return -1;
    }

    private void replaceAll() {
        String word = searchText.getText();
        String text = MNotepade.getArea().getText();
        String insert = replaceText.getText();
        StringBuffer sb = new StringBuffer(text);
        int diff = insert.length() - word.length();
        int offset = 0;
        int tally = 0;
        for (int i = 0; i < (text.length() - word.length()); i++) {
            String temp = text.substring(i, i + word.length());
            if ((temp.equals(word) && checkForWholeWord(word.length(), text, 0, i))) {
                tally++;
                sb.replace(i + offset, i+offset + word.length(), insert);
                offset += diff;
            }
        }
        MNotepade.getArea().setText(sb.toString());
        endResult(true, tally);
        MNotepade.getArea().setCaretPosition(0);
    }

    private boolean isSearchDown() {
        return down.isSelected();
    }

    private boolean wholeWordIsSelected() {
        return cbWhole.isSelected();
    }

    private boolean checkForWholeWord(int check, String text, int add, int caret) {
        int offSetLeft = (caret + add) - 1;
        int offSetRight = (caret + add) + check;
        if (offSetLeft < 0 || offSetRight > text.length()) {
            return true;
        }
        return ((!Character.isLetterOrDigit(text.charAt(offSetLeft))) && (!Character.isLetterOrDigit(text.charAt(offSetRight))));

    }

}
