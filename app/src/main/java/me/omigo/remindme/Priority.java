package me.omigo.remindme;

public enum Priority {
    NORMAL("Zwykłe"), IMPORTANT("Ważne");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
