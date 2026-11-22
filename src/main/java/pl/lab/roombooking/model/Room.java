package pl.lab.roombooking.model;

/**
 * Model domenowy reprezentujący salę w systemie.
 * Na kolejnych zajęciach ta klasa stanie się encją bazodanową (@Entity).
 */
public class Room {

    private Long id;
    private String name;
    private String building;
    private int floor;
    private int capacity;
    private boolean hasProjector;
    private boolean available;

    public Room() {
    }

    public Room(Long id, String name, String building, int floor, int capacity, boolean hasProjector, boolean available) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
