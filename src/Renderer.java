import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer {

	private int shaderProgram;
	private int renderMode = GL_FILL;
	private OGLBuffers buffersList;
	private OGLBuffers buffersStrip;
	private GridTopology topology = GridTopology.TRIANGLES;

	// Proměnná pro výběr matematické funkce (0 až 5)
	private int functionMode = 0;

	@Override
	public void init() {
		super.init();
		// Základní barva pozadí (pokud nepoužíváš super.display())
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// Generování hustší mřížky (např. 50x50), aby tělesa vypadala hladce
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

		// Načtení shaderu
		shaderProgram = ShaderUtils.loadProgram("/shaders/start");
	}

	@Override
	public void display() {
		super.display();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(shaderProgram);

		// Společné uniformy
		int timeLoc = glGetUniformLocation(shaderProgram, "time");
		glUniform1f(timeLoc, pass / 50.0f);

		int modeLoc = glGetUniformLocation(shaderProgram, "mode");
		int offsetLoc = glGetUniformLocation(shaderProgram, "offset");

		glPolygonMode(GL_FRONT_AND_BACK, renderMode);

		// --- PRVNÍ TĚLESO (vlevo) ---
		// Toto těleso přepínáš klávesami 1-6
		glUniform1i(modeLoc, functionMode);
		glUniform1f(offsetLoc, -0.6f);
		drawGeometry();

		// --- DRUHÉ TĚLESO (vpravo) ---
		// Zde nastavíme fixně mode 5 (Donut), aby tam byl jako druhý objekt
		glUniform1i(modeLoc, 5);
		glUniform1f(offsetLoc, 0.6f);
		drawGeometry();
	}

	// Pomocná metoda, aby kód nebyl duplicitní
	private void drawGeometry() {
		if (topology == GridTopology.TRIANGLES) {
			if (buffersList != null) buffersList.draw(GL_TRIANGLES, shaderProgram);
		} else {
			if (buffersStrip != null) buffersStrip.draw(GL_TRIANGLE_STRIP, shaderProgram);
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