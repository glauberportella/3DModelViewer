public interface Drawable  {
    default public void update() { }
    default public void draw() { }
    default public void draw(AppParams params) { }

}
