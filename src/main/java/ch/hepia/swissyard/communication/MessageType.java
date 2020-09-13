package ch.hepia.swissyard.communication;

import java.io.Serializable;

/**
 * This enum lists all messageTypes sent by a Place
 */

public enum MessageType implements Serializable{
    TERMINUS,
    LEFT,
    ARRIVED
}