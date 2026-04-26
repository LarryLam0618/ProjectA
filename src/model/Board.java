package model;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents the game board with 40 slots.
 * Loads slot configuration from external board.csv file (or InputStream)
 * and provides methods for slot retrieval and consecutive land ownership
 * rent multiplier calculation.
 */
public class Board {
    private Slot[] slots;

    /**
     *
     */
    public Board() {
        this.slots = new Slot[0];
    }

    /**
     *
     * @param path
     */
    public void loadFromFile(String path) {
        try {
            File file = new File(path);
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            loadFromLines(lines);
        } catch (IOException | RuntimeException ex) {
            throw new IllegalArgumentException("Cannot load board file: " + path, ex);
        }
    }

    /**
     *
     * @param inputStream
     */
    public void loadFromStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Board input stream is null.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            loadFromLines(lines);
        } catch (IOException | RuntimeException ex) {
            throw new IllegalArgumentException("Cannot load board from stream.", ex);
        }
    }

    /**
     *
     * @param pos
     * @return
     */
    public Slot getSlot(int pos) {
        if (slots.length == 0) {
            throw new IllegalStateException("Board is empty. Call loadFromFile() first.");
        }
        int index = ((pos % slots.length) + slots.length) % slots.length;
        return slots[index];
    }

    /**
     *
     * @param land
     * @param owner
     * @return
     */
    public int getSideRentMultiplier(LandSlot land, Player owner) {
        if (land == null || owner == null) {
            return 1;
        }

        if (land.getOwner() != owner) {
            return 1;
        }

        String group = land.getGroup();
        if (group == null || group.isBlank()) {
            return 1;
        }

        int ownedInGroup = 0;
        for (Slot slot : slots) {
            if (!(slot instanceof LandSlot)) {
                continue;
            }

            LandSlot otherLand = (LandSlot) slot;
            if (group.equalsIgnoreCase(otherLand.getGroup()) && otherLand.getOwner() == owner) {
                ownedInGroup++;
            }
        }

        if (ownedInGroup >= 3) {
            return 3;
        }
        if (ownedInGroup == 2) {
            return 2;
        }
        return 1;
    }

    /**
     *
     * @return
     */
    public Slot[] getSlots() {
        return slots;
    }

    /**
     *
     * @param owner
     * @return
     */
    public int countOwnedLand(Player owner) {
        if (owner == null) {
            return 0;
        }

        int count = 0;
        for (Slot slot : slots) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                if (land.getOwner() == owner) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     *
     * @param buyer
     * @return
     */
    public List<LandSlot> getTradableLandsFor(Player buyer) {
        List<LandSlot> tradable = new ArrayList<>();
        if (buyer == null) {
            return tradable;
        }

        for (Slot slot : slots) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                Player owner = land.getOwner();
                if (owner != null && owner != buyer && owner.isActive()) {
                    tradable.add(land);
                }
            }
        }
        return tradable;
    }

    /**
     *
     * @param owner
     * @return
     */
    public List<LandSlot> getOwnedLandsFor(Player owner) {
        List<LandSlot> owned = new ArrayList<>();
        if (owner == null) {
            return owned;
        }

        for (Slot slot : slots) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                if (land.getOwner() == owner) {
                    owned.add(land);
                }
            }
        }
        return owned;
    }

    /**
     *
     * @param owner
     */
    public void releaseLandsOwnedBy(Player owner) {
        if (owner == null) {
            return;
        }
        for (Slot slot : slots) {
            if (slot instanceof LandSlot) {
                LandSlot land = (LandSlot) slot;
                if (land.getOwner() == owner) {
                    land.setOwner(null);
                }
            }
        }
    }

    private void loadFromLines(List<String> lines) {
        Slot[] loaded = new Slot[Player.BOARD_SIZE];
        int landCount = 0;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 3) {
                throw new IllegalArgumentException("CSV line must define index, type, and name: " + line);
            }

            int index = Integer.parseInt(parts[0].trim());
            String type = parts[1].trim().toLowerCase();
            String name = parts[2].trim();

            if ("go".equals(type)) {
                if (parts.length < 4) {
                    throw new IllegalArgumentException("GO slot must define bonus value in CSV: " + line);
                }
                int bonus = Integer.parseInt(parts[3].trim());
                loaded[index] = new GoSlot(index, name, bonus);
            } else if ("land".equals(type)) {
                int price = Integer.parseInt(parts[3].trim());
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Land slot must define group in CSV: " + line);
                }
                String group = parts[4].trim();
                landCount++;
                loaded[index] = new LandSlot(index, name, price, group);
            } else {
                loaded[index] = new ActionSlot(index, name);
            }
        }

        if (landCount != 22) {
            throw new IllegalArgumentException("Board must define exactly 22 land slots, found: " + landCount);
        }

        for (int i = 0; i < loaded.length; i++) {
            if (loaded[i] == null) {
                throw new IllegalStateException("Missing slot index in CSV: " + i);
            }
        }

        this.slots = loaded;
    }

}
