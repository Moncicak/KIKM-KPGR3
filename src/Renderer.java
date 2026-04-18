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

	private int functionMode = 0; // 0 až 5 pro matematické funkce
	private int debugMode = 0;    // 0: barva, 1: pozice, 2: normály, 3: UV, 4: hloubka

	@Override
	public void init() {
		super.init();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_DEPTH_TEST); // Zapnutí hloubkového testu (nutné pro 3D)
		glDisable(GL_CULL_FACE); // Aby byly vidět obě strany ploch

		int m = 50;
		int n = 50;

		float[] vertices = GridMeshFactory.generateVertices(m, n);
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 3)
		};

		int[] indicesList = GridMeshFactory.generateIndicesList(m, n);
		buffersList = new OGLBuffers(vertices, attributes, indicesList);

		int[] indicesStrip = GridMeshFactory.generateIndicesStrip(m, n);
		buffersStrip = new OGLBuffers(vertices, attributes, indicesStrip);

		try {
			// Načtení externího modelu (např. konvice nebo krychle)
			staticBody = new OGLModelOBJ("/obj/StaticBody.obj");
		} catch (Exception e) {
			System.err.println("Nepodařilo se načíst model StaticBody.obj!");
		}

		paramProgram = ShaderUtils.loadProgram("/shaders/start");
		modelProgram = ShaderUtils.loadProgram("/shaders/static");
	}

	@Override
	public void display() {
		pass++;
		glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		drawProceduralBody();
		drawStaticBody();
	}

	private void drawProceduralBody() {
		glUseProgram(paramProgram);

		// Odeslání společných parametrů
		glUniform1f(glGetUniformLocation(paramProgram, "time"), pass / 50.0f);
		glUniform1i(glGetUniformLocation(paramProgram, "mode"), functionMode);
		glUniform1i(glGetUniformLocation(paramProgram, "debugMode"), debugMode);
		glUniform1f(glGetUniformLocation(paramProgram, "offset"), -0.75f);

		glPolygonMode(GL_FRONT_AND_BACK, renderMode);
		drawGeometry();

		glUseProgram(0);
	}

	private void drawStaticBody() {
		if (staticBody == null || staticBody.getBuffers() == null) return;

		glUseProgram(modelProgram);

		// Matice modelu pro statické těleso (posun vpravo)
		Mat4 model = new Mat4Scale(0.35).mul(new Mat4Transl(0.85, -0.15, 0.0));
		glUniformMatrix4fv(glGetUniformLocation(modelProgram, "uModel"), false, ToFloatArray.convert(model));

		// Debug mode pro statické těleso
		glUniform1i(glGetUniformLocation(modelProgram, "debugMode"), debugMode);
		glUniform3f(glGetUniformLocation(modelProgram, "uColor"), 0.85f, 0.75f, 0.25f);

		staticBody.getBuffers().draw(staticBody.getTopology(), modelProgram);
		glUseProgram(0);
	}

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
					// Režimy zobrazení
					case GLFW_KEY_P -> renderMode = GL_POINT;
					case GLFW_KEY_L -> renderMode = GL_LINE;
					case GLFW_KEY_F -> renderMode = GL_FILL;

					// Topologie
					case GLFW_KEY_T -> topology = GridTopology.TRIANGLES;
					case GLFW_KEY_S -> topology = GridTopology.TRIANGLE_STRIP;

					// Matematické funkce (levé těleso)
					case GLFW_KEY_1 -> functionMode = 0;
					case GLFW_KEY_2 -> functionMode = 1;
					case GLFW_KEY_3 -> functionMode = 2;
					case GLFW_KEY_4 -> functionMode = 3;
					case GLFW_KEY_5 -> functionMode = 4;
					case GLFW_KEY_6 -> functionMode = 5;

					// PŘEPÍNÁNÍ DEBUG REŽIMŮ (Zadání bod 3)
					case GLFW_KEY_M -> {
						debugMode = (debugMode + 1) % 5;
						System.out.println("Debug mode: " + debugMode);
					}

					case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
				}
			}
		}
	};
}