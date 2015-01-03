package tictactoe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tictactoe implements PropertyChangeListener {
    private JFrame frame;
    private Container contentPane;
    
    private JMenuBar menuBar;
    private MenuListener menuListener;
    
    private ButtonListener buttonListener;
    
    private Connection connection;
    
    private ArrayList<JButton> board;  
    private int turnsPlayed;
    private int replaceTile;
    private int player;
    private String[] players;

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        Tictactoe ttt = new Tictactoe();
    }
    
    public Tictactoe() {
        this.initializeGUI();
        this.configureGUI();
        this.createMenu();
        this.createBoard();
        
        this.turnsPlayed = 0;
        this.replaceTile = -1;
        this.player = -1;
        this.players = new String[]{"X", "O"};
    }
    
    private void initializeGUI() {
        this.frame = new JFrame();
        this.contentPane = this.frame.getContentPane();
        
        this.menuBar = new JMenuBar();
        this.menuListener = new MenuListener();
        
        this.buttonListener = new ButtonListener();
        
        this.board = new ArrayList<JButton>();
        for(int i = 0; i < 9; i++) {
            this.board.add(new JButton());
        }
    }
    
    private void configureGUI() {
        this.frame.setVisible(true);
        
        this.frame.pack();
        this.frame.setMinimumSize(new Dimension(200, 200));
        this.frame.setLocationRelativeTo(null);
        
        this.frame.setTitle("Tic-Tac-Toe");
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.frame.setJMenuBar(this.menuBar);
    }
    
    private void createMenu() {
        JMenu menu;
        JMenuItem menuItem;
        
        menu = new JMenu("File");
        String[] menuTexts = new String[]{"Create game", "Join game"};
        for(String menuText : menuTexts) {
            menuItem = new JMenuItem(menuText);
            menuItem.addActionListener(this.menuListener);
            menu.add(menuItem);
        }
        menuBar.add(menu);
    }
    
    private void createBoard() {
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(3, 3));
        for(JButton tile : this.board) {
            tile.setEnabled(false);
            tile.addActionListener(this.buttonListener);
            board.add(tile);
        }

        this.contentPane.setLayout(new GridLayout(1, 1));
        this.contentPane.add(board);
    }
    
    private void createGame(int port) {
        this.connection = new Connection(true, null, port);

        this.connection.addPropertyChangeListener(this);
        
        this.player = 0;
        this.enableTiles();
    }
    
    private void joinGame(String host) {
        int port = Integer.parseInt(host.split(":")[1]);
        host = host.split(":")[0];

        this.connection = new Connection(false, host, port);

        this.connection.addPropertyChangeListener(this);
        
        this.player = 1;
        this.disableTiles();
    }
    
    private void tileClicked(JButton clickedTile) {
        int tileIndex = this.board.indexOf(clickedTile);
        
        if(this.turnsPlayed > 5) {
            if(this.replaceTile != -1) {
                this.disableTiles();
                
                this.connection.send("replace " + this.replaceTile + " " + tileIndex);
                
                this.board.get(this.replaceTile).setText("");
                clickedTile.setText(this.players[this.player]);
                
                this.replaceTile = -1;
                this.endOfTurn();
            } else {
                this.replaceTile = tileIndex;
                this.disableTiles();
                this.enableTiles();
            }
        } else {
            this.disableTiles();
            this.connection.send("place " + tileIndex);
            clickedTile.setText(this.players[this.player]);
            this.endOfTurn();
        }
    }
    
    private void disableTiles() {
        for(JButton tile : this.board) {
            tile.setEnabled(false);
        }
    }
    
    private void enableTiles() {
        for(JButton tile : this.board) {
            if(!tile.getText().isEmpty() && !tile.getText().equals(this.players[this.player])) { // if it's the opponent's tile
            } else if(this.turnsPlayed > 5 && this.replaceTile == -1) { // if we should select a tile to re-place
                if(tile.getText().equals(this.players[this.player])) {
                    tile.setEnabled(true);
                }
            } else { // if we should select an empty tile
                if(tile.getText().isEmpty()) {
                    tile.setEnabled(true);
                }
            }
        }
    }
    
    private void endOfTurn() {
        this.turnsPlayed++;

        int won = -1;
        
        for(int i = 0; i < 3; i++) {
            if(!this.board.get(0 + i * 3).getText().isEmpty() &&
               this.board.get(0 + i * 3).getText().equals(this.board.get(1 + i * 3).getText()) &&
               this.board.get(0 + i * 3).getText().equals(this.board.get(2 + i * 3).getText())) { // horizontal win
                won = Arrays.asList(this.players).indexOf(this.board.get(0 + i * 3).getText());
                break;
            }
            if(!this.board.get(i).getText().isEmpty() &&
               this.board.get(i).getText().equals(this.board.get(i + 3).getText()) &&
               this.board.get(i).getText().equals(this.board.get(i + 6).getText())) { // vertical win
                won = Arrays.asList(this.players).indexOf(this.board.get(i).getText());
                break;
            }
        }
        if(!this.board.get(5).getText().isEmpty() &&
           (this.board.get(0).getText().equals(this.board.get(5).getText()) &&
            this.board.get(0).getText().equals(this.board.get(8).getText())) || // diagonal win from top left
           (this.board.get(2).getText().equals(this.board.get(5).getText()) &&
            this.board.get(2).getText().equals(this.board.get(6).getText()))) { // diagonal win from top right
            won = Arrays.asList(this.players).indexOf(this.board.get(5).getText());
        }
        
        if(won != -1) {
            JOptionPane.showMessageDialog(null, (won == this.player ? "You" : "Your opponent") + " won!");
            this.disableTiles();
            this.connection.disconnect();
        }
    }
    
    private void placeOpponentMarker(int tileIndex) {
        this.board.get(tileIndex).setText(this.players[(this.player == 0 ? 1 : 0)]);

        this.endOfTurn();
        this.enableTiles();
    }
    
    private void replaceOpponentMarker(int tileIndex1, int tileIndex2) {
        this.board.get(tileIndex1).setText("");
        this.board.get(tileIndex2).setText(this.players[(this.player == 0 ? 1 : 0)]);

        this.endOfTurn();
        this.enableTiles();
    }
    
    private class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch(e.getActionCommand()) {
                case "Create game":
                    String port = JOptionPane.showInputDialog("Enter port to run server on (default 53000):");
                    if(port != null) {
                        JOptionPane.showMessageDialog(null, "The window will lock until someone joins.");
                        if(port.isEmpty()) {
                            port = "53000";
                        }
                        createGame(Integer.parseInt(port));
                    }
                    break;
                case "Join game":
                    String host = JOptionPane.showInputDialog("Enter host and port to connect to (default 127.0.0.1:53000):");
                    if(host != null) {
                        if(host.isEmpty()) {
                            host = "127.0.0.1:53000";
                        }
                        joinGame(host);
                    }
                    break;
            }
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tileClicked((JButton)e.getSource());
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object obj = evt.getSource();
        String message = (String) evt.getNewValue();
        
        if(obj == this.connection) {
            if(message != null) {
                String[] args = message.split(" ");
                
                if(args[0].equalsIgnoreCase("replace")) {
                    this.replaceOpponentMarker(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                } else if(args[0].equalsIgnoreCase("place")) {
                    this.placeOpponentMarker(Integer.parseInt(args[1]));
                }
            }
        }
    }
}
