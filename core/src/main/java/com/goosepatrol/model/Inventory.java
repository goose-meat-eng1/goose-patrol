package com.goosepatrol.model;

public class Inventory {
    private int coffeeCount;
    private int breadCount;

    public Inventory() {
        this.coffeeCount = 0;
        this.breadCount = 0;
    }

    public Inventory(int coffeeCount, int breadCount) {
        this.coffeeCount = coffeeCount;
        this.breadCount = breadCount;
    }

    public void addItem(Items item, int count) {
        switch (item) {
            case COFFEE:
                coffeeCount += count;
                break;
            case BREAD:
                breadCount += count;
                break;
        }
    }

    public boolean useItem(Items item) {
        switch (item) {
            case COFFEE:
                if (coffeeCount > 0) {
                    coffeeCount--;
                    return true;
                }
                break;
            case BREAD:
                if (breadCount > 0) {
                    breadCount--;
                    return true;
                }
                break;
        }
        return false;
    }

    public int getItemCount(Items item){
        switch (item) {
            case COFFEE:
                return coffeeCount;
            case BREAD:
                return breadCount;
            default:
                return 0;
        }
    }
}
