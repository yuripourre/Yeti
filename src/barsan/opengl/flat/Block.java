package barsan.opengl.flat;

import barsan.opengl.Yeti;
import barsan.opengl.math.Rectangle;
import barsan.opengl.math.Vector3;
import barsan.opengl.rendering.Cube;

// Floating block you can jump on and stuff
public class Block extends Entity2D {

	public Block(Rectangle bounds) {
		super(bounds, true, false, new Cube(Yeti.get().gl, 1.0f, false));
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		graphics.getTransform().updateScale(physics.bounds.width, physics.bounds.height, 4.0f);
		graphics.getTransform().updateTranslate(new Vector3(
				physics.bounds.x + physics.bounds.width / 2,
				physics.bounds.y + physics.bounds.height, 0.0f));
	}

}
