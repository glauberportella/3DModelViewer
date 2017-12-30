package BasicModels;

// These are events (they have a different name because there's way too many namespaces called Event*).  Handles things
// like user input.  Allows us to decouple systems from each other using the Command pattern.

interface BlipHandler {
    void handle(Blip blip);
}

interface Blip {
}

