package view;

import controller.GameEditorController;
import model.LandSlot;
import model.Player;
import model.Slot;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
/**
 * Hidden Game Editor dialog (Easter Egg).
 * Triggered by Ctrl+Shift+E. Allows testers to modify game state
 * (land ownership, player balance, position, status, etc.) for debugging.
 */
public class GameEditorDialog extends JDialog {
    private final GameEditorController controller;
    private final Runnable onStateChanged;

    private final JComboBox<String> playerCombo;
    private final JSpinner balanceSpinner;
    private final JSpinner positionSpinner;
    private final JCheckBox bankruptBox;

    private final JComboBox<String> landCombo;
    private final JComboBox<String> ownerCombo;
    private final JComboBox<String> allLandOwnerCombo;
    private final List<LandSlot> landSlots;

    private final JComboBox<String> turnCombo;
    private final JSpinner nextRollSpinner;

    private final JList<String> tradeLandList;
    private final JComboBox<String> tradeBuyerCombo;
    private final JSpinner tradePriceSpinner;
    private final List<LandSlot> tradeLandSlots;

    /**
     *
     * @param parent
     * @param controller
     * @param onStateChanged
     */
    public GameEditorDialog(JFrame parent, GameEditorController controller, Runnable onStateChanged) {
        super(parent, "Game Editor", false);
        this.controller = controller;
        this.onStateChanged = onStateChanged;
        this.landSlots = new ArrayList<>();

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Game Editor (Ctrl+Shift+E, Esc)");
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        add(title, BorderLayout.NORTH);

        playerCombo = new JComboBox<>();
        balanceSpinner = new JSpinner(new SpinnerNumberModel(1500, -100000, 1000000, 100));
        positionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 39, 1));
        bankruptBox = new JCheckBox("Bankrupt");

        landCombo = new JComboBox<>();
        ownerCombo = new JComboBox<>();
        allLandOwnerCombo = new JComboBox<>();

        turnCombo = new JComboBox<>();
        nextRollSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        tradeLandList = new JList<>();
        tradeLandList.setVisibleRowCount(8);
        tradeLandList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tradeBuyerCombo = new JComboBox<>();
        tradePriceSpinner = new JSpinner(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 100));
        tradeLandSlots = new ArrayList<>();

        JButton applyPlayerBtn = new JButton("Apply Player");
        JButton applyOwnerBtn = new JButton("Set Owner");
        JButton applyAllOwnersBtn = new JButton("Set All Lands");
        JButton setTurnBtn = new JButton("Set Turn");
        JButton setNextRollBtn = new JButton("Set Next Roll");
        JButton tradeBtn = new JButton("Execute Trade");

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        tabs.addTab("Player", buildPlayerPanel(applyPlayerBtn));
        tabs.addTab("Land", buildLandPanel(applyOwnerBtn, applyAllOwnersBtn));
        tabs.addTab("Turn", buildTurnPanel(setTurnBtn, setNextRollBtn));
        tabs.addTab("Trade", buildTradePanel(tradeBtn));
        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> setVisible(false));
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);

        playerCombo.addActionListener(e -> loadSelectedPlayer());
        landCombo.addActionListener(e -> loadSelectedLandOwner());
        tradeLandList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedTradeLand();
            }
        });

        applyPlayerBtn.addActionListener(e -> {
            int idx = playerCombo.getSelectedIndex();
            controller.editorSetBalance(idx, (Integer) balanceSpinner.getValue());
            controller.editorSetPosition(idx, (Integer) positionSpinner.getValue());
            controller.editorSetBankrupt(idx, bankruptBox.isSelected());
            notifyStateChanged();
        });

        applyOwnerBtn.addActionListener(e -> {
            int landIdx = landCombo.getSelectedIndex();
            int ownerIdx = ownerCombo.getSelectedIndex() - 1;
            if (landIdx >= 0 && landIdx < landSlots.size()) {
                controller.editorSetOwner(landSlots.get(landIdx).getSlotNumber(), ownerIdx);
                notifyStateChanged();
            }
        });

        applyAllOwnersBtn.addActionListener(e -> {
            int ownerIdx = allLandOwnerCombo.getSelectedIndex() - 1;
            controller.editorSetAllLandOwners(ownerIdx);
            refreshFromGame();
            notifyStateChanged();
        });

        setTurnBtn.addActionListener(e -> {
            controller.editorSetTurn(turnCombo.getSelectedIndex());
            notifyStateChanged();
        });

        setNextRollBtn.addActionListener(e -> {
            controller.editorSetNextRoll((Integer) nextRollSpinner.getValue());
            notifyStateChanged();
        });

        tradeBtn.addActionListener(e -> {
            int[] tradeLandIndexes = tradeLandList.getSelectedIndices();
            int buyerIndex = tradeBuyerCombo.getSelectedIndex();
            int price = (Integer) tradePriceSpinner.getValue();

            if (tradeLandIndexes.length == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one owned land for trade.");
                return;
            }
            if (buyerIndex < 0) {
                JOptionPane.showMessageDialog(this, "Please select a buyer.");
                return;
            }

            List<Integer> slotIndexes = new ArrayList<>();
            for (int tradeLandIndex : tradeLandIndexes) {
                if (tradeLandIndex < 0 || tradeLandIndex >= tradeLandSlots.size()) {
                    JOptionPane.showMessageDialog(this, "Invalid land selection.");
                    return;
                }
                slotIndexes.add(tradeLandSlots.get(tradeLandIndex).getSlotNumber());
            }

            String error = controller.editorTradeLands(slotIndexes, buyerIndex, price);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
                return;
            }

            JOptionPane.showMessageDialog(this, "Trade completed for " + slotIndexes.size() + " land(s).");
            refreshFromGame();
            notifyStateChanged();
        });

        refreshFromGame();
        setMinimumSize(new Dimension(460, 460));
        setSize(520, 540);
        setLocationRelativeTo(parent);
    }

    /**
     *
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            refreshFromGame();
        }
        super.setVisible(b);
    }

    private JPanel buildPlayerPanel(JButton applyPlayerBtn) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Player"));

        addRow(panel, 0, "Player", playerCombo);
        addRow(panel, 1, "Balance", balanceSpinner);
        addRow(panel, 2, "Position", positionSpinner);
        addRow(panel, 3, "Status", bankruptBox);
        addButtonRow(panel, 4, applyPlayerBtn);
        return panel;
    }

    private JPanel buildLandPanel(JButton applyOwnerBtn, JButton applyAllOwnersBtn) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Land"));

        addRow(panel, 0, "Land", landCombo);
        addRow(panel, 1, "Owner", ownerCombo);
        addButtonRow(panel, 2, applyOwnerBtn);
        addRow(panel, 3, "All Lands", allLandOwnerCombo);
        addButtonRow(panel, 4, applyAllOwnersBtn);
        return panel;
    }

    private JPanel buildTurnPanel(JButton setTurnBtn, JButton setNextRollBtn) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Turn"));

        addRow(panel, 0, "Current Turn", turnCombo);
        addButtonRow(panel, 1, setTurnBtn);
        addRow(panel, 2, "Next Roll", nextRollSpinner);
        addButtonRow(panel, 3, setNextRollBtn);
        return panel;
    }

    private JPanel buildTradePanel(JButton tradeBtn) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Trade Land"));

        addRow(panel, 0, "Lands", new JScrollPane(tradeLandList));
        addRow(panel, 1, "Buyer", tradeBuyerCombo);
        addRow(panel, 2, "Price Each", tradePriceSpinner);
        addButtonRow(panel, 3, tradeBtn);
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, java.awt.Component field) {
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.gridy = row;
        left.weightx = 0;
        left.fill = GridBagConstraints.NONE;
        left.anchor = GridBagConstraints.WEST;
        left.insets = new Insets(4, 8, 4, 8);
        panel.add(new JLabel(label), left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1;
        right.gridy = row;
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.anchor = GridBagConstraints.WEST;
        right.insets = new Insets(4, 0, 4, 8);
        if (field instanceof JComboBox<?>) {
            field.setPreferredSize(new Dimension(220, 28));
        }
        panel.add(field, right);
    }

    private void addButtonRow(JPanel panel, int row, JButton button) {
        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1;
        right.gridy = row;
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.insets = new Insets(6, 0, 8, 8);
        panel.add(button, right);
    }

    private void refreshFromGame() {
        Player[] players = controller.getPlayers();

        playerCombo.removeAllItems();
        ownerCombo.removeAllItems();
        allLandOwnerCombo.removeAllItems();
        turnCombo.removeAllItems();
        tradeBuyerCombo.removeAllItems();

        ownerCombo.addItem("None");
        allLandOwnerCombo.addItem("None");
        for (Player p : players) {
            playerCombo.addItem(p.getName());
            ownerCombo.addItem(p.getName());
            allLandOwnerCombo.addItem(p.getName());
            turnCombo.addItem(p.getName());
            tradeBuyerCombo.addItem(p.getName());
        }

        landSlots.clear();
        landCombo.removeAllItems();
        for (Slot slot : controller.getSlots()) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                landSlots.add(land);
                landCombo.addItem(land.getSlotNumber() + " - " + land.getName());
            }
        }

        tradeLandSlots.clear();
        List<String> tradeLandLabels = new ArrayList<>();
        for (Slot slot : controller.getSlots()) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                if (land.getOwner() != null) {
                    tradeLandSlots.add(land);
                    tradeLandLabels.add(land.getSlotNumber() + " - " + land.getName() + " (" + land.getOwner().getName() + ")");
                }
            }
        }
        tradeLandList.setListData(tradeLandLabels.toArray(new String[0]));

        int turn = controller.getCurrentTurnIndex();
        if (turn >= 0 && turn < turnCombo.getItemCount()) {
            turnCombo.setSelectedIndex(turn);
        }

        Integer nextRoll = controller.getNextRoll();
        if (nextRoll != null) {
            nextRollSpinner.setValue(nextRoll);
        }

        if (playerCombo.getItemCount() > 0) {
            playerCombo.setSelectedIndex(Math.max(0, turn));
            loadSelectedPlayer();
        }
        if (landCombo.getItemCount() > 0) {
            landCombo.setSelectedIndex(0);
            loadSelectedLandOwner();
        }
        if (!tradeLandSlots.isEmpty()) {
            tradeLandList.setSelectedIndex(0);
            loadSelectedTradeLand();
        }
    }

    private void loadSelectedPlayer() {
        int idx = playerCombo.getSelectedIndex();
        Player[] players = controller.getPlayers();
        if (idx < 0 || idx >= players.length) {
            return;
        }

        Player p = players[idx];
        balanceSpinner.setValue(p.getBalance());
        positionSpinner.setValue(p.getPosition());
        bankruptBox.setSelected(!p.isActive());
    }

    private void loadSelectedLandOwner() {
        int idx = landCombo.getSelectedIndex();
        if (idx < 0 || idx >= landSlots.size()) {
            return;
        }

        LandSlot land = landSlots.get(idx);
        Player owner = land.getOwner();
        if (owner == null) {
            ownerCombo.setSelectedIndex(0);
            return;
        }

        Player[] players = controller.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == owner) {
                ownerCombo.setSelectedIndex(i + 1);
                return;
            }
        }
        ownerCombo.setSelectedIndex(0);
    }

    private void loadSelectedTradeLand() {
        int idx = tradeLandList.getSelectedIndex();
        if (idx < 0 || idx >= tradeLandSlots.size()) {
            return;
        }

        LandSlot land = tradeLandSlots.get(idx);
        tradePriceSpinner.setValue(Math.max(1, land.getPrice()));

        Player owner = land.getOwner();
        if (owner == null) {
            if (tradeBuyerCombo.getItemCount() > 0) {
                tradeBuyerCombo.setSelectedIndex(0);
            }
            return;
        }

        Player[] players = controller.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] != owner) {
                tradeBuyerCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void notifyStateChanged() {
        if (onStateChanged != null) {
            onStateChanged.run();
        }
    }
}
