package modelviewer;

/** These are events (they have a different name because there's way too many namespaces called Event*).  Handles things
 * like user input.  Allows us to decouple systems from each other using the Command pattern.
 * It's tempting to do clever visitor-pattern things with these rather than ugly instanceof checks, but bear in mind that
 * this will hugely reduce the reusablity of those components.  The whole point of this is to decouple complex systems.
 */

interface BlipHandler {
    void handle(Blip blip);
}

interface Blip {
}

