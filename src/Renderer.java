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

	@Override
	public void init() {
		super.init();
		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

		float[] vertices = GridMeshFactory.generateVertices(20, 20);
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 3)
		};

		int[] indicesList = GridMeshFactory.generateIndicesList(20, 20);
		buffersList = new OGLBuffers(vertices, attributes, indicesList);

		int[] indicesStrip = GridMeshFactory.generateIndicesStrip(20, 20);
		buffersStrip = new OGLBuffers(vertices, attributes, indicesStrip);

		shaderProgram = ShaderUtils.loadProgram("/shaders/start");
	}

	@Override
	public void display() {
		super.display();
		glUseProgram(shaderProgram);
		glPolygonMode(GL_FRONT_AND_BACK, renderMode);

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

					// Přepínání List (klávesa T) vs Strip (klávesa S)
					case GLFW_KEY_T -> {
						topology = GridTopology.TRIANGLES;
					}
					case GLFW_KEY_S -> {
						topology = GridTopology.TRIANGLE_STRIP;
					}

					case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
				}
			}
		}
	};
}