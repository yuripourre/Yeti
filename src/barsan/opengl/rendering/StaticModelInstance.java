package barsan.opengl.rendering;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import barsan.opengl.Yeti;
import barsan.opengl.math.Matrix4Stack;
import barsan.opengl.math.Transform;
import barsan.opengl.rendering.materials.BasicMaterial;
import barsan.opengl.rendering.materials.Material;
import barsan.opengl.rendering.techniques.Technique;

public class StaticModelInstance extends ModelInstance {

	/* pp */ StaticModel model;
	
	List<MaterialGroup> materialGroups = new ArrayList<MaterialGroup>();
	
	public StaticModelInstance(StaticModel model) {
		this(model, new Transform());
	}
	
	public StaticModelInstance(StaticModel model, Material material) {
		this(model, material, new Transform());
	}
	
	public StaticModelInstance(StaticModel model, Transform transform) {
		this.model = model;
		this.localTransform = transform;

		castsShadows = true;
		
		if(model.getDefaultMaterialGroups().size() == 0) {
			Yeti.screwed("The model [" + model.getName() + "] doesn't have "
					+ "any default materials. I can't render it!");
		}
		
		for(MaterialGroup mg : model.getDefaultMaterialGroups()) {
			materialGroups.add(mg.copy());
		}
	}
		
	public StaticModelInstance(StaticModel model, Material material, Transform localTransform) {
		this.model = model;
		this.localTransform = localTransform;

		castsShadows = true;
		
		materialGroups.add(new MaterialGroup(0, model.getArrayLength(), material));
	}
	
	@Override
	public void render(RendererState rendererState, Matrix4Stack transformStack) {
		transformStack.push(localTransform.get());
	
		Material activeMaterial;
		if(rendererState.hasForcedMaterial()) {
			activeMaterial = rendererState.getForcedMaterial();
		} else {
			activeMaterial = materialGroups.get(0).material;
		}
		
		activeMaterial.setup(rendererState, transformStack.result());
	
		int pindex = activeMaterial.getPositionIndex();
		model.getVertices().use(pindex);
	
		if (!activeMaterial.ignoresLights()) {
			int nindex = activeMaterial.getNormalIndex();
			model.getNormals().use(nindex);
			
			int tindex = activeMaterial.getTangentIndex();
			model.getTangents().use(tindex);
			
			int bnindex = activeMaterial.getBiormalIndex();
			model.getBinormals().use(bnindex);
		}
	
		activeMaterial.bindTextureCoodrinates(model);
		
		// This should actually be something like:
		// Batcher.getBatch(activeMaterial).add(this, new ImmutableRS(rendererState));
		// - if we do this, the number of uniform sets will drop dramatically!
		// - challenge - optimizing the rendererstate so that it really only
		// holds the information that influence the corresponding object
		activeMaterial.render(rendererState, model);
		
		// Ya need to call glDisableVertexAttribArray cuz otherwise the
		// fixed pipeline rendering gets messed up, yo!
		// Mild bug ~1.5h 28.11.2012
	
		activeMaterial.unsetBuffers(model);
		activeMaterial.cleanUp(rendererState);
		
		for (Renderable mi : children) {
			mi.render(rendererState, transformStack);
		}
	
		transformStack.pop();
	}
	
	public void techniqueRender(RendererState rs) {
		int pindex = Technique.current.getVertexIndex();
		model.getVertices().use(pindex);
		
		// Maybe set these in the technique (e.g., in the light pass they are
		// definitely not needed)
		int nindex = Technique.current.getNormalIndex();
		int tindex = -1;
		int bindex = -1;
		if(nindex != -1) {
			model.getNormals().use(nindex);
			
			tindex = Technique.current.getTangentIndex();
			model.getTangents().use(tindex);
			bindex = Technique.current.getBinormalIndex();
			model.getBinormals().use(bindex);
		}
	
		int tcindex = Technique.current.getTexCoordIndex();
		if(tcindex != -1) {
			model.getTexcoords().use(tcindex);
		}
		
		for(MaterialGroup sm : materialGroups) {
			Technique.current.loadMaterial(rs, sm.material);
			int ppf = model.getPointsPerFace();
			rs.gl.glDrawArrays(model.getFaceMode(), sm.beginIndex * ppf, sm.length * ppf);
		}
		rs.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		model.cleanUp(pindex, nindex, tindex, bindex, tcindex);
	}

	@Override
	public Material getMaterial() {
		return materialGroups.get(0).material;
	}

	@Override
	public void setMaterial(Material material) {
		assert false : "Currently not working. Don't remove this assert thinking it magically will. Please.";
	}

	@Override
	public StaticModel getModel() {
		return model;
	}

	public void setModel(StaticModel model) {
		this.model = model;
	}
}
