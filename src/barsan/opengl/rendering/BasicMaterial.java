package barsan.opengl.rendering;

import barsan.opengl.math.MathUtil;
import barsan.opengl.math.Matrix4;
import barsan.opengl.resources.ResourceLoader;
import barsan.opengl.util.Color;

/**
 * Note - Gouraud is pretty much 100% replaceable with Phong. They're the same
 * anyway, so only the actual shader programs differ. The uniforms are the same.
 * @author SiegeDog
 *
 */
public class BasicMaterial extends Material {	
	
	// TODO: flags to ignore certain light types
	// TODO: consistent uniform names to ease automatic material management in the future
	
	static final String GOURAUD_NAME 	= "basic";
	static final String PHONG_NAME 		= "phong";
	static final String PHONG_NAME_FLAT	= "phongFlat";
	
	static final Color blank = new Color(0.0f, 0.0f, 0.0f, 0.0f);
	
	public enum ShadingModel {
		Phong,
		Gouraud
	}
	
	private ShadingModel mode = ShadingModel.Phong;
	
	public BasicMaterial(Color diffuse, Color specular) {
		super(ResourceLoader.shader(PHONG_NAME), diffuse, specular);
	}

	public BasicMaterial() {
		this(Color.WHITE, Color.WHITE);
	}
	
	public void setMode(ShadingModel mode) {
		this.mode = mode;
		if(mode == ShadingModel.Phong) {
			shader = ResourceLoader.shader(PHONG_NAME);
		} else {
			shader = ResourceLoader.shader(GOURAUD_NAME);
		}
	}
	
	public void toggleMode() {
		if(mode == ShadingModel.Phong) {
			setMode(ShadingModel.Gouraud);
		} else {
			setMode(ShadingModel.Phong);
		}
	}

	@Override
	public void setup(RendererState rendererState, Matrix4 modelMatrix) {
		Matrix4 view = rendererState.getCamera().getView();
		Matrix4 projection = rendererState.getCamera().getProjection();
		Matrix4 viewModel = new Matrix4(view).mul(modelMatrix);
		
		// WARNING: A * B * C != A * (B * C) with matrices
		// The following line does not equal projection * viewModel
		Matrix4 MVP = new Matrix4(projection).mul(view).mul(modelMatrix);
		
		// FFFFFFFFFUUUuuu 2 hours wasted 22.11.2012 because I forgot to actually
		// set a shader... :|
		enableShader(rendererState);
		
		shader.setUMatrix4("mvpMatrix", MVP);
		shader.setUMatrix4("mvMatrix", viewModel);
		shader.setUMatrix4("vMatrix", view);
		//shader.setUMatrix4("mMatrix", modelMatrix);
		//shader.setUMatrix4("imvMatrix", view.cpy().inv());
		shader.setUMatrix3("normalMatrix", MathUtil.getNormalTransform(viewModel));
		
		// TODO: implement ARRAYS OF LIGHTS here!
		PointLight light = rendererState.getPointLights().get(0);
		AmbientLight ambient = rendererState.getAmbientLight();
		shader.setUVector3f("vLightPosition", light.getPosition());
		
		shader.setUVector4f("globalAmbient", ambient.getColor().getData());
		
		shader.setUVector4f("lightDiffuse", light.getDiffuse().getData());
		shader.setUVector4f("lightSpecular", light.getSpecular().getData());
		
		shader.setU1f("constantAt", 0.0f);
		shader.setU1f("linearAt", 0.01f);
		shader.setU1f("quadraticAt", 0.000f);
		shader.setU1f("cubicAt", 0);
		
		shader.setUVector4f("matDiffuse", diffuse.getData());
		
		// Texture
		if(texture != null) {
			shader.setU1i("useTexture", 1);
			shader.setU1i("colorMap", 0);
			texture.bind(rendererState.getGl());
		} else {
			shader.setU1i("useTexture", 0);
		}
		
		// Fog
		if(rendererState.getFog() != null) {
			Fog fog = rendererState.getFog();
			shader.setU1i("fogEnabled", 1);
			shader.setU1f("minFogDistance", fog.minDistance);
			shader.setU1f("maxFogDistance", fog.maxDistance);
			shader.setUVector4f("fogColor", fog.color.getData());
		} else {
			shader.setU1i("fogEnabled", 0);
		}
		
		shader.setU1i("shininess", shininess);
	}
}