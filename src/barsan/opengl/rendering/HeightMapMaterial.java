package barsan.opengl.rendering;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import barsan.opengl.math.Matrix4;
import barsan.opengl.resources.ResourceLoader;

import com.jogamp.opengl.util.texture.Texture;

/**
 * @author Andrei B�rsan
 *
 */
public class HeightMapMaterial extends BasicMaterial {

	private float minHeight;
	private float maxHeight;
	private Texture upperTexture;
	
	public HeightMapMaterial(Texture lowerTexture, Texture upperTexture,
			float minHeight, float maxHeight) {
		shininess = 1;
		setTexture(lowerTexture);
		this.upperTexture = upperTexture;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		shader = ResourceLoader.shader("heightMapPhong"); 
	}
	
	@Override
	public void setup(RendererState rendererState, Matrix4 modelMatrix) {
		// Map everything
		super.setup(rendererState, modelMatrix);
		
		shader.setU1f("minHeight", minHeight);
		shader.setU1f("maxHeight", maxHeight);
		
		shader.setU1i("colorMapB", 1);
		
		GL2 gl = rendererState.getGl();
		gl.glActiveTexture(GL.GL_TEXTURE1);
		upperTexture.bind(rendererState.getGl());
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
	}
	
}
