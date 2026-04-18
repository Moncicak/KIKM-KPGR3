import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.glfw.GLFWKeyCallback;
import transforms.Mat4;
import transforms.Mat4Scale;
import transforms.Mat4Transl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer {

	private int paramProgram;
	private int modelProgram;
	private int renderMode = GL_FILL;
	private OGLBuffers buffersList;
	private OGLBuffers buffersStrip;
	private OGLModelOBJ staticBody;
	private GridTopology topology = GridTopology.TRIANGLES;

	// Proměnná pro výběr matematické funkce (0 až 5)
	private int functionMode = 0;

	@Override
	public void init() {
		super.init();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		int m = 50;
		int n = 50;

		float[] vertices = GridMeshFactory.generateVertices(m, n);
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 3)
		};

		// Buffery pro obě topologie
		int[] indicesList = GridMeshFactory.generateIndicesList(m, n);
		buffersList = new OGLBuffers(vertices, attributes, indicesList);

		int[] indicesStrip = GridMeshFactory.generateIndicesStrip(m, n);
		buffersStrip = new OGLBuffers(vertices, attributes, indicesStrip);

		try {
			staticBody = new OGLModelOBJ("/obj/StaticBody.obj");
		} catch (Exception e) {
			throw new RuntimeException("Failed to load static body model", e);
		}

		paramProgram = ShaderUtils.loadProgram("/shaders/start");
		modelProgram = ShaderUtils.loadProgram("/shaders/static");
	}

	@Override
	public void display() {
		pass++;
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		drawProceduralBody();
		drawStaticBody();
	}

	private void drawProceduralBody() {
		glUseProgram(paramProgram);

		int timeLoc = glGetUniformLocation(paramProgram, "time");
		glUniform1f(timeLoc, pass / 50.0f);

		int modeLoc = glGetUniformLocation(paramProgram, "mode");
		int offsetLoc = glGetUniformLocation(paramProgram, "offset");

		glPolygonMode(GL_FRONT_AND_BACK, renderMode);

		glUniform1i(modeLoc, functionMode);
		glUniform1f(offsetLoc, -0.75f);
		drawGeometry();

		glUseProgram(0);
	}

	private void drawStaticBody() {
		if (staticBody == null || staticBody.getBuffers() == null) {
			return;
		}

		glUseProgram(modelProgram);

		Mat4 model = new Mat4Scale(0.35).mul(new Mat4Transl(0.85, -0.15, 0.0));
		int modelLoc = glGetUniformLocation(modelProgram, "uModel");
		glUniformMatrix4fv(modelLoc, false, ToFloatArray.convert(model));

		int colorLoc = glGetUniformLocation(modelProgram, "uColor");
		glUniform3f(colorLoc, 0.85f, 0.75f, 0.25f);

		staticBody.getBuffers().draw(staticBody.getTopology(), modelProgram);
		glUseProgram(0);
	}

	// Pomocná metoda, aby kód nebyl duplicitní
	private void drawGeometry() {
		if (topology == GridTopology.TRIANGLES) {
			if (buffersList != null) buffersList.draw(GL_TRIANGLES, paramProgram);
		} else {
			if (buffersStrip != null) buffersStrip.draw(GL_TRIANGLE_STRIP, paramProgram);
		}
	}

	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (action == GLFW_PRESS) {
				switch (key) {
					// Režimy zobrazení (body, hrany, plochy)
					case GLFW_KEY_P -> renderMode = GL_POINT;
					case GLFW_KEY_L -> renderMode = GL_LINE;
					case GLFW_KEY_F -> renderMode = GL_FILL;

					// Přepínání topologie
					case GLFW_KEY_T -> topology = GridTopology.TRIANGLES;
					case GLFW_KEY_S -> topology = GridTopology.TRIANGLE_STRIP;

					// Výběr tělesa (1 až 6)
					case GLFW_KEY_1 -> functionMode = 0;
					case GLFW_KEY_2 -> functionMode = 1;
					case GLFW_KEY_3 -> functionMode = 2;
					case GLFW_KEY_4 -> functionMode = 3;
					case GLFW_KEY_5 -> functionMode = 4;
					case GLFW_KEY_6 -> functionMode = 5;

					case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
				}
			}
		}
	};
}
