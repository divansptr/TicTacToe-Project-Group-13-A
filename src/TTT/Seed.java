/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #13
 * 1- 5026231020- Diva Nesia Putri
 * 2- 5026231114- Imanuel Dwi Prasetyo
 * 3- 5026231196- Ni Kadek Adelia Paramita Putri
 */

package TTT;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * This enum is used by:
 * 1. Player: takes value of CROSS or NOUGHT
 * 2. tictactoe.Cell content: takes value of CROSS, NOUGHT, or NO_SEED.
 *
 * Ideally, we should define two enums with inheritance, which is,
 *  however, not supported.
 */
public enum Seed {   // to save as "Seed.java"
    CROSS("X", "images/cat.png"),   // displayName, imageFilename
    NOUGHT("O", "images/dog.png"),
    NO_SEED(" ", null);

    // Private variables
    private String displayName;
    private Image img = null;

    // Constructor (must be private)
    private Seed(String name, String imageFilename) {
        this.displayName = name;

        if (imageFilename != null) {
            URL imgURL = getClass().getClassLoader().getResource(imageFilename);
            ImageIcon icon = null;
            if (imgURL != null) {
                icon = new ImageIcon(imgURL);
                //System.out.println(icon);  // debugging
            } else {
                System.err.println("Couldn't find file " + imageFilename);
            }
            img = icon.getImage();
        }
    }

    // Public getters
    public String getDisplayName() {
        return displayName;
    }
    public Image getImage() {
        return img;
    }
}
