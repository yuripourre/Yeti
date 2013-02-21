package barsan.opengl.rendering;

import java.util.List;

import javax.media.opengl.GL2;

import barsan.opengl.Yeti;
import barsan.opengl.resources.ModelLoader.Face;

public class AnimatedModel extends Model {
	
	public static class Frame {
		// interpolations would also go here in the future
		
		// How long does this frame influence the animation
		public final float duration;
		
		// The actual keyframe
		public final StaticModel model;
		
		public Frame(StaticModel model, float duration) {
			this.model = model;
			this.duration = duration;
		}
	}
	
	private String name;
	private final List<Frame> frames;
		
	public AnimatedModel(String name, List<Frame> frames) {
		assert frames.size() > 0 : "Cannot have an animation with no frames!";
		this.frames = frames;
		
		setPointsPerFace(frames.get(0).model.getPointsPerFace());
	}
	
	public void dispose() {
		// TODO?
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Frame> getFrames() {
		return frames;
	}

	@Override
	public int getArrayLength() {
		assert frames.size() > 0 : "Cannot have an animation with no frames!";
		return frames.get(0).model.getArrayLength();
	}

	@Override
	public void cleanUp(int... indices) {
		// TODO: unify
		GL2 gl = Yeti.get().gl;
		for(int el : indices) {
			if(el >= 0) {
				//gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
				gl.glDisableVertexAttribArray(el);
			}
		}
	}

	/**
	 * Just return the texture coordinates of the first frame. After the system 
	 * is working, this will b)e made so there will only be one set of texture
	 * coords per animation, not per frame.
	 */
	@Override
	public VBO getTexCoords() {
		assert frames.size() > 0 : "Cannot have an animation with no frames!";
		return frames.get(0).model.getTexCoords();
	}
}