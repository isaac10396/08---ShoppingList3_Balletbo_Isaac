package edu.upc.eseiaat.pma.balletbo.isaac.shoppinglist3;

/**
 * Created by Usuario on 23/10/2017.
 */

public class Shoppingitem {
    private String text;
    private boolean checked;

    public Shoppingitem(String text) {
        this.text = text;
        this.checked = false;
    }

    public Shoppingitem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        this.checked = !this.checked;
    }
}
