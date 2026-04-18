import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.glfw.GLFWKeyCallback;
import transforms.*; // Importujeme vše pro matice

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

	private int functionMode = 0;
	private int debugMode = 0;

	// NOVÉ: Proměnné pro ovládání transformace uživatelem
	private float rotX = 0, rotY = 0;

	@Override
	public void init() {
		super.init();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		int m = 50;
		int n = 50;

		float[] vertices = GridMeshFactory.generateVertices(m, n);
		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 3) };

		buffersList = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesList(m, n));
		buffersStrip = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesStrip(m, n));

		try {
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

		// --- MODELOVACÍ TRANSFORMACE (Zadání bod 4) ---
		// Vytvoříme matici: Rotace podle uživatele (rotX, rotY)
		// + automatická rotace v čase (pass / 100.0)
		// + posun doleva (Transl)
		Mat4 modelMatrix = new Mat4Transl(-0.6, 0, 0)
				.mul(new Mat4RotXYZ(rotX, rotY + (pass / 100.0), 0));

		int modelLoc = glGetUniformLocation(paramProgram, "modelMatrix");
		glUniformMatrix4fv(modelLoc, false, ToFloatArray.convert(modelMatrix));

		glUniform1f(glGetUniformLocation(paramProgram, "time"), pass / 50.0f);
		glUniform1i(glGetUniformLocation(paramProgram, "mode"), functionMode);
		glUniform1i(glGetUniformLocation(paramProgram, "debugMode"), debugMode);

		glPolygonMode(GL_FRONT_AND_BACK, renderMode);
		drawGeometry();
		glUseProgram(0);
	}

	private void drawStaticBody() {
		if (staticBody == null || staticBody.getBuffers() == null) return;
		glUseProgram(modelProgram);

		// Modelová matice pro statické těleso (posun vpravo)
		Mat4 model = new Mat4Transl(0.6, 0, 0).mul(new Mat4Scale(0.35));
		glUniformMatrix4fv(glGetUniformLocation(modelProgram, "uModel"), false, ToFloatArray.convert(model));
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

	protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (action == GLFW_PRESS || action == GLFW_REPEAT) { // REPEAT pro plynulý pohyb
				switch (key) {
					case GLFW_KEY_P -> renderMode = GL_POINT;
					case GLFW_KEY_L -> renderMode = GL_LINE;
					case GLFW_KEY_F -> renderMode = GL_FILL;
					case GLFW_KEY_T -> topology = GridTopology.TRIANGLES;
					case GLFW_KEY_S -> topology = GridTopology.TRIANGLE_STRIP;
					case GLFW_KEY_1 -> functionMode = 0;
					case GLFW_KEY_2 -> functionMode = 1;
					case GLFW_KEY_3 -> functionMode = 2;
					case GLFW_KEY_4 -> functionMode = 3;
					case GLFW_KEY_5 -> functionMode = 4;
					case GLFW_KEY_6 -> functionMode = 5;
					case GLFW_KEY_M -> debugMode = (debugMode + 1) % 5;

					// OVLÁDÁNÍ TRANSFORMACE ŠIPKAMI
					case GLFW_KEY_UP -> rotX += 0.1f;
					case GLFW_KEY_DOWN -> rotX -= 0.1f;
					case GLFW_KEY_LEFT -> rotY -= 0.1f;
					case GLFW_KEY_RIGHT -> rotY += 0.1f;

					case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
				}
			}
		}
	};

	@Override
	public GLFWKeyCallback getKeyCallback() { return keyCallback; }
}