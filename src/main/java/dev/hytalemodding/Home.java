package dev.hytalemodding;

// One saved home. world is "" for old homes saved before worlds were tracked.
public record Home(String world, double x, double y, double z, float pitch, float yaw, float roll) {}