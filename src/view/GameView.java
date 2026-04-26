package view;

import model.Game;
import model.LandSlot;
import model.Player;
import model.Slot;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * The main graphical user interface for the Monopoly game.
 * Displays the board, player information, game log, and action buttons.
 * Handles UI updates and user interactions.
 */
public class GameView {

    private final JFrame frame;
    private final BoardPanel boardPanel;
    private final JTextArea logArea;
    private final JPanel playersListPanel;
    private final JPanel rightPanel;
    private final JButton rollButton;
    private final JButton buyButton;
    private final JButton tradeButton;
    private final JButton endTurnButton;
    private final LinkedList<String> logLines;
    private boolean winnerScreenShown;

    private Game game;

    /**
     *
     */
    public GameView() {
        frame = new JFrame("Monopoly Game View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        logLines = new LinkedList<>();
        winnerScreenShown = false;

        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setMinimumSize(new Dimension(620, 620));

        rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setPreferredSize(new Dimension(390, 820));
        rightPanel.setMinimumSize(new Dimension(260, 700));
        rightPanel.setBackground(new Color(239, 239, 239));

        JLabel playerTitle = new JLabel("Players");
        playerTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        playerTitle.setBorder(new EmptyBorder(4, 6, 4, 6));

        playersListPanel = new JPanel();
        playersListPanel.setLayout(new BoxLayout(playersListPanel, BoxLayout.Y_AXIS));
        playersListPanel.setBackground(new Color(239, 239, 239));

        JScrollPane playersScrollPane = new JScrollPane(playersListPanel);
        playersScrollPane.setBorder(null);
        playersScrollPane.setBackground(new Color(239, 239, 239));
        playersScrollPane.getViewport().setBackground(new Color(239, 239, 239));
        playersScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel topRight = new JPanel(new BorderLayout(4, 4));
        topRight.setBackground(new Color(239, 239, 239));
        topRight.setPreferredSize(new Dimension(280, 520));
        topRight.add(playerTitle, BorderLayout.NORTH);
        topRight.add(playersScrollPane, BorderLayout.CENTER);

        JLabel logTitle = new JLabel("Game Log");
        logTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel middleRight = new JPanel(new BorderLayout(4, 4));
        middleRight.add(logTitle, BorderLayout.NORTH);

        middleRight.add(logScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        rollButton = new JButton("Roll");
        buyButton = new JButton("Buy");
        tradeButton = new JButton("Trade");
        endTurnButton = new JButton("End Turn");
        Dimension btnSize = new Dimension(145, 50);
        rollButton.setPreferredSize(btnSize);
        buyButton.setPreferredSize(btnSize);
        tradeButton.setPreferredSize(btnSize);
        endTurnButton.setPreferredSize(btnSize);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 12);  
        rollButton.setFont(buttonFont);
        buyButton.setFont(buttonFont);
        tradeButton.setFont(buttonFont);
        endTurnButton.setFont(buttonFont);

        buttonPanel.add(rollButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(tradeButton);
        buttonPanel.add(endTurnButton);
        rollButton.setFocusable(false);
        buyButton.setFocusable(false);
        tradeButton.setFocusable(false);
        endTurnButton.setFocusable(false);
        rollButton.setFocusPainted(false);
        buyButton.setFocusPainted(false);
        tradeButton.setFocusPainted(false);
        endTurnButton.setFocusPainted(false);
        setActionButtonState(false, false, false, false);

        rightPanel.add(topRight, BorderLayout.NORTH);
        rightPanel.add(middleRight, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);

        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        frame.pack();
        frame.setMinimumSize(new Dimension(980, 820));
        frame.setSize(new Dimension(1120, 860));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     *
     * @param onRoll
     * @param onBuy
     */
    public void setActionHandlers(Runnable onRoll, Runnable onBuy) {
        setActionHandlers(onRoll, onBuy, null);
    }

    /**
     *
     * @param canRoll
     * @param canBuy
     * @param canEndTurn
     * @param canTrade
     */
    public void setActionButtonState(boolean canRoll, boolean canBuy, boolean canEndTurn, boolean canTrade) {
        rollButton.setEnabled(canRoll);
        buyButton.setEnabled(canBuy);
        endTurnButton.setEnabled(canEndTurn);
        tradeButton.setEnabled(canTrade);
    }

    /**
     *
     * @param onRoll
     * @param onBuy
     * @param onEndTurn
     */
    public void setActionHandlers(Runnable onRoll, Runnable onBuy, Runnable onEndTurn) {
        setActionHandlers(onRoll, onBuy, onEndTurn, null);
    }

    /**
     *
     * @param onRoll
     * @param onBuy
     * @param onEndTurn
     * @param onTrade
     */
    public void setActionHandlers(Runnable onRoll, Runnable onBuy, Runnable onEndTurn, Runnable onTrade) {
        rollButton.addActionListener(e -> {
            if (onRoll != null) {
                onRoll.run();
            }
        });
        buyButton.addActionListener(e -> {
            if (onBuy != null) {
                onBuy.run();
            }
        });
        endTurnButton.addActionListener(e -> {
            if (onEndTurn != null) {
                onEndTurn.run();
            }
        });
        tradeButton.addActionListener(e -> {
            if (onTrade != null) {
                onTrade.run();
            }
        });
    }

    /**
     *
     * @param onShowEditor
     * @param onHideEditor
     */
    public void setEditorShortcut(Runnable onShowEditor, Runnable onHideEditor) {
        KeyStroke openEditor = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        KeyStroke closeEditor = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        JComponent root = frame.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openEditor, "openEditor");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeEditor, "closeEditor");

        root.getActionMap().put("openEditor", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onShowEditor != null) {
                    onShowEditor.run();
                }
            }
        });
        root.getActionMap().put("closeEditor", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onHideEditor != null) {
                    onHideEditor.run();
                }
            }
        });
    }

    /**
     *
     * @return
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     *
     * @param game
     */
    public void displayBoard(Game game) {
        this.game = game;
        this.winnerScreenShown = false;
        refreshUI();
    }

    /**
     *
     */
    public void showPlayerInfo() {
        // Kept for compatibility with existing method shape.
    }

    /**
     *
     * @param game
     */
    public void showPlayerInfo(Game game) {
        this.game = game;
        Player[] players = game.getPlayers();
        playersListPanel.removeAll();

        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            boolean isCurrent = game.getCurrentTurnIndex() == i;
            JPanel card = buildPlayerCard(p, isCurrent);
            playersListPanel.add(card);
            JPanel spacer = new JPanel();
            spacer.setBackground(new Color(239, 239, 239));
            spacer.setPreferredSize(new Dimension(285, 10));
            spacer.setMaximumSize(new Dimension(285, 10));
            playersListPanel.add(spacer);
        }

        playersListPanel.revalidate();
        playersListPanel.repaint();
        rightPanel.revalidate();
        rightPanel.repaint();
        refreshUI();
    }

    /**
     *
     * @param msg
     */
    public void showMessage(String msg) {
        logLines.add(msg);

        while (logLines.size() > 200) {
            logLines.removeFirst();
        }

        StringBuilder sb = new StringBuilder();
        for (String line : logLines) {
            sb.append(line).append("\n");
        }
        logArea.setText(sb.toString());

        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     *
     * @param currentPlayer
     * @param tradePartners
     * @param buyableLands
     * @param sellableLands
     * @return
     */
    public TradeRequest showTradeDialog(Player currentPlayer, List<Player> tradePartners,
            List<LandSlot> buyableLands, List<LandSlot> sellableLands) {
        boolean canBuy = buyableLands != null && !buyableLands.isEmpty();
        boolean canSell = sellableLands != null && !sellableLands.isEmpty()
                && tradePartners != null && !tradePartners.isEmpty();
        if (!canBuy && !canSell) {
            JOptionPane.showMessageDialog(frame, "No land available to trade.");
            return null;
        }

        JComboBox<String> tradeTypeCombo = new JComboBox<>();
        if (canBuy) {
            tradeTypeCombo.addItem("Buy from another player");
        }
        if (canSell) {
            tradeTypeCombo.addItem("Sell to another player");
        }

        JComboBox<String> landCombo = new JComboBox<>();
        JComboBox<String> partnerCombo = new JComboBox<>();
        JLabel partnerLabel = new JLabel("Other player:");
        JTextArea hint = new JTextArea("Enter agreed trade price:");
        hint.setEditable(false);
        hint.setOpaque(false);
        JTextField priceField = new JTextField("0");

        Runnable reloadChoices = () -> {
            boolean selling = "Sell to another player".equals(tradeTypeCombo.getSelectedItem());
            landCombo.removeAllItems();
            partnerCombo.removeAllItems();
            partnerLabel.setVisible(selling && canSell);
            partnerCombo.setVisible(selling && canSell);

            if (selling) {
                for (LandSlot land : sellableLands) {
                    landCombo.addItem(land.getName());
                }
                for (Player partner : tradePartners) {
                    partnerCombo.addItem(partner.getName());
                }
            } else {
                for (LandSlot land : buyableLands) {
                    landCombo.addItem(land.getName() + " (Owner: " + land.getOwner().getName() + ")");
                }
            }
        };
        reloadChoices.run();
        tradeTypeCombo.addActionListener(e -> reloadChoices.run());

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Trade type:"));
        panel.add(tradeTypeCombo);
        panel.add(new JLabel("Select land:"));
        panel.add(landCombo);
        if (canSell) {
            panel.add(partnerLabel);
            panel.add(partnerCombo);
        }
        panel.add(hint);
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Trade Land", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        int selected = landCombo.getSelectedIndex();
        if (selected < 0) {
            JOptionPane.showMessageDialog(frame, "Select a land first.");
            return null;
        }

        int price;
        try {
            price = Integer.parseInt(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid price.");
            return null;
        }
        if (price <= 0) {
            JOptionPane.showMessageDialog(frame, "Price must be greater than 0.");
            return null;
        }

        boolean selling = "Sell to another player".equals(tradeTypeCombo.getSelectedItem());
        if (selling) {
            int partnerIndex = partnerCombo.getSelectedIndex();
            if (partnerIndex < 0) {
                JOptionPane.showMessageDialog(frame, "Select another player first.");
                return null;
            }

            LandSlot land = sellableLands.get(selected);
            Player buyer = tradePartners.get(partnerIndex);
            return TradeRequest.forSale(land, currentPlayer, buyer, price);
        }

        LandSlot land = buyableLands.get(selected);
        return TradeRequest.forPurchase(land, land.getOwner(), currentPlayer, price);
    }

    /**
     *
     * @param seller
     * @param buyer
     * @param land
     * @param price
     * @return
     */
    public boolean confirmTradeRequest(Player seller, Player buyer, LandSlot land, int price) {
        String message = String.format("%s, player %s wants to buy your \"%s\" for $%d.%nDo you agree to this deal?",
                seller.getName(), buyer.getName(), land.getName(), price);

        int result = JOptionPane.showConfirmDialog(frame, message,
                "Trade Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        return result == JOptionPane.YES_OPTION;
    }

    /**
     *
     */
    public void update() {
        refreshUI();
    }

    /**
     *
     * @param winner
     */
    public void showWinnerScreen(Player winner) {
        if (winner == null || winnerScreenShown) {
            return;
        }

        winnerScreenShown = true;
        String message = String.format(
                "Winner: %s\nBalance: $%d\nLands Owned: %d",
                winner.getName(),
                winner.getBalance(),
                game.getBoard().countOwnedLand(winner)
        );

        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
        System.exit(0);
    }

    /**
     *
     * @param player
     * @param reason
     */
    public void showBankruptcyDialog(Player player, String reason) {
        if (player == null) {
            return;
        }

        String message;
        if (reason == null || reason.isBlank()) {
            message = String.format(
                    "%s is bankrupt.\nThey are removed from the game and all their lands are reset to No Owner.",
                    player.getName()
            );
        } else {
            message = String.format(
                    "%s is bankrupt.\nReason: %s\nThey are removed from the game and all their lands are reset to No Owner.",
                    player.getName(),
                    reason
            );
        }
        JOptionPane.showMessageDialog(frame, message, "Player Bankrupt", JOptionPane.WARNING_MESSAGE);
    }

    /**
     *
     * @param payer
     * @param owner
     * @param land
     * @param amount
     */
    public void showRentPaidDialog(Player payer, Player owner, LandSlot land, int amount) {
        if (payer == null || owner == null || land == null) {
            return;
        }

        String message = String.format(
                "%s paid $%d rent to %s for %s.",
                payer.getName(),
                amount,
                owner.getName(),
                land.getName()
        );
        JOptionPane.showMessageDialog(frame, message, "Rent Paid", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *
     * @param player
     * @param amount
     */
    public void showGoBonusDialog(Player player, int amount) {
        if (player == null) {
            return;
        }

        String message = String.format(
                "%s passed GO and received $%d.\nCurrent Balance: $%d\nRound: %d",
                player.getName(),
                amount,
                player.getBalance(),
                player.getRound()
        );
        JOptionPane.showMessageDialog(frame, message, "GO Bonus", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *
     * @param player
     * @param requiredAmount
     */
    public void showInsufficientFundsDialog(Player player, int requiredAmount) {
        if (player == null) {
            return;
        }

        String message = String.format(
                "%s does not have enough money.\nRequired: $%d\nCurrent Balance: $%d",
                player.getName(),
                requiredAmount,
                player.getBalance()
        );
        JOptionPane.showMessageDialog(frame, message, "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
    }

    private void refreshUI() {
        Runnable run = () -> boardPanel.repaint();
        if (SwingUtilities.isEventDispatchThread()) {
            run.run();
        } else {
            SwingUtilities.invokeLater(run);
        }
    }

    private JPanel buildPlayerCard(Player player, boolean isCurrent) {
        boolean bankrupt = !player.isActive();

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(240, 110));
        card.setMaximumSize(new Dimension(240, 110));
        if (bankrupt) {
            card.setBackground(new Color(230, 230, 230));
        } else {
            card.setBackground(isCurrent ? new Color(205, 216, 228) : new Color(248, 248, 248));
        }

        Border normal = BorderFactory.createLineBorder(new Color(160, 160, 160), 2);
        Border current = BorderFactory.createLineBorder(new Color(220, 90, 90), 3);
        Border bankruptBorder = BorderFactory.createLineBorder(new Color(120, 120, 120), 2);
        if (bankrupt) {
            card.setBorder(bankruptBorder);
        } else {
            card.setBorder(isCurrent ? current : normal);
        }

        Color textColor = bankrupt ? new Color(120, 120, 120) : Color.BLACK;

        // Header row: name on the left, status badge on the right
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(6, 12, 2, 12));

        String nameText = (isCurrent && !bankrupt) ? "> " + player.getName() : player.getName();
        JLabel nameLabel = new JLabel(nameText);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(textColor);

        JLabel statusLabel = new JLabel(bankrupt ? "BANKRUPT" : "Active");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setForeground(bankrupt ? new Color(200, 40, 40) : new Color(25, 135, 84));
        statusLabel.setHorizontalAlignment(JLabel.RIGHT);

        header.add(nameLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        // Body: balance, position, round, land count stacked
        JPanel body = new JPanel(new GridLayout(4, 1));
        body.setOpaque(false);

        JLabel moneyLabel = new JLabel("$" + player.getBalance());
        moneyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        moneyLabel.setBorder(new EmptyBorder(0, 12, 0, 12));
        moneyLabel.setForeground(textColor);

        Slot currentSlot = game.getBoard().getSlot(player.getPosition());
        String posText = player.getPosition() + ": " + currentSlot.getName();

        JLabel posLabel = new JLabel(posText);
        posLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        posLabel.setBorder(new EmptyBorder(0, 12, 0, 12));
        posLabel.setForeground(textColor);

        JLabel roundLabel = new JLabel("Round " + player.getRound());
        roundLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roundLabel.setBorder(new EmptyBorder(0, 12, 0, 12));
        roundLabel.setForeground(textColor);

        JLabel landLabel = new JLabel(game.getBoard().countOwnedLand(player) + " land(s)");
        landLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        landLabel.setBorder(new EmptyBorder(0, 12, 6, 12));
        landLabel.setForeground(textColor);

        body.add(moneyLabel);
        body.add(posLabel);
        body.add(roundLabel);
        body.add(landLabel);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private class BoardPanel extends JPanel {

        private static final int SIDE_BOTTOM = 0;
        private static final int SIDE_LEFT = 1;
        private static final int SIDE_TOP = 2;
        private static final int SIDE_RIGHT = 3;

        private final Color boardColor = new Color(198, 234, 210);
        private final Color landColor = new Color(233, 247, 239);
        private final Color goColor = new Color(255, 216, 107);
        private final Color actionColor = new Color(245, 214, 106);
        private final Color[] tokenColors = {
            new Color(220, 53, 69),
            new Color(13, 110, 253),
            new Color(25, 135, 84),
            new Color(255, 140, 0)
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int boardSize = Math.min(w, h) - 20;
            int x0 = (w - boardSize) / 2;
            int y0 = (h - boardSize) / 2;

            g2.setColor(boardColor);
            g2.fillRect(x0, y0, boardSize, boardSize);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x0, y0, boardSize, boardSize);

            drawSlots(g2, x0, y0, boardSize);
            drawCenterTitle(g2, x0, y0, boardSize);
            drawPlayers(g2, x0, y0, boardSize);
        }

        private void drawSlots(Graphics2D g2, int x0, int y0, int size) {
            if (game == null || game.getBoard().getSlots().length == 0) {
                return;
            }

            Slot[] slots = game.getBoard().getSlots();

            for (int i = 0; i < slots.length; i++) {
                Rectangle rect = getSlotRect(i, x0, y0, size);
                Slot slot = slots[i];
                Color tileColor;
                boolean isLand = slot instanceof LandSlot;
                int side = getSideForSlot(i);

                if (isLand) {
                    LandSlot land = (LandSlot) slot;
                    tileColor = landColor;
                    if (land.getOwner() != null) {
                        tileColor = blend(landColor, getOwnerColor(land.getOwner()), 0.25);
                    }
                } else if (slot.getSlotNumber() == 0) {
                    tileColor = goColor;
                } else {
                    tileColor = actionColor;
                }

                g2.setColor(tileColor);
                g2.fillRect(rect.x, rect.y, rect.width, rect.height);
                g2.setColor(Color.BLACK);
                g2.drawRect(rect.x, rect.y, rect.width, rect.height);

                if (isLand) {
                    LandSlot land = (LandSlot) slot;
                    g2.setColor(getGroupColor(land.getGroup()));
                    drawGroupStrip(g2, side, rect);
                }

                Rectangle textArea = getTextArea(rect, side, isLand);
                String slotLabel = String.valueOf(i);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                int textX = textArea.x;
                int indexY = textArea.y + 12;
                int nameY;
                int priceY = textArea.y + textArea.height - 4;
                g2.drawString(slotLabel, textX, indexY);

                if (isLand) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    int nameStartY = indexY + 14;
                    int nameBottom = priceY - 6;
                    int lineHeight = 13;
                    int maxLines = Math.min(2, Math.max(1, ((nameBottom - nameStartY) / lineHeight) + 1));
                    String[] nameLines = wrapText(g2, slot.getName(), Math.max(20, textArea.width - 2), maxLines);
                    for (int line = 0; line < nameLines.length; line++) {
                        nameY = nameStartY + (line * lineHeight);
                        if (nameY < nameBottom + 2) {
                            g2.drawString(nameLines[line], textX, nameY);
                        }
                    }

                    LandSlot land = (LandSlot) slot;
                    String price = "$" + land.getPrice();
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.drawString(price, textX, priceY);
                } else {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    int nameStartY = indexY + 14;
                    int lineHeight = 13;
                    int maxLines = Math.min(2, Math.max(1, ((textArea.height - 22) / lineHeight)));
                    String[] nameLines = wrapText(g2, slot.getName(), Math.max(20, textArea.width - 2), maxLines);
                    for (int line = 0; line < nameLines.length; line++) {
                        nameY = nameStartY + (line * lineHeight);
                        if (nameY < textArea.y + textArea.height - 2) {
                            g2.drawString(nameLines[line], textX, nameY);
                        }
                    }
                }
            }
        }

        private void drawCenterTitle(Graphics2D g2, int x0, int y0, int size) {
            int corner = getCornerSize(size);
            int innerX = x0 + corner;
            int innerY = y0 + corner;
            int innerSize = size - (corner * 2);

            g2.setColor(new Color(185, 230, 196));
            g2.fillRect(innerX, innerY, innerSize, innerSize);
            g2.setColor(new Color(120, 120, 120));
            g2.drawRect(innerX, innerY, innerSize, innerSize);

            g2.setColor(new Color(200, 36, 36));
            int bannerW = innerSize - 90;
            int bannerH = 70;
            int bannerX = innerX + (innerSize - bannerW) / 2;
            int bannerY = innerY + (innerSize - bannerH) / 2;
            g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);

            String text = "MONOPOLY";
            g2.setColor(Color.WHITE);
            Font titleFont = new Font("SansSerif", Font.BOLD, 44);
            g2.setFont(titleFont);
            FontMetrics fm = g2.getFontMetrics(titleFont);
            int textX = bannerX + (bannerW - fm.stringWidth(text)) / 2;
            int textY = bannerY + ((bannerH - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }

        /**
         * Draws the player 1/2/3/4 tokens on their current slot.
         * Each player is offset to one of the four quadrants of the slot
         * (P1 top-left, P2 top-right, P3 bottom-left, P4 bottom-right) so
         * the circles do not overlap when multiple players share a slot.
         *
         * AI-assisted: token offset and centering math drafted with AI.
         * Reviewed and verified by Leung Laam Kwan on 2026-04-21.
         */
        private void drawPlayers(Graphics2D g2, int x0, int y0, int size) {
            if (game == null) {
                return;
            }

            Player[] players = game.getPlayers();

            for (int i = 0; i < players.length; i++) {
                Player p = players[i];
                if (!p.isActive()) {
                    continue;
                }
                Rectangle rect = getSlotRect(p.getPosition(), x0, y0, size);
                int baseX = rect.x + rect.width / 2;
                int baseY = rect.y + rect.height / 2;

                int dx = (i % 2 == 0) ? -8 : 8;
                int dy = (i < 2) ? -8 : 8;
                int tokenSize = 14;

                g2.setColor(tokenColors[i % tokenColors.length]);
                g2.fillOval(baseX + dx - tokenSize / 2, baseY + dy - tokenSize / 2, tokenSize, tokenSize);
                g2.setColor(Color.BLACK);
                g2.drawOval(baseX + dx - tokenSize / 2, baseY + dy - tokenSize / 2, tokenSize, tokenSize);

                String number = String.valueOf(p.getId());
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int textX = baseX + dx - (fm.stringWidth(number) / 2);
                int textY = baseY + dy + (fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Color.BLACK);
                g2.drawString(number, textX, textY);
            }
        }

        private Rectangle getSlotRect(int slotIndex, int x0, int y0, int size) {
            int corner = getCornerSize(size);
            int sideLength = size - (corner * 2);

            if (slotIndex == 0) {
                return new Rectangle(x0 + size - corner, y0 + size - corner, corner, corner);
            }
            if (slotIndex == 10) {
                return new Rectangle(x0, y0 + size - corner, corner, corner);
            }
            if (slotIndex == 20) {
                return new Rectangle(x0, y0, corner, corner);
            }
            if (slotIndex == 30) {
                return new Rectangle(x0 + size - corner, y0, corner, corner);
            }

            if (slotIndex >= 1 && slotIndex <= 9) {
                int segment = 9 - slotIndex;
                int start = getSegmentOffset(segment, sideLength);
                int end = getSegmentOffset(segment + 1, sideLength);
                return new Rectangle(x0 + corner + start, y0 + size - corner, end - start, corner);
            }

            if (slotIndex >= 11 && slotIndex <= 19) {
                int segment = 19 - slotIndex;
                int start = getSegmentOffset(segment, sideLength);
                int end = getSegmentOffset(segment + 1, sideLength);
                return new Rectangle(x0, y0 + corner + start, corner, end - start);
            }

            if (slotIndex >= 21 && slotIndex <= 29) {
                int segment = slotIndex - 21;
                int start = getSegmentOffset(segment, sideLength);
                int end = getSegmentOffset(segment + 1, sideLength);
                return new Rectangle(x0 + corner + start, y0, end - start, corner);
            }

            int segment = slotIndex - 31;
            int start = getSegmentOffset(segment, sideLength);
            int end = getSegmentOffset(segment + 1, sideLength);
            return new Rectangle(x0 + size - corner, y0 + corner + start, corner, end - start);
        }

        private int getCornerSize(int size) {
            return Math.max(72, size / 6);
        }

        private int getSegmentOffset(int segment, int sideLength) {
            double segmentSize = sideLength / 9.0;
            return (int) Math.round(segment * segmentSize);
        }

        private int getSideForSlot(int slotIndex) {
            if (slotIndex >= 0 && slotIndex <= 10) {
                return SIDE_BOTTOM;
            }
            if (slotIndex >= 11 && slotIndex <= 19) {
                return SIDE_LEFT;
            }
            if (slotIndex >= 20 && slotIndex <= 30) {
                return SIDE_TOP;
            }
            return SIDE_RIGHT;
        }

        private int getStripSize(Rectangle rect) {
            return Math.max(6, Math.min(rect.width, rect.height) / 7);
        }

        /**
         * Draws the colored property-group band along the inner edge of a
         * land slot (the stripe that always faces the centre of the board).
         * The band is placed on the top, bottom, left, or right of the tile
         * depending on which side of the board the slot sits on.
         *
         * AI-assisted: per-side rectangle math drafted with AI.
         * Reviewed and verified by Leung Laam Kwan on 2026-04-21.
         */
        private void drawGroupStrip(Graphics2D g2, int side, Rectangle rect) {
            int strip = getStripSize(rect);
            int inset = 1;

            if (side == SIDE_BOTTOM) {
                g2.fillRect(rect.x + inset, rect.y + inset, rect.width - (inset * 2), strip);
                return;
            }
            if (side == SIDE_LEFT) {
                g2.fillRect(rect.x + rect.width - strip - inset, rect.y + inset, strip, rect.height - (inset * 2));
                return;
            }
            if (side == SIDE_TOP) {
                g2.fillRect(rect.x + inset, rect.y + rect.height - strip - inset, rect.width - (inset * 2), strip);
                return;
            }
            g2.fillRect(rect.x + inset, rect.y + inset, strip, rect.height - (inset * 2));
        }

        private Rectangle getTextArea(Rectangle rect, int side, boolean hasStrip) {
            int padding = 4;
            Rectangle area = new Rectangle(
                    rect.x + padding,
                    rect.y + padding,
                    Math.max(8, rect.width - padding * 2),
                    Math.max(8, rect.height - padding * 2)
            );

            if (!hasStrip) {
                return area;
            }

            int strip = getStripSize(rect) + 1;
            if (side == SIDE_BOTTOM) {
                area.y += strip;
                area.height = Math.max(8, area.height - strip);
            } else if (side == SIDE_LEFT) {
                area.width = Math.max(8, area.width - strip);
            } else if (side == SIDE_TOP) {
                area.height = Math.max(8, area.height - strip);
            } else {
                area.x += strip;
                area.width = Math.max(8, area.width - strip);
            }
            return area;
        }

        private String fitText(Graphics2D g2, String text, int maxWidth) {
            FontMetrics fm = g2.getFontMetrics();
            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }

            String out = text;
            while (out.length() > 0 && fm.stringWidth(out + "...") > maxWidth) {
                out = out.substring(0, out.length() - 1);
            }
            if (out.isEmpty()) {
                return "";
            }
            return out + "...";
        }

        private String[] wrapText(Graphics2D g2, String text, int maxWidth, int maxLines) {
            if (text == null || text.isBlank()) {
                return new String[]{""};
            }

            String[] words = text.trim().split("\\s+");
            ArrayList<String> lines = new ArrayList<>();
            int i = 0;

            while (i < words.length && lines.size() < maxLines) {
                String line = "";
                while (i < words.length) {
                    String candidate;
                    if (line.isEmpty()) {
                        candidate = words[i];
                    } else {
                        candidate = line + " " + words[i];
                    }

                    if (g2.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                        line = candidate;
                        i++;
                    } else {
                        if (line.isEmpty()) {
                            line = fitText(g2, words[i], maxWidth);
                            i++;
                        }
                        break;
                    }
                }
                lines.add(line);
            }

            if (i < words.length && !lines.isEmpty()) {
                StringBuilder remaining = new StringBuilder(lines.get(lines.size() - 1));
                for (int j = i; j < words.length; j++) {
                    remaining.append(" ").append(words[j]);
                }
                lines.set(lines.size() - 1, fitText(g2, remaining.toString(), maxWidth));
            }

            return lines.toArray(new String[0]);
        }

        private Color getOwnerColor(Player owner) {
            int ownerId = owner.getId() - 1;
            if (ownerId >= 0 && ownerId < tokenColors.length) {
                return tokenColors[ownerId];
            }
            return Color.LIGHT_GRAY;
        }

        private Color blend(Color base, Color overlay, double ratio) {
            int r = (int) (base.getRed() * (1 - ratio) + overlay.getRed() * ratio);
            int g = (int) (base.getGreen() * (1 - ratio) + overlay.getGreen() * ratio);
            int b = (int) (base.getBlue() * (1 - ratio) + overlay.getBlue() * ratio);
            return new Color(r, g, b);
        }

        /**
         * Maps a property-group name (e.g. "brown", "lightblue", "pink")
         * to the Monopoly-style colour used for that group's band.
         *
         * AI-assisted: colour palette and group-to-Color mapping drafted with AI.
         * Reviewed and verified by [your name] on 2026-04-26.
         */
        private Color getGroupColor(String group) {
            if (group == null) {
                return new Color(210, 210, 210);
            }

            String key = group.trim().toLowerCase();
            if ("brown".equals(key)) {
                return new Color(139, 87, 42);
            }
            if ("lightblue".equals(key) || "light_blue".equals(key) || "light blue".equals(key)) {
                return new Color(117, 202, 255);
            }
            if ("pink".equals(key)) {
                return new Color(234, 88, 170);
            }
            if ("orange".equals(key)) {
                return new Color(255, 140, 0);
            }
            if ("red".equals(key)) {
                return new Color(223, 52, 52);
            }
            if ("yellow".equals(key)) {
                return new Color(245, 204, 0);
            }
            if ("green".equals(key)) {
                return new Color(38, 166, 91);
            }
            if ("blue".equals(key)) {
                return new Color(57, 73, 171);
            }
            if ("railroad".equals(key)) {
                return new Color(90, 90, 90);
            }
            if ("utility".equals(key)) {
                return new Color(126, 87, 194);
            }
            return new Color(210, 210, 210);
        }
    }

    /**
     *
     */
    public static class TradeRequest {

        private final LandSlot land;
        private final Player seller;
        private final Player buyer;
        private final int price;
        private final boolean selling;

        private TradeRequest(LandSlot land, Player seller, Player buyer, int price, boolean selling) {
            this.land = land;
            this.seller = seller;
            this.buyer = buyer;
            this.price = price;
            this.selling = selling;
        }

        /**
         *
         * @param land
         * @param seller
         * @param buyer
         * @param price
         * @return
         */
        public static TradeRequest forPurchase(LandSlot land, Player seller, Player buyer, int price) {
            return new TradeRequest(land, seller, buyer, price, false);
        }

        /**
         *
         * @param land
         * @param seller
         * @param buyer
         * @param price
         * @return
         */
        public static TradeRequest forSale(LandSlot land, Player seller, Player buyer, int price) {
            return new TradeRequest(land, seller, buyer, price, true);
        }

        /**
         *
         * @return
         */
        public LandSlot getLand() {
            return land;
        }

        /**
         *
         * @return
         */
        public Player getSeller() {
            return seller;
        }

        /**
         *
         * @return
         */
        public Player getBuyer() {
            return buyer;
        }

        /**
         *
         * @return
         */
        public int getPrice() {
            return price;
        }

        /**
         *
         * @return
         */
        public boolean isSelling() {
            return selling;
        }
    }
}
